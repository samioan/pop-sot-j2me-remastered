package javax.microedition.lcdui;

/** Oracle-only Displayable: the handset's canvas size comes from -Doracle.width / -Doracle.height. */
public abstract class Displayable {
    public int getWidth() { return Integer.getInteger("oracle.width", 176); }
    public int getHeight() { return Integer.getInteger("oracle.height", 208); }
    public boolean isShown() { return true; }
    public void addCommand(Command c) {}
    public void removeCommand(Command c) {}
    public void setCommandListener(CommandListener l) {}
}
