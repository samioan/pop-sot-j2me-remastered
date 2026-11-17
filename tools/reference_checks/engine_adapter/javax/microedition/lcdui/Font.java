package javax.microedition.lcdui;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Oracle-only Font: metrics exactly as the native engine measures them (-Doracle.fonts=FILE, written by --font-metrics). */
public final class Font {
    public static final int FACE_SYSTEM = 0, FACE_MONOSPACE = 32, FACE_PROPORTIONAL = 64;
    public static final int STYLE_PLAIN = 0, STYLE_BOLD = 1, STYLE_ITALIC = 2, STYLE_UNDERLINED = 4;
    public static final int SIZE_MEDIUM = 0, SIZE_SMALL = 8, SIZE_LARGE = 16;
    private static final Map<Integer, int[]> METRICS = new HashMap<>();
    static {
        String path = System.getProperty("oracle.fonts");
        if (path != null) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(path));
                for (String line : lines) {
                    String[] p = line.trim().split(" ");
                    if (p.length < 4) continue;
                    int[] m = new int[p.length - 2];
                    for (int i = 2; i < p.length; i++) m[i - 2] = Integer.parseInt(p[i]);
                    METRICS.put(Integer.parseInt(p[0]) * 8 + Integer.parseInt(p[1]), m);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private final int face, style, size;
    private Font(int face, int style, int size) { this.face = face; this.style = style; this.size = size; }
    public static Font getFont(int face, int style, int size) { return new Font(face, style, size); }
    public static Font getDefaultFont() { return new Font(0, 0, 0); }
    private int[] m() {
        int[] m = METRICS.get(size * 8 + (style & 3));
        if (m == null) throw new IllegalStateException("no font metrics for size " + size + " style " + style);
        return m;
    }
    public int getFace() { return face; }
    public int getStyle() { return style; }
    public int getSize() { return size; }
    public boolean isBold() { return (style & 1) != 0; }
    public int getHeight() { return m()[0]; }
    public int getBaselinePosition() { return m()[1]; }
    public int charWidth(char c) { return c < 256 ? m()[2 + c] : m()[2 + '?']; }
    public int charsWidth(char[] c, int o, int l) { int w = 0; for (int i = 0; i < l; i++) w += charWidth(c[o + i]); return w; }
    public int stringWidth(String s) { int w = 0; for (int i = 0; i < s.length(); i++) w += charWidth(s.charAt(i)); return w; }
    public int substringWidth(String s, int o, int l) { if (o < 0 || l < 0 || o + l > s.length()) throw new StringIndexOutOfBoundsException(); return stringWidth(s.substring(o, o + l)); }
}
