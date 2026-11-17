package com.nokia.mid.sound;

/** Oracle-only Nokia Sound sharing the one channel with Player, like the native engine. */
public class Sound {
    public static final int FORMAT_TONE = 1, FORMAT_WAV = 5, SOUND_PLAYING = 0, SOUND_STOPPED = 1, SOUND_UNINITIALIZED = 3;
    public static Object channelOwner;
    public static boolean channelPlaying;
    byte[] data;
    boolean started;
    int gain = 255;
    public Sound(byte[] d, int type) { init(d, type); }
    public Sound(int freq, long duration) { init(freq, duration); }
    public void init(byte[] d, int type) { if (d == null) throw new NullPointerException(); data = d; }
    public void init(int freq, long duration) { data = new byte[0]; }
    public void play(int loops) {
        if (data.length == 0) return;
        channelOwner = this;
        channelPlaying = true;
        started = true;
    }
    public void stop() {
        if (started && channelOwner == this) channelPlaying = false;
        started = false;
    }
    public void resume() {}
    public void release() { stop(); }
    public int getState() {
        if (data.length == 0) return SOUND_UNINITIALIZED;
        if (started && (channelOwner != this || !channelPlaying)) started = false;
        return started ? SOUND_PLAYING : SOUND_STOPPED;
    }
    public void setGain(int g) { gain = Math.max(0, Math.min(255, g)); }
    public int getGain() { return gain; }
    public static int getConcurrentSoundCount(int type) { return 1; }
    public static int[] getSupportedFormats() { return new int[]{FORMAT_TONE, FORMAT_WAV}; }
}
