// The slice of MIDP 2.0 / Nokia UI that the Prince of Persia J2ME games use, drawn into software ARGB
// surfaces. Shared runtime: the same file serves every game of the series; game-specific classes
// (the MIDlet, stubs for classes that are not translated) live in game_platform.h.
#pragma once
#include "jrt.h"

namespace pop {

struct Graphics;
struct Font;

struct Image : Object {
    jint width = 0, height = 0;
    bool mutableImage = false;
    bool hasAlpha = false;
    std::vector<std::uint32_t> pixels;
    static Ref<Image> createImage(const Ref<String>& resource);
    static Ref<Image> createImage(jint width, jint height);
    static Ref<Image> createImage(const Arr<jbyte>& data, jint offset, jint length);
    static Ref<Image> createImage(const Ref<Image>& source);
    static Ref<Image> createImage(const Ref<Image>& source, jint x, jint y, jint w, jint h, jint transform);
    static Ref<Image> createImage(const Ref<InputStream>& stream);
    static Ref<Image> createRGBImage(const Arr<jint>& rgb, jint width, jint height, jboolean processAlpha);
    Ref<Graphics> getGraphics();
    jint getWidth() { return width; }
    jint getHeight() { return height; }
    jboolean isMutable() { return mutableImage; }
    void getRGB(const Arr<jint>& out, jint offset, jint scanLength, jint x, jint y, jint w, jint h);
};

// javax.microedition.lcdui.Font. Glyphs are rasterized by the host (GDI on Windows) at the pixel
// sizes the game's original handset used (fontConfigure), without anti-aliasing, so text looks like
// the phone's bitmap system font and measures the same everywhere in a run.
struct Font : Object {
    static constexpr jint FACE_SYSTEM = 0, FACE_MONOSPACE = 32, FACE_PROPORTIONAL = 64;
    static constexpr jint STYLE_PLAIN = 0, STYLE_BOLD = 1, STYLE_ITALIC = 2, STYLE_UNDERLINED = 4;
    static constexpr jint SIZE_MEDIUM = 0, SIZE_SMALL = 8, SIZE_LARGE = 16;
    jint face = 0, style = 0, size = 0;
    static Ref<Font> getFont(jint face, jint style, jint size);
    static Ref<Font> getDefaultFont() { return getFont(0, 0, 0); }
    jint getFace() { return face; }
    jint getStyle() { return style; }
    jint getSize() { return size; }
    jboolean isBold() { return (style & STYLE_BOLD) != 0; }
    jint getHeight();
    jint getBaselinePosition();
    jint charWidth(jchar c);
    jint charsWidth(const Arr<jchar>& chars, jint offset, jint length);
    jint stringWidth(const Ref<String>& s);
    jint substringWidth(const Ref<String>& s, jint offset, jint length);
};
// Pixel heights for SIZE_SMALL / MEDIUM / LARGE on the original handset; set once by the glue.
void fontConfigure(int smallPx, int mediumPx, int largePx, const char* faceName);
std::string fontMetrics();

struct Graphics : Object {
    static constexpr jint HCENTER = 1, VCENTER = 2, LEFT = 4, RIGHT = 8, TOP = 16, BOTTOM = 32, BASELINE = 64;
    static constexpr jint SOLID = 0, DOTTED = 1;
    Ref<Image> target;
    Ref<Font> font;
    jint clipX = 0, clipY = 0, clipW = 0, clipH = 0;
    jint tx = 0, ty = 0;
    jint strokeStyle = 0;
    std::uint32_t color = 0xff000000u;
    explicit Graphics(const Ref<Image>& image);
    void setColor(jint rgb) { color = 0xff000000u | static_cast<std::uint32_t>(rgb); }
    void setColor(jint r, jint g, jint b) { setColor(((r & 255) << 16) | ((g & 255) << 8) | (b & 255)); }
    void setGrayScale(jint v) { setColor(v, v, v); }
    jint getColor() { return static_cast<jint>(color & 0xffffffu); }
    jint getRedComponent() { return static_cast<jint>((color >> 16) & 0xff); }
    jint getGreenComponent() { return static_cast<jint>((color >> 8) & 0xff); }
    jint getBlueComponent() { return static_cast<jint>(color & 0xff); }
    void setStrokeStyle(jint s) { strokeStyle = s; }
    jint getStrokeStyle() { return strokeStyle; }
    void setClip(jint x, jint y, jint w, jint h);
    void clipRect(jint x, jint y, jint w, jint h);
    jint getClipX() { return clipX - tx; }
    jint getClipY() { return clipY - ty; }
    jint getClipWidth() { return clipW; }
    jint getClipHeight() { return clipH; }
    void translate(jint x, jint y) { tx += x; ty += y; }
    jint getTranslateX() { return tx; }
    jint getTranslateY() { return ty; }
    void fillRect(jint x, jint y, jint w, jint h);
    void drawRect(jint x, jint y, jint w, jint h);
    void fillRoundRect(jint x, jint y, jint w, jint h, jint aw, jint ah);
    void drawRoundRect(jint x, jint y, jint w, jint h, jint aw, jint ah);
    void drawLine(jint x1, jint y1, jint x2, jint y2);
    void fillArc(jint x, jint y, jint w, jint h, jint start, jint arc);
    void drawArc(jint x, jint y, jint w, jint h, jint start, jint arc);
    void fillTriangle(jint x1, jint y1, jint x2, jint y2, jint x3, jint y3);
    void drawImage(const Ref<Image>& image, jint x, jint y, jint anchor);
    void drawRegion(const Ref<Image>& image, jint sx, jint sy, jint w, jint h, jint transform, jint dx, jint dy, jint anchor);
    void drawRGB(const Arr<jint>& rgb, jint offset, jint scanLength, jint x, jint y, jint w, jint h, jboolean processAlpha);
    void copyArea(jint sx, jint sy, jint w, jint h, jint dx, jint dy, jint anchor);
    void setFont(const Ref<Font>& f) { font = f; }
    Ref<Font> getFont();
    void drawString(const Ref<String>& s, jint x, jint y, jint anchor);
    void drawSubstring(const Ref<String>& s, jint offset, jint length, jint x, jint y, jint anchor);
    void drawChar(jchar c, jint x, jint y, jint anchor);
    void drawChars(const Arr<jchar>& chars, jint offset, jint length, jint x, jint y, jint anchor);
    void plot(jint x, jint y);
    void span(jint x1, jint x2, jint y);
    void drawText(const std::u16string& text, jint x, jint y, jint anchor);
};

// com.nokia.mid.ui
struct DirectGraphics : Object {
    static constexpr jint FLIP_HORIZONTAL = 8192, FLIP_VERTICAL = 16384, ROTATE_90 = 90, ROTATE_180 = 180, ROTATE_270 = 270;
    static constexpr jint TYPE_BYTE_1_GRAY = 1, TYPE_USHORT_4444_ARGB = 4444, TYPE_INT_8888_ARGB = 8888, TYPE_USHORT_565_RGB = 565;
    Ref<Graphics> g;
    explicit DirectGraphics(const Ref<Graphics>& graphics) : g(graphics) {}
    void setARGBColor(jint argb) { g->color = static_cast<std::uint32_t>(argb); }
    jint getAlphaComponent() { return static_cast<jint>(g->color >> 24); }
    jint getNativePixelFormat() { return TYPE_INT_8888_ARGB; }
    void drawImage(const Ref<Image>& image, jint x, jint y, jint anchor, jint manipulation);
    void fillPolygon(const Arr<jint>& xs, jint xOffset, const Arr<jint>& ys, jint yOffset, jint n, jint argb);
    void drawPolygon(const Arr<jint>& xs, jint xOffset, const Arr<jint>& ys, jint yOffset, jint n, jint argb);
    void fillTriangle(jint x1, jint y1, jint x2, jint y2, jint x3, jint y3, jint argb);
    void drawTriangle(jint x1, jint y1, jint x2, jint y2, jint x3, jint y3, jint argb);
    void drawPixels(const Arr<jint>& pixels, jboolean transparency, jint offset, jint scan, jint x, jint y, jint w, jint h, jint manipulation, jint format);
    void drawPixels(const Arr<jshort>& pixels, jboolean transparency, jint offset, jint scan, jint x, jint y, jint w, jint h, jint manipulation, jint format);
    void getPixels(const Arr<jint>& pixels, jint offset, jint scan, jint x, jint y, jint w, jint h, jint format);
    void getPixels(const Arr<jshort>& pixels, jint offset, jint scan, jint x, jint y, jint w, jint h, jint format);
};
struct DirectUtils {
    static Ref<DirectGraphics> getDirectGraphics(const Ref<Graphics>& g) { return jnew<DirectGraphics>(g); }
    static Ref<Image> createImage(jint w, jint h, jint argb);
    static Ref<Image> createImage(const Arr<jbyte>& data, jint offset, jint length) { return Image::createImage(data, offset, length); }
};
// MIDP transform (javax.microedition.lcdui.game.Sprite TRANS_*) for a Nokia manipulation.
jint nokiaTransform(jint manipulation);

// The handset's canvas size (portrait, original pixels) and the wider logical size of the port.
int hostCanvasWidth();
int hostCanvasHeight();

struct Displayable : Object {
    virtual ~Displayable() = default;
    jint getWidth() { return hostCanvasWidth(); }
    jint getHeight() { return hostCanvasHeight(); }
    jboolean isShown() { return true; }
    void addCommand(const Ref<Object>&) {}
    void removeCommand(const Ref<Object>&) {}
    void setCommandListener(const Ref<Object>&) {}
};

struct Canvas : Displayable {
    static constexpr jint UP = 1, DOWN = 6, LEFT = 2, RIGHT = 5, FIRE = 8, GAME_A = 9, GAME_B = 10, GAME_C = 11, GAME_D = 12;
    static constexpr jint KEY_NUM0 = 48, KEY_NUM1 = 49, KEY_NUM2 = 50, KEY_NUM3 = 51, KEY_NUM4 = 52, KEY_NUM5 = 53,
                          KEY_NUM6 = 54, KEY_NUM7 = 55, KEY_NUM8 = 56, KEY_NUM9 = 57, KEY_STAR = 42, KEY_POUND = 35;
    virtual void paint(Ref<Graphics> g) = 0;
    virtual void keyPressed(jint) {}
    virtual void keyReleased(jint) {}
    virtual void keyRepeated(jint) {}
    virtual void pointerPressed(jint, jint) {}
    virtual void pointerReleased(jint, jint) {}
    virtual void pointerDragged(jint, jint) {}
    virtual void hideNotify() {}
    virtual void showNotify() {}
    virtual void sizeChanged(jint, jint) {}
    void repaint() {}
    void repaint(jint, jint, jint, jint) {}
    void serviceRepaints() {}
    void setFullScreenMode(jboolean) {}
    jboolean hasPointerEvents() { return false; }
    jboolean hasPointerMotionEvents() { return false; }
    jboolean hasRepeatEvents() { return false; }
    jboolean isDoubleBuffered() { return true; }
    jint getGameAction(jint keyCode);
    jint getKeyCode(jint gameAction);
    Ref<String> getKeyName(jint keyCode) { return jstr(keyCode); }
};
struct FullCanvas : Canvas {
    static constexpr jint KEY_SOFTKEY1 = -6, KEY_SOFTKEY2 = -7, KEY_SEND = -10, KEY_END = -11, KEY_SOFTKEY3 = -5,
                          KEY_UP_ARROW = -1, KEY_DOWN_ARROW = -2, KEY_LEFT_ARROW = -3, KEY_RIGHT_ARROW = -4;
};

// Audio. All sources play through the host's single music channel: the phones these games were made
// for played one sound at a time too.
enum AudioKind { AudioMidi = 0, AudioWave = 1, AudioTone = 2 };

// com.nokia.mid.sound.Sound
struct Sound : Object {
    static constexpr jint FORMAT_TONE = 1, FORMAT_WAV = 5;
    static constexpr jint SOUND_PLAYING = 0, SOUND_STOPPED = 1, SOUND_UNINITIALIZED = 3;
    std::vector<std::uint8_t> data;
    jint type = FORMAT_TONE;
    jint gain = 255;
    bool started = false;
    Sound(const Arr<jbyte>& bytes, jint format) { init(bytes, format); }
    Sound(jint frequency, jlong duration) { init(frequency, duration); }
    void init(const Arr<jbyte>& bytes, jint format);
    void init(jint frequency, jlong duration);
    void play(jint loops);
    void stop();
    void resume() {}
    void release() { stop(); }
    jint getState();
    void setGain(jint g) { gain = g < 0 ? 0 : g > 255 ? 255 : g; }
    jint getGain() { return gain; }
    static jint getConcurrentSoundCount(jint) { return 1; }
    static Arr<jint> getSupportedFormats() { return Arr<jint>::of({FORMAT_TONE, FORMAT_WAV}); }
};

// javax.microedition.media
struct Control : Object {};
struct VolumeControl : Control {
    jint level = 100;
    bool muted = false;
    jint setLevel(jint l) { level = l < 0 ? 0 : l > 100 ? 100 : l; return level; }
    jint getLevel() { return level; }
    void setMute(jboolean m) { muted = m; }
    jboolean isMuted() { return muted; }
};
struct Player : Object {
    static constexpr jint CLOSED = 0, UNREALIZED = 100, REALIZED = 200, PREFETCHED = 300, STARTED = 400;
    std::vector<std::uint8_t> data;
    int kind = AudioMidi;
    jint state = UNREALIZED;
    jint loops = 1;
    Ref<VolumeControl> volume = jnew<VolumeControl>();
    void realize() { if (state < REALIZED) state = REALIZED; }
    void prefetch() { if (state < PREFETCHED) state = PREFETCHED; }
    void setLoopCount(jint n) { loops = n; }
    void start();
    void stop();
    void deallocate() { if (state == STARTED) stop(); if (state == PREFETCHED) state = REALIZED; }
    void close();
    jint getState();
    jlong setMediaTime(jlong t) { return t; }
    jlong getMediaTime() { return 0; }
    jlong getDuration() { return -1; }
    Ref<Control> getControl(const Ref<String>& name);
    void addPlayerListener(const Ref<Object>&) {}
};
struct Manager {
    static Ref<Player> createPlayer(const Ref<InputStream>& stream, const Ref<String>& type);
    static void playTone(jint, jint, jint) {}
};

// javax.microedition.rms. A store is a list of records kept by the host as one file.
struct RecordEnumeration;
struct RecordStore : Object {
    std::string name;
    std::vector<std::pair<jint, std::vector<std::uint8_t>>> records;
    jint nextId = 1;
    static Ref<RecordStore> openRecordStore(const Ref<String>& name, jboolean create);
    static void deleteRecordStore(const Ref<String>& name);
    jint getNumRecords() { return static_cast<jint>(records.size()); }
    jint getNextRecordID() { return nextId; }
    jint getSize();
    jint getSizeAvailable() { return 1 << 20; }
    jint addRecord(const Arr<jbyte>& data, jint offset, jint length);
    void setRecord(jint id, const Arr<jbyte>& data, jint offset, jint length);
    void deleteRecord(jint id);
    jint getRecord(jint id, const Arr<jbyte>& out, jint offset);
    Arr<jbyte> getRecord(jint id);
    jint getRecordSize(jint id);
    Ref<RecordEnumeration> enumerateRecords(const Ref<Object>& filter, const Ref<Object>& comparator, jboolean keepUpdated);
    void closeRecordStore() {}
    std::vector<std::uint8_t>* find(jint id);
    void persist();
};
struct RecordEnumeration : Object {
    Ref<RecordStore> store;
    std::vector<jint> ids;
    std::size_t pos = 0;
    jint numRecords() { return static_cast<jint>(ids.size()); }
    jboolean hasNextElement() { return pos < ids.size(); }
    jint nextRecordId();
    Arr<jbyte> nextRecord() { return store->getRecord(nextRecordId()); }
    void reset() { pos = 0; }
    void destroy() {}
};

// javax.microedition.midlet.MIDlet: application properties come from the JAR manifest (host).
struct MIDlet : Object {
    Ref<String> getAppProperty(const Ref<String>& key);
    void notifyDestroyed();
    void notifyPaused() {}
    void resumeRequest() {}
    jboolean platformRequest(const Ref<String>&) { return false; }
};

// Layer markers the patched draw code emits for render interpolation (tools/port_java/PortNative.java
// is the compile-time stub). They only label draw calls; they never read or change game state.
struct PortNative {
    static void layerScreen();
    static void layerWorld(jint originX, jint originY);
    static void layerTiles(jint originX, jint originY) { layerTiles(0, originX, originY); }
    static void layerTiles(jint id, jint originX, jint originY);
    static void layerActor(const Ref<Object>& actor, jint screenX, jint screenY);
    // A scroll buffer the world is blitted from with wrap-around (period w x h): in-between frames
    // draw it shifted inside the blits' own clips, with its wrapped copies, instead of moving the clips.
    static void tileBuffer(const Ref<Image>& image, jint w, jint h);
    // In-game options menu (full build): host settings by page and index.
    static jint settingCount(jint page);
    static Ref<String> settingName(jint page, jint index);
    static Ref<String> settingValue(jint page, jint index);
    static void settingChange(jint page, jint index, jint direction);
    static jboolean settingCapturing();
    static void settingsClosed();
};
void recorderStart(Graphics* target);
void recorderFinish();
void recorderDrop();
bool recorderHasFrame();
bool recorderReplay(double alpha, Graphics& out);

// Host hooks (implemented by the platform layer).
int hostSettingCount(int page);
std::string hostSettingName(int page, int index);
std::string hostSettingValue(int page, int index);
void hostSettingChange(int page, int index, int direction);
bool hostSettingCapturing();
void hostSettingsClosed();
bool hostLoadSave(const std::string& name, std::vector<std::uint8_t>& out);
bool hostStoreSave(const std::string& name, const std::vector<std::uint8_t>& data);
bool hostDeleteSave(const std::string& name);
void hostAudioStart(const std::vector<std::uint8_t>& data, int kind, int loops, int gain);
void hostAudioStop();
bool hostAudioPlaying();
void hostQuit();
std::string hostAppProperty(const std::string& key);
bool decodePng(const std::uint8_t* data, std::size_t size, int& width, int& height, std::vector<std::uint32_t>& argb, bool& alpha);

} // namespace pop

#include "game_platform.h"
