package javax.microedition.lcdui;

/** Oracle-only Graphics: keeps clip, translation, colour and font; draws nothing. */
public class Graphics {
    public static final int HCENTER = 1, VCENTER = 2, LEFT = 4, RIGHT = 8, TOP = 16, BOTTOM = 32, BASELINE = 64, SOLID = 0, DOTTED = 1;
    int cx, cy, cw, ch, tx, ty, color, stroke;
    Font font = Font.getDefaultFont();
    public Graphics(int w, int h) { cw = w; ch = h; }
    public void setColor(int rgb) { color = rgb & 0xffffff; }
    public void setColor(int r, int g, int b) { setColor(((r & 255) << 16) | ((g & 255) << 8) | (b & 255)); }
    public void setGrayScale(int v) { setColor(v, v, v); }
    public int getColor() { return color; }
    public int getRedComponent() { return (color >> 16) & 255; }
    public int getGreenComponent() { return (color >> 8) & 255; }
    public int getBlueComponent() { return color & 255; }
    public void setStrokeStyle(int s) { stroke = s; }
    public int getStrokeStyle() { return stroke; }
    public void setClip(int x, int y, int w, int h) { cx = x + tx; cy = y + ty; cw = Math.max(0, w); ch = Math.max(0, h); }
    public void clipRect(int x, int y, int w, int h) {
        int x1 = Math.max(cx, x + tx), y1 = Math.max(cy, y + ty);
        int x2 = Math.min(cx + cw, x + tx + w), y2 = Math.min(cy + ch, y + ty + h);
        cx = x1; cy = y1; cw = Math.max(0, x2 - x1); ch = Math.max(0, y2 - y1);
    }
    public int getClipX() { return cx - tx; }
    public int getClipY() { return cy - ty; }
    public int getClipWidth() { return cw; }
    public int getClipHeight() { return ch; }
    public void translate(int x, int y) { tx += x; ty += y; }
    public int getTranslateX() { return tx; }
    public int getTranslateY() { return ty; }
    public void setFont(Font f) { font = f; }
    public Font getFont() { return font; }
    public void fillRect(int x, int y, int w, int h) {}
    public void drawRect(int x, int y, int w, int h) {}
    public void fillRoundRect(int x, int y, int w, int h, int a, int b) {}
    public void drawRoundRect(int x, int y, int w, int h, int a, int b) {}
    public void drawLine(int a, int b, int c, int d) {}
    public void fillArc(int x, int y, int w, int h, int s, int a) {}
    public void drawArc(int x, int y, int w, int h, int s, int a) {}
    public void fillTriangle(int a, int b, int c, int d, int e, int f) {}
    public void drawImage(Image i, int x, int y, int a) { if (i == null) throw new NullPointerException(); }
    public void drawRegion(Image i, int sx, int sy, int w, int h, int t, int dx, int dy, int a) {
        if (i == null) throw new NullPointerException();
        if (w > 0 && h > 0 && (sx < 0 || sy < 0 || sx + w > i.getWidth() || sy + h > i.getHeight())) throw new IllegalArgumentException();
    }
    public void drawRGB(int[] rgb, int o, int s, int x, int y, int w, int h, boolean a) { if (rgb == null) throw new NullPointerException(); }
    public void copyArea(int a, int b, int c, int d, int e, int f, int g) {}
    public void drawString(String s, int x, int y, int a) { if (s == null) throw new NullPointerException(); }
    public void drawSubstring(String s, int o, int l, int x, int y, int a) { if (s == null) throw new NullPointerException(); if (o < 0 || l < 0 || o + l > s.length()) throw new StringIndexOutOfBoundsException(); }
    public void drawChar(char c, int x, int y, int a) {}
    public void drawChars(char[] c, int o, int l, int x, int y, int a) {}
}
