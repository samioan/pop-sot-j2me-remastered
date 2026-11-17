package javax.microedition.media.control;

public class VolumeControl implements javax.microedition.media.Control {
    int level = 100;
    boolean muted;
    public int setLevel(int l) { level = Math.max(0, Math.min(100, l)); return level; }
    public int getLevel() { return level; }
    public void setMute(boolean m) { muted = m; }
    public boolean isMuted() { return muted; }
}
