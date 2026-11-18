// Connects the generated engine (pop_gen.h) to the host-facing API. This is the only hand-written
// file that sees the game's own identifiers.
//
// The original Game.run() thread: on its first pass it shows the Gameloft logo (aw = 6), loads
// (Game.i), sleeps one second and starts the title sequence (Game.a); after that every iteration
// calls Game.d (update) and paints, then sleeps the rest of Game.H milliseconds (80 in play, 70 after
// the intro, 0 while the intro text scrolls). tick() is one such iteration; the clock the game reads
// (System.currentTimeMillis) advances by Game.H per tick, so level timers follow the simulation.
#include "engine_api.h"
#include "pop_gen.h"

namespace pop {
namespace {
popapi::Options g_options;
Ref<Image> g_screen;
Ref<Graphics> g_graphics;
Ref<Image> g_interp;
bool g_showInterp = false;
bool g_quit = false;
bool g_started = false;
bool g_booted = false;
int g_bootTicks = 0;        // >0: the one-second pause after loading, in ticks
jlong g_clock = 0;
constexpr jlong kClockStart = 1000000;   // any value; the game only uses differences
// Game.H = 0 (loading, the title) means "no sleep", but a pass of the loop still took time on the
// phone; 20 ms is about what an iteration of drawing cost there.
constexpr jlong kMinIterationMs = 20;
jlong iterationMs() { return Game::H > kMinIterationMs ? Game::H : kMinIterationMs; }
// Game.paint redraws the whole world each tick in play and in the menus over it (aw 0, 1, 3, 17,
// 19; "quit?" from the title, aw 1 with f < 0, draws the title instead). Other screens repaint only
// what changes, so only these are recorded for interpolation.
bool worldState() {
    const jint s = Game::aw;
    return s == 0 || s == 3 || s == 17 || s == 19 || (s == 1 && Game::f >= 0);
}
}

int hostCanvasWidth() { return popapi::kBaseWidth; }
int hostCanvasHeight() { return popapi::kBaseHeight; }
void hostQuit() { g_quit = true; }

void PrinceSOT::destroyApp(jboolean) {
    if (c != nullptr) Game::m_n_V();
}

} // namespace pop

namespace popapi {
using namespace pop;

void configure(const Options& options) {
#ifdef POP_PORT_BUILD
    const bool widthChanged = g_started && PortConfig::screenWidth != options.screenWidth;
    PortConfig::screenWidth = options.screenWidth;
    PortConfig::directTiles = options.interpolation;
    if (widthChanged) PortConfig::widthChanged();
#endif
    if (!options.interpolation) recorderDrop();
    const bool menu = g_started ? g_options.optionsMenu : options.optionsMenu;
    g_options = options;
    g_options.optionsMenu = menu;
}

static void ensureScreen() {
    if (g_screen == nullptr || g_screen->width != g_options.screenWidth) {
        Ref<Image> old = g_screen;
        g_screen = Image::createImage(g_options.screenWidth, kBaseHeight);
        std::fill(g_screen->pixels.begin(), g_screen->pixels.end(), 0xff000000u);
        g_graphics = g_screen->getGraphics();
        // The game repaints only what changes on some screens (the logo, menus over a still
        // picture): carry the old picture over, centred.
        if (old != nullptr) {
            const int dx = (g_screen->width - old->width) / 2;
            for (int y = 0; y < kBaseHeight; ++y)
                for (int x = 0; x < old->width; ++x)
                    if (x + dx >= 0 && x + dx < g_screen->width)
                        g_screen->pixels[static_cast<std::size_t>(y * g_screen->width + x + dx)] = old->pixels[static_cast<std::size_t>(y * old->width + x)];
        }
    }
}

void start(std::int64_t, bool) {
    if (g_started) return;
    g_started = true;
    fontConfigure(12, 14, 16, "Tahoma");
    g_clock = kClockStart;
    System::virtualTime = g_clock;
#ifdef POP_PORT_BUILD
    PortConfig::clinit();
    PortConfig::screenWidth = g_options.screenWidth;
    PortConfig::directTiles = g_options.interpolation;
#endif
    // The JVM initializes a class on first use: the canvas (Game) when startApp creates it, the
    // others while Game.i loads; none of these static initializers reads another class's statics.
    Game::clinit();
    Actor::clinit();
    Prince::clinit();
    Sprite::clinit();
    Level::clinit();
#ifdef POP_PORT_BUILD
    PortMenu::clinit();
    if (g_options.optionsMenu) PortConfig::desktopMenus();
#endif
    PrinceSOT::instance = jnew<PrinceSOT>();
    PrinceSOT::c = jnew<Game>(PrinceSOT::instance);
    ensureScreen();
}

static void paintOnce() {
    g_graphics->tx = g_graphics->ty = 0;
    g_graphics->setClip(0, 0, g_screen->width, g_screen->height);
    PrinceSOT::c->paint(g_graphics);
}

int g_recordedTicks = 0;   // consecutive recorded level frames (no in-betweens across a cut)

void tick() {
    if (!g_started || g_quit) return;
    ensureScreen();
    g_showInterp = false;
    bool recording = false;
    System::virtualTime = g_clock;
    try {
        Ref<Game> game = PrinceSOT::c;
        if (!g_booted) {
            // run(), first pass: logo, load, then a one-second pause before the title.
            g_booted = true;
            game->aF = true;
            Game::e = true;
            Game::aw = 6;
            paintOnce();
            game->aF = false;
            game->m_i_V();
            g_bootTicks = static_cast<int>(1000 / iterationMs());
        } else if (g_bootTicks > 0) {
            if (--g_bootTicks == 0) Game::m_a_V();
        } else if (Game::l != 0) {
            Game::i = g_clock;
            game->m_d_V();
            recording = g_options.interpolation && worldState();
            if (recording) recorderStart(g_graphics.get());
            paintOnce();
        } else {
            // run() left its loop: notifyDestroyed, then destroyApp(true)
            g_quit = true;
            PrinceSOT::instance->destroyApp(true);
        }
    } catch (JavaThrow& e) {
        hostLog("uncaught: " + jutf8(e.ex->toString()));
    }
    g_clock += iterationMs();
    if (recording) {
        recorderFinish();
        ++g_recordedTicks;
    } else {
        recorderDrop();
        g_recordedTicks = 0;
    }
}

bool renderInterpolated(double alpha) {
    if (!g_started || !g_options.interpolation || !recorderHasFrame() || g_screen == nullptr || g_recordedTicks < 2) return false;
    if (g_interp == nullptr || g_interp->width != g_screen->width) g_interp = Image::createImage(g_screen->width, g_screen->height);
    Ref<Graphics> g = g_interp->getGraphics();
    if (!recorderReplay(alpha, *g.get())) return false;
    g_showInterp = true;
    return true;
}

Frame frame() {
    Frame f;
    const Ref<Image>& image = (g_showInterp && g_interp != nullptr) ? g_interp : g_screen;
    if (image != nullptr) {
        f.pixels = image->pixels.data();
        f.width = image->width;
        f.height = image->height;
    }
    return f;
}

void keyPressed(int key) {
    if (!g_started) return;
    System::virtualTime = g_clock;
    try { PrinceSOT::c->keyPressed(key); } catch (JavaThrow& e) { hostLog("uncaught in keyPressed: " + jutf8(e.ex->toString())); }
}
void keyReleased(int key) {
    if (!g_started) return;
    System::virtualTime = g_clock;
    try { PrinceSOT::c->keyReleased(key); } catch (JavaThrow& e) { hostLog("uncaught in keyReleased: " + jutf8(e.ex->toString())); }
}
void hideNotify() { if (g_started) { try { PrinceSOT::c->hideNotify(); } catch (JavaThrow&) {} } }
void showNotify() { if (g_started) { try { PrinceSOT::c->showNotify(); } catch (JavaThrow&) {} } }
bool finished() { return g_quit; }

void shutdown() {
    if (!g_started || g_quit) return;
    try {
        PrinceSOT::instance->destroyApp(true);
    } catch (JavaThrow&) {
    }
}

double tickSeconds() {
    if (!g_started) return 0.080;
    return iterationMs() / 1000.0;
}

int gameState() { return g_started ? Game::aw : -1; }
int levelIndex() { return g_started ? Game::au : -1; }
bool inGameplay() { return g_started && Game::aw == 0; }
int inputContext() {
    if (!g_started) return 0;
    if (Game::aw != 0) return 0;
    if (Game::av >= 0) return 2; // a hint message is up
    return 1;
}

static void capture(StateSink& sink) {
    sink.prefix = "e.";
    Game::portDumpStatics(sink);
    if (PrinceSOT::c != nullptr) PrinceSOT::c->portDumpFields(sink);
    sink.prefix = "a.";
    Actor::portDumpStatics(sink);
    sink.prefix = "d.";
    Prince::portDumpStatics(sink);
    sink.prefix = "b.";
    Level::portDumpStatics(sink);
    sink.prefix = "c.";
    Sprite::portDumpStatics(sink);
    if (Level::i != nullptr) {
        sink.prefix = "prince.";
        Level::i->portDumpFields(sink);
    }
    if (Level::m != nullptr) {
        for (jint k = 0; k < Level::m.length(); ++k) {
            if (Level::m[k] == nullptr) continue;
            sink.prefix = "actor[" + std::to_string(k) + "].";
            Level::m[k]->portDumpFields(sink);
        }
    }
}

std::uint64_t stateHash() {
    StateSink sink;
    capture(sink);
    return sink.hash;
}

void dumpState(std::vector<std::string>& lines) {
    StateSink sink;
    sink.lines = &lines;
    capture(sink);
}

std::string describeState() {
    if (!g_started) return "not started";
    std::string s = "aw=" + std::to_string(Game::aw) + " level=" + std::to_string(Game::au) + " camera=" + std::to_string(Level::d) + "," + std::to_string(Level::l);
    if (Level::i != nullptr) s += " prince=" + std::to_string(Level::i->e) + "," + std::to_string(Level::i->M);
    return s;
}

} // namespace popapi

namespace popapi {
std::string fontMetrics() { return pop::fontMetrics(); }
}
