package javax.microedition.midlet;

/** Oracle-only MIDlet: manifest properties from -Doracle.prop.<key>. */
public abstract class MIDlet {
    protected MIDlet() {}
    protected abstract void startApp() throws MIDletStateChangeException;
    protected abstract void pauseApp();
    protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;
    public final String getAppProperty(String key) { return System.getProperty("oracle.prop." + key); }
    public final void notifyDestroyed() {}
    public final void notifyPaused() {}
    public final void resumeRequest() {}
    public final boolean platformRequest(String url) { return false; }
}
