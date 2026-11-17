package javax.microedition.media;

import javax.microedition.media.control.VolumeControl;

/** Oracle-only Player: one audio channel, playing until stopped or replaced, like engine_check. */
public class Player {
    public static final int CLOSED = 0, UNREALIZED = 100, REALIZED = 200, PREFETCHED = 300, STARTED = 400;
    int state = UNREALIZED;
    final VolumeControl volume = new VolumeControl();
    public void realize() { if (state < REALIZED) state = REALIZED; }
    public void prefetch() { if (state < PREFETCHED) state = PREFETCHED; }
    public void setLoopCount(int n) {}
    public void start() throws MediaException {
        if (state == CLOSED) throw new IllegalStateException();
        com.nokia.mid.sound.Sound.channelOwner = this;
        com.nokia.mid.sound.Sound.channelPlaying = true;
        state = STARTED;
    }
    public void stop() {
        if (state == STARTED) {
            if (com.nokia.mid.sound.Sound.channelOwner == this) com.nokia.mid.sound.Sound.channelPlaying = false;
            state = PREFETCHED;
        }
    }
    public void deallocate() { if (state == STARTED) stop(); if (state == PREFETCHED) state = REALIZED; }
    public void close() {
        if (state == STARTED && com.nokia.mid.sound.Sound.channelOwner == this) com.nokia.mid.sound.Sound.channelPlaying = false;
        state = CLOSED;
    }
    public int getState() {
        if (state == STARTED && (com.nokia.mid.sound.Sound.channelOwner != this || !com.nokia.mid.sound.Sound.channelPlaying)) state = PREFETCHED;
        return state;
    }
    public long setMediaTime(long t) { return t; }
    public long getMediaTime() { return 0; }
    public long getDuration() { return -1; }
    public Control getControl(String name) { return name != null && name.indexOf("VolumeControl") >= 0 ? volume : null; }
    public void addPlayerListener(PlayerListener l) {}
}
