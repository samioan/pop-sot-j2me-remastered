package com.nokia.mid.ui;

import javax.microedition.lcdui.Image;

public interface DirectGraphics {
    int FLIP_HORIZONTAL = 8192, FLIP_VERTICAL = 16384, ROTATE_90 = 90, ROTATE_180 = 180, ROTATE_270 = 270;
    int TYPE_BYTE_1_GRAY = 1, TYPE_USHORT_4444_ARGB = 4444, TYPE_INT_8888_ARGB = 8888, TYPE_USHORT_565_RGB = 565;
    void setARGBColor(int argb);
    int getAlphaComponent();
    int getNativePixelFormat();
    void drawImage(Image img, int x, int y, int anchor, int manipulation);
    void fillPolygon(int[] xs, int xo, int[] ys, int yo, int n, int argb);
    void drawPolygon(int[] xs, int xo, int[] ys, int yo, int n, int argb);
    void fillTriangle(int a, int b, int c, int d, int e, int f, int argb);
    void drawTriangle(int a, int b, int c, int d, int e, int f, int argb);
    void drawPixels(int[] p, boolean t, int o, int s, int x, int y, int w, int h, int m, int f);
    void drawPixels(short[] p, boolean t, int o, int s, int x, int y, int w, int h, int m, int f);
    void drawPixels(byte[] p, byte[] mask, int o, int s, int x, int y, int w, int h, int m, int f);
    void getPixels(int[] p, int o, int s, int x, int y, int w, int h, int f);
    void getPixels(short[] p, int o, int s, int x, int y, int w, int h, int f);
}
