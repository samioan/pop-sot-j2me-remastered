package javax.microedition.lcdui;

public abstract class Canvas extends Displayable {
    public static final int UP = 1, DOWN = 6, LEFT = 2, RIGHT = 5, FIRE = 8, GAME_A = 9, GAME_B = 10, GAME_C = 11, GAME_D = 12;
    public static final int KEY_NUM0 = 48, KEY_NUM1 = 49, KEY_NUM2 = 50, KEY_NUM3 = 51, KEY_NUM4 = 52, KEY_NUM5 = 53,
        KEY_NUM6 = 54, KEY_NUM7 = 55, KEY_NUM8 = 56, KEY_NUM9 = 57, KEY_STAR = 42, KEY_POUND = 35;
    protected Canvas() {}
    protected abstract void paint(Graphics g);
    protected void keyPressed(int k) {}
    protected void keyReleased(int k) {}
    protected void keyRepeated(int k) {}
    protected void pointerPressed(int x, int y) {}
    protected void pointerReleased(int x, int y) {}
    protected void pointerDragged(int x, int y) {}
    protected void hideNotify() {}
    protected void showNotify() {}
    protected void sizeChanged(int w, int h) {}
    public final void repaint() {}
    public final void repaint(int x, int y, int w, int h) {}
    public final void serviceRepaints() {}
    public void setFullScreenMode(boolean b) {}
    public boolean hasPointerEvents() { return false; }
    public boolean hasPointerMotionEvents() { return false; }
    public boolean hasRepeatEvents() { return false; }
    public boolean isDoubleBuffered() { return true; }
    public int getGameAction(int k) {
        switch (k) {
            case -1: case 50: return UP;
            case -2: case 56: return DOWN;
            case -3: case 52: return LEFT;
            case -4: case 54: return RIGHT;
            case -5: case 53: return FIRE;
            case 55: return GAME_A;
            case 57: return GAME_B;
            case 42: return GAME_C;
            case 35: return GAME_D;
            default: return 0;
        }
    }
    public int getKeyCode(int a) {
        switch (a) {
            case UP: return -1;
            case DOWN: return -2;
            case LEFT: return -3;
            case RIGHT: return -4;
            case FIRE: return -5;
            case GAME_A: return 55;
            case GAME_B: return 57;
            case GAME_C: return 42;
            case GAME_D: return 35;
            default: return 0;
        }
    }
    public String getKeyName(int k) { return Integer.toString(k); }
}
