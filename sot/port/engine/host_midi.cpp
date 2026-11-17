#include "host_midi.h"

#include <windows.h>
#include <mmsystem.h>

#include <algorithm>
#include <atomic>
#include <chrono>
#include <cmath>
#include <condition_variable>
#include <mutex>
#include <thread>

namespace pophost {

// ---------------------------------------------------------------- Standard MIDI File

namespace {

struct Reader {
    const std::vector<std::uint8_t>& d;
    std::size_t p, end;
    bool ok = true;
    std::uint8_t u8() {
        if (p >= end) { ok = false; return 0; }
        return d[p++];
    }
    std::uint32_t vlq() {
        std::uint32_t v = 0;
        for (int i = 0; i < 4; ++i) {
            std::uint8_t b = u8();
            v = (v << 7) | (b & 0x7f);
            if (!(b & 0x80)) return v;
        }
        ok = false;
        return v;
    }
};

std::uint32_t be(const std::vector<std::uint8_t>& d, std::size_t p, int n) {
    std::uint32_t v = 0;
    for (int i = 0; i < n; ++i) v = (v << 8) | d[p + static_cast<std::size_t>(i)];
    return v;
}

struct TickEvent {
    std::uint32_t tick;
    std::uint32_t order;      // file order, keeps simultaneous events stable
    std::uint32_t msg;        // short message, or 0 for a tempo change
    std::uint32_t tempo;      // microseconds per quarter note when msg == 0
};

} // namespace

bool parseMidi(const std::vector<std::uint8_t>& data, MidiSong& out, std::string& error) {
    out = MidiSong();
    if (data.size() < 14 || be(data, 0, 4) != 0x4d546864u) { error = "not a MIDI file"; return false; }
    const std::uint32_t headerLength = be(data, 4, 4);
    const unsigned format = be(data, 8, 2), tracks = be(data, 10, 2), division = be(data, 12, 2);
    if (format > 1 || division == 0 || (division & 0x8000)) { error = "unsupported MIDI format"; return false; }
    std::vector<TickEvent> events;
    std::uint32_t endTick = 0, order = 0;
    std::size_t p = 8 + static_cast<std::size_t>(headerLength);
    for (unsigned t = 0; t < tracks && p + 8 <= data.size(); ++t) {
        const std::uint32_t length = be(data, p + 4, 4);
        if (be(data, p, 4) != 0x4d54726bu || p + 8 + length > data.size()) { error = "bad MIDI track"; return false; }
        Reader r{data, p + 8, p + 8 + length};
        std::uint32_t tick = 0;
        std::uint8_t status = 0;
        while (r.ok && r.p < r.end) {
            tick += r.vlq();
            std::uint8_t b = r.u8();
            if (b == 0xff) {
                const std::uint8_t type = r.u8();
                const std::uint32_t n = r.vlq();
                if (type == 0x51 && n == 3 && r.p + 3 <= r.end)
                    events.push_back({tick, order++, 0, be(data, r.p, 3)});
                r.p += n;
                if (type == 0x2f) break;
                continue;
            }
            if (b == 0xf0 || b == 0xf7) { r.p += r.vlq(); continue; }
            std::uint8_t first;
            if (b & 0x80) { status = b; first = r.u8(); }
            else { if (!status) { error = "MIDI data without status"; return false; } first = b; }
            const unsigned kind = status >> 4;
            std::uint32_t msg = status | (static_cast<std::uint32_t>(first & 0x7f) << 8);
            if (kind != 0xc && kind != 0xd) msg |= static_cast<std::uint32_t>(r.u8() & 0x7f) << 16;
            events.push_back({tick, order++, msg, 0});
        }
        if (!r.ok) { error = "truncated MIDI track"; return false; }
        endTick = std::max(endTick, tick);
        p += 8 + length;
    }
    std::stable_sort(events.begin(), events.end(), [](const TickEvent& a, const TickEvent& b) { return a.tick < b.tick; });

    // Ticks to milliseconds through the tempo map.
    double tempo = 500000.0, ms = 0;
    std::uint32_t lastTick = 0;
    auto advance = [&](std::uint32_t tick) {
        ms += (tick - lastTick) * tempo / division / 1000.0;
        lastTick = tick;
    };
    for (const TickEvent& e : events) {
        advance(e.tick);
        if (e.msg == 0) { tempo = e.tempo ? e.tempo : 500000.0; continue; }
        out.events.push_back({ms, e.msg});
    }
    advance(std::max(endTick, lastTick));
    out.lengthMs = ms;
    return true;
}

// ---------------------------------------------------------------- Nokia ringing tones

namespace {
struct BitReader {
    const std::vector<std::uint8_t>& d;
    std::size_t bit = 0;
    bool ok = true;
    unsigned get(int n) {
        unsigned v = 0;
        for (int i = 0; i < n; ++i) {
            const std::size_t byte = bit >> 3;
            if (byte >= d.size()) { ok = false; return 0; }
            v = (v << 1) | ((d[byte] >> (7 - (bit & 7))) & 1);
            ++bit;
        }
        return v;
    }
};
} // namespace

bool parseNokiaTone(const std::vector<std::uint8_t>& data, MidiSong& out, std::string& error) {
    out = MidiSong();
    BitReader r{data};
    // <command-part>: a length byte, then 7-bit commands each padded to a byte; 0x25 is
    // "ringing tone programming", 0x1d "sound" (the song follows), 0x0a "unicode".
    const unsigned commands = r.get(8);
    bool sound = false;
    for (unsigned i = 0; i < commands && r.ok; ++i) {
        const unsigned cmd = r.get(7);
        if (cmd == 0x1d) { sound = true; break; }   // song type bits follow immediately
        r.get(1);
    }
    if (!sound || !r.ok) { error = "not a ringing tone"; return false; }
    const unsigned songType = r.get(3);
    if (songType == 1) {                           // basic song: title first
        const unsigned titleLength = r.get(4);
        r.get(8 * titleLength);
    } else if (songType != 2) { error = "unsupported ringing tone song type"; return false; }
    const unsigned sequenceLength = r.get(8);

    static const int kTempo[32] = {25, 28, 31, 35, 40, 45, 50, 56, 63, 70, 80, 90, 100, 112, 125, 140,
                                   160, 180, 200, 225, 250, 285, 320, 355, 400, 450, 500, 565, 635, 715, 800, 900};
    int bpm = 63, scale = 1, style = 0, volume = 7;
    double ms = 0;
    const std::uint32_t channel = 0;
    out.events.push_back({0, 0xc0u | channel | (80u << 8)});              // Lead 1 (square)
    out.events.push_back({0, 0xb0u | channel | (7u << 8) | (100u << 16)}); // channel volume
    struct Note { unsigned value, duration, specifier; };
    std::vector<std::vector<std::pair<unsigned, unsigned>>> patterns(4); // (instruction id, payload) per pattern id
    for (unsigned s = 0; s < sequenceLength && r.ok; ++s) {
        if (r.get(3) != 0) { error = "ringing tone: pattern header expected"; return false; }
        const unsigned patternId = r.get(2);
        const unsigned loop = r.get(4);
        const unsigned count = r.get(8);
        std::vector<std::pair<unsigned, unsigned>> body;
        if (count == 0) {
            body = patterns[patternId];                                     // repeat a pattern defined earlier
        } else {
            for (unsigned k = 0; k < count && r.ok; ++k) {
                const unsigned id = r.get(3);
                switch (id) {
                    case 1: body.emplace_back(id, r.get(9)); break;         // note: value 4, duration 3, specifier 2
                    case 2: body.emplace_back(id, r.get(2)); break;         // scale
                    case 3: body.emplace_back(id, r.get(2)); break;         // style
                    case 4: body.emplace_back(id, r.get(5)); break;         // tempo
                    case 5: body.emplace_back(id, r.get(4)); break;         // volume
                    default: error = "ringing tone: unknown instruction"; return false;
                }
            }
            patterns[patternId] = body;
        }
        const unsigned plays = loop == 15 ? 1 : loop + 1;                    // "infinite" pattern loops play once here
        for (unsigned play = 0; play < plays; ++play)
            for (const auto& ins : body) {
                switch (ins.first) {
                    case 2: scale = static_cast<int>(ins.second); break;
                    case 3: style = static_cast<int>(ins.second); break;
                    case 4: bpm = kTempo[ins.second & 31]; break;
                    case 5: volume = static_cast<int>(ins.second); break;
                    case 1: {
                        const unsigned value = ins.second >> 5, duration = (ins.second >> 2) & 7, spec = ins.second & 3;
                        double length = 4.0 * 60000.0 / bpm / static_cast<double>(1u << std::min(duration, 5u)); // whole note = 4 beats
                        if (spec == 1) length *= 1.5;
                        else if (spec == 2) length *= 1.75;
                        else if (spec == 3) length *= 2.0 / 3.0;
                        if (value >= 1 && value <= 12) {
                            // scale 0..3: the A of the octave is 440, 880, 1760, 3520 Hz
                            const int note = 69 + 12 * scale - 9 + static_cast<int>(value - 1);
                            const int velocity = volume == 0 ? 0 : 40 + volume * 87 / 15;
                            const double sounding = style == 1 ? length : style == 2 ? length * 0.5 : length * 0.9;
                            if (note >= 0 && note < 128 && velocity > 0) {
                                out.events.push_back({ms, 0x90u | channel | (static_cast<std::uint32_t>(note) << 8) | (static_cast<std::uint32_t>(velocity) << 16)});
                                out.events.push_back({ms + sounding, 0x80u | channel | (static_cast<std::uint32_t>(note) << 8)});
                            }
                        }
                        ms += length;
                        break;
                    }
                    default: break;
                }
            }
    }
    std::stable_sort(out.events.begin(), out.events.end(), [](const MidiSong::Event& a, const MidiSong::Event& b) { return a.ms < b.ms; });
    out.lengthMs = ms;
    if (!r.ok && out.events.size() <= 2) { error = "truncated ringing tone"; return false; }
    return true;
}

// ---------------------------------------------------------------- player thread

struct MidiPlayer::Impl {
    using Clock = std::chrono::steady_clock;
    std::thread thread;
    mutable std::mutex mutex;
    std::condition_variable wake;
    bool quit = false;
    // requests from the game thread
    bool requestStop = false;
    std::shared_ptr<const MidiSong> requestSong;
    int requestLoops = 1;
    bool requestVolume = true;
    int volume = 80;
    std::atomic<bool> isPlaying{false};
    std::vector<std::string> log;

    // player thread state
    HMIDIOUT out = nullptr;
    bool openFailed = false;
    std::shared_ptr<const MidiSong> song;
    std::size_t next = 0;
    int loopsLeft = 0;
    Clock::time_point origin;
    int rawVolume[16] = {};
    double scale = 1.0;      // user volume, applied to CC7

    void send(std::uint32_t msg) { if (out) midiOutShortMsg(out, msg); }
    std::uint32_t cc(int channel, int controller, int value) {
        return static_cast<std::uint32_t>(0xb0 | channel) | (static_cast<std::uint32_t>(controller) << 8) | (static_cast<std::uint32_t>(value) << 16);
    }
    int scaled(int raw) const { return std::clamp(static_cast<int>(std::lround(raw * scale)), 0, 127); }
    void silence() {
        for (int c = 0; c < 16; ++c) {
            send(cc(c, 64, 0));     // sustain off
            send(cc(c, 123, 0));    // all notes off
            send(cc(c, 120, 0));    // all sound off
        }
    }
    void resetChannels() {
        for (int c = 0; c < 16; ++c) {
            send(cc(c, 121, 0));    // reset controllers (leaves volume alone)
            send(static_cast<std::uint32_t>(0xc0 | c));     // program 0
            send(static_cast<std::uint32_t>(0xe0 | c) | (0x40u << 16));  // pitch bend centre
            rawVolume[c] = 100;
            send(cc(c, 7, scaled(100)));
            send(cc(c, 11, 127));
        }
    }
    void updateScale(int userVolume) {
        // Amplitude follows CC7 squared, so the user's amplitude percentage becomes its square root.
        scale = std::sqrt(std::clamp(userVolume, 0, 100) / 100.0);
    }
    void dispatch(std::uint32_t msg) {
        if ((msg & 0xf0) == 0xb0 && ((msg >> 8) & 0x7f) == 7) {
            const int c = msg & 0xf;
            rawVolume[c] = (msg >> 16) & 0x7f;
            send(cc(c, 7, scaled(rawVolume[c])));
            return;
        }
        send(msg);
    }

    void run() {
        std::unique_lock<std::mutex> lock(mutex);
        if (!out && !openFailed) {
            lock.unlock();
            MMRESULT r = midiOutOpen(&out, MIDI_MAPPER, 0, 0, CALLBACK_NULL);
            lock.lock();
            if (r != MMSYSERR_NOERROR) {
                out = nullptr;
                openFailed = true;
                log.push_back("music: no MIDI output device (midiOutOpen " + std::to_string(r) + ")");
            }
        }
        while (!quit) {
            if (requestStop) {
                requestStop = false;
                if (song) silence();
                song = nullptr;
            }
            if (requestSong) {
                if (song) silence();
                song = std::move(requestSong);
                requestSong = nullptr;
                loopsLeft = requestLoops;
                next = 0;
                origin = Clock::now();
                updateScale(volume);
                resetChannels();
                requestVolume = false;
            }
            if (requestVolume) {
                requestVolume = false;
                updateScale(volume);
                if (song) for (int c = 0; c < 16; ++c) send(cc(c, 7, scaled(rawVolume[c])));
            }
            Clock::time_point due = Clock::time_point::max();
            if (song) {
                const double elapsed = std::chrono::duration<double, std::milli>(Clock::now() - origin).count();
                while (next < song->events.size() && song->events[next].ms <= elapsed) dispatch(song->events[next++].msg);
                if (next >= song->events.size() && elapsed >= song->lengthMs) {
                    if (loopsLeft < 0 || --loopsLeft > 0) {
                        // Restart from the song's own timeline so loops do not drift.
                        origin += std::chrono::duration_cast<Clock::duration>(std::chrono::duration<double, std::milli>(std::max(song->lengthMs, 1.0)));
                        next = 0;
                        continue;
                    }
                    silence();
                    song = nullptr;
                    isPlaying = false;
                } else {
                    const double at = next < song->events.size() ? song->events[next].ms : song->lengthMs;
                    due = origin + std::chrono::duration_cast<Clock::duration>(std::chrono::duration<double, std::milli>(at));
                }
            }
            if (due == Clock::time_point::max()) wake.wait(lock);
            else wake.wait_until(lock, due);
        }
        if (song) silence();
        if (out) {
            midiOutReset(out);
            midiOutClose(out);
            out = nullptr;
        }
    }
};

MidiPlayer::MidiPlayer() : impl_(new Impl) {
    impl_->thread = std::thread([this] { impl_->run(); });
}

MidiPlayer::~MidiPlayer() {
    {
        std::lock_guard<std::mutex> lock(impl_->mutex);
        impl_->quit = true;
    }
    impl_->wake.notify_all();
    if (impl_->thread.joinable()) impl_->thread.join();
}

void MidiPlayer::play(std::shared_ptr<const MidiSong> song, int loops) {
    if (!song) { stop(); return; }
    {
        std::lock_guard<std::mutex> lock(impl_->mutex);
        impl_->requestSong = std::move(song);
        impl_->requestLoops = loops == 0 ? 1 : loops;
        impl_->requestStop = false;
        impl_->isPlaying = true;
    }
    impl_->wake.notify_all();
}

void MidiPlayer::stop() {
    {
        std::lock_guard<std::mutex> lock(impl_->mutex);
        impl_->requestSong = nullptr;
        impl_->requestStop = true;
        impl_->isPlaying = false;
    }
    impl_->wake.notify_all();
}

bool MidiPlayer::playing() const { return impl_->isPlaying; }

void MidiPlayer::setVolume(int volume) {
    {
        std::lock_guard<std::mutex> lock(impl_->mutex);
        impl_->volume = volume;
        impl_->requestVolume = true;
    }
    impl_->wake.notify_all();
}

std::vector<std::string> MidiPlayer::takeLog() {
    std::lock_guard<std::mutex> lock(impl_->mutex);
    std::vector<std::string> l;
    l.swap(impl_->log);
    return l;
}

} // namespace pophost
