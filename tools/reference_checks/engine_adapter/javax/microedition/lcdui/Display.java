package javax.microedition.lcdui;

public class Display {
    public static Display getDisplay(javax.microedition.midlet.MIDlet m) { return new Display(); }
    public void setCurrent(Displayable d) {}
    public Displayable getCurrent() { return null; }
    public boolean vibrate(int ms) { return false; }
    public boolean flashBacklight(int ms) { return false; }
    public void callSerially(Runnable r) {}
}
