package javax.microedition.lcdui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/** Oracle-only Image: sizes and pixels from ImageIO; drawing into it does nothing. */
public class Image {
    private final int width, height;
    private final boolean mutable;
    private int[] rgb;
    private Image(int width, int height, boolean mutable) { this.width = width; this.height = height; this.mutable = mutable; }
    private static Image decode(InputStream in) throws IOException {
        if (in == null) throw new IOException("missing image resource");
        BufferedImage image = ImageIO.read(in);
        if (image == null) throw new IOException("ImageIO could not decode PNG");
        Image result = new Image(image.getWidth(), image.getHeight(), false);
        result.rgb = image.getRGB(0, 0, result.width, result.height, null, 0, result.width);
        return result;
    }
    public void getRGB(int[] out, int offset, int scan, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || w < 0 || h < 0 || x + w > width || y + h > height) throw new IllegalArgumentException("getRGB area");
        for (int row = 0; row < h; row++)
            for (int col = 0; col < w; col++)
                out[offset + row * scan + col] = rgb != null ? rgb[(y + row) * width + x + col] : 0xffffffff;
    }
    public static Image createImage(String resource) throws IOException {
        String path = resource.startsWith("/") ? resource.substring(1) : resource;
        return decode(ClassLoader.getSystemResourceAsStream(path) != null ? ClassLoader.getSystemResourceAsStream(path) : Image.class.getResourceAsStream("/" + path));
    }
    public static Image createImage(InputStream in) throws IOException { return decode(in); }
    public static Image createImage(byte[] data, int offset, int length) {
        try { return decode(new ByteArrayInputStream(data, offset, length)); }
        catch (IOException e) { throw new IllegalArgumentException(e.getMessage()); }
    }
    public static Image createImage(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        Image i = new Image(width, height, true);
        i.rgb = new int[width * height];
        java.util.Arrays.fill(i.rgb, 0xffffffff);
        return i;
    }
    public static Image createImage(Image source) { return source; }
    public static Image createImage(Image s, int x, int y, int w, int h, int t) {
        if (x < 0 || y < 0 || w <= 0 || h <= 0 || x + w > s.width || y + h > s.height) throw new IllegalArgumentException();
        return t >= 4 ? new Image(h, w, false) : new Image(w, h, false);
    }
    public static Image createRGBImage(int[] pixels, int width, int height, boolean alpha) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        if ((long) width * height > pixels.length) throw new ArrayIndexOutOfBoundsException();
        Image result = new Image(width, height, false);
        result.rgb = new int[width * height];
        for (int i = 0; i < result.rgb.length; i++) result.rgb[i] = alpha ? pixels[i] : (pixels[i] | 0xff000000);
        return result;
    }
    public Graphics getGraphics() {
        if (!mutable) throw new IllegalStateException();
        return new Graphics(width, height);
    }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isMutable() { return mutable; }
}
