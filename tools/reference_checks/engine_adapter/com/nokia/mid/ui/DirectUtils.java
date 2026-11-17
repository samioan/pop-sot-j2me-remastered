package com.nokia.mid.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class DirectUtils {
    static final class Impl implements DirectGraphics {
        int argb;
        public void setARGBColor(int a) { argb = a; }
        public int getAlphaComponent() { return argb >>> 24; }
        public int getNativePixelFormat() { return TYPE_INT_8888_ARGB; }
        public void drawImage(Image img, int x, int y, int anchor, int m) { if (img == null) throw new NullPointerException(); }
        public void fillPolygon(int[] xs, int xo, int[] ys, int yo, int n, int a) { for (int i = 0; i < n; i++) { int q = xs[xo + i] + ys[yo + i]; } }
        public void drawPolygon(int[] xs, int xo, int[] ys, int yo, int n, int a) { for (int i = 0; i < n; i++) { int q = xs[xo + i] + ys[yo + i]; } }
        public void fillTriangle(int a, int b, int c, int d, int e, int f, int g) {}
        public void drawTriangle(int a, int b, int c, int d, int e, int f, int g) {}
        public void drawPixels(int[] p, boolean t, int o, int s, int x, int y, int w, int h, int m, int f) { if (p == null) throw new NullPointerException(); }
        public void drawPixels(short[] p, boolean t, int o, int s, int x, int y, int w, int h, int m, int f) { if (p == null) throw new NullPointerException(); }
        public void drawPixels(byte[] p, byte[] mask, int o, int s, int x, int y, int w, int h, int m, int f) {}
        public void getPixels(int[] p, int o, int s, int x, int y, int w, int h, int f) { for (int r = 0; r < h; r++) for (int c = 0; c < w; c++) p[o + r * s + c] = 0; }
        public void getPixels(short[] p, int o, int s, int x, int y, int w, int h, int f) { for (int r = 0; r < h; r++) for (int c = 0; c < w; c++) p[o + r * s + c] = 0; }
    }
    public static DirectGraphics getDirectGraphics(Graphics g) { return new Impl(); }
    public static Image createImage(int w, int h, int argb) { return Image.createImage(w, h); }
    public static Image createImage(byte[] d, int o, int l) { return Image.createImage(d, o, l); }
}
