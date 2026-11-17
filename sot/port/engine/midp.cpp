#include "midp.h"
#include "inflate.h"
#include <algorithm>
#include <cmath>
#include <map>
#include <memory>
#include <type_traits>

namespace pop {

// ---------------------------------------------------------------- PNG

namespace {
std::uint32_t be32(const std::uint8_t* p) { return (static_cast<std::uint32_t>(p[0]) << 24) | (p[1] << 16) | (p[2] << 8) | p[3]; }
int paeth(int a, int b, int c) {
    int p = a + b - c, pa = std::abs(p - a), pb = std::abs(p - b), pc = std::abs(p - c);
    return (pa <= pb && pa <= pc) ? a : (pb <= pc ? b : c);
}
} // namespace

bool decodePng(const std::uint8_t* data, std::size_t size, int& width, int& height, std::vector<std::uint32_t>& argb, bool& alpha) {
    static const std::uint8_t signature[8] = {137, 80, 78, 71, 13, 10, 26, 10};
    if (size < 8 || std::memcmp(data, signature, 8) != 0) return false;
    std::size_t pos = 8;
    int depth = 0, colorType = 0, interlace = 0;
    std::vector<std::uint8_t> idat, palette, trns;
    bool header = false;
    while (pos + 8 <= size) {
        std::uint32_t length = be32(data + pos);
        std::uint32_t kind = be32(data + pos + 4);
        const std::uint8_t* body = data + pos + 8;
        if (pos + 12 + static_cast<std::size_t>(length) > size) break;
        if (kind == 0x49484452u && length >= 13) {
            width = static_cast<int>(be32(body));
            height = static_cast<int>(be32(body + 4));
            depth = body[8];
            colorType = body[9];
            interlace = body[12];
            header = true;
        } else if (kind == 0x504c5445u) palette.assign(body, body + length);
        else if (kind == 0x74524e53u) trns.assign(body, body + length);
        else if (kind == 0x49444154u) idat.insert(idat.end(), body, body + length);
        else if (kind == 0x49454e44u) break;
        pos += 12 + static_cast<std::size_t>(length);
    }
    if (!header || width <= 0 || height <= 0 || width > 8192 || height > 8192 || interlace != 0) return false;
    int channels = colorType == 0 ? 1 : colorType == 2 ? 3 : colorType == 3 ? 1 : colorType == 4 ? 2 : colorType == 6 ? 4 : 0;
    if (!channels || (depth != 1 && depth != 2 && depth != 4 && depth != 8 && depth != 16)) return false;
    std::vector<std::uint8_t> raw;
    if (!inflateZlib(idat.data(), idat.size(), raw)) return false;
    std::size_t bpp = static_cast<std::size_t>(std::max(1, channels * depth / 8));
    std::size_t stride = (static_cast<std::size_t>(width) * static_cast<std::size_t>(channels) * static_cast<std::size_t>(depth) + 7) / 8;
    if (raw.size() < (stride + 1) * static_cast<std::size_t>(height)) return false;
    std::vector<std::uint8_t> prev(stride, 0), cur(stride);
    argb.assign(static_cast<std::size_t>(width) * static_cast<std::size_t>(height), 0);
    alpha = colorType == 4 || colorType == 6 || !trns.empty();
    for (int y = 0; y < height; ++y) {
        const std::uint8_t* line = raw.data() + (stride + 1) * static_cast<std::size_t>(y);
        int filter = line[0];
        for (std::size_t i = 0; i < stride; ++i) {
            int a = i >= bpp ? cur[i - bpp] : 0, b = prev[i], c = i >= bpp ? prev[i - bpp] : 0, x = line[1 + i];
            switch (filter) {
                case 1: x += a; break;
                case 2: x += b; break;
                case 3: x += (a + b) / 2; break;
                case 4: x += paeth(a, b, c); break;
                default: break;
            }
            cur[i] = static_cast<std::uint8_t>(x);
        }
        std::uint32_t* dst = argb.data() + static_cast<std::size_t>(y) * static_cast<std::size_t>(width);
        for (int x = 0; x < width; ++x) {
            auto sample = [&](int channel) -> int {
                if (depth == 8) return cur[static_cast<std::size_t>(x * channels + channel)];
                if (depth == 16) return cur[static_cast<std::size_t>((x * channels + channel) * 2)];
                int bit = x * depth;
                int v = (cur[static_cast<std::size_t>(bit / 8)] >> (8 - depth - (bit % 8))) & ((1 << depth) - 1);
                return v;
            };
            std::uint32_t px = 0;
            if (colorType == 3) {
                std::size_t index = static_cast<std::size_t>(sample(0));
                std::uint32_t a = index < trns.size() ? trns[index] : 255;
                if (index * 3 + 2 < palette.size()) px = (a << 24) | (palette[index * 3] << 16) | (palette[index * 3 + 1] << 8) | palette[index * 3 + 2];
                else px = a << 24;
            } else if (colorType == 0) {
                int v = sample(0);
                int scaled = depth >= 8 ? v : v * 255 / ((1 << depth) - 1);
                std::uint32_t a = 255;
                if (trns.size() >= 2 && ((trns[0] << 8) | trns[1]) == (depth == 16 ? (v << 8) | cur[static_cast<std::size_t>(x * 2 + 1)] : v)) a = 0;
                px = (a << 24) | (scaled << 16) | (scaled << 8) | scaled;
            } else if (colorType == 2) {
                int r = sample(0), g = sample(1), b = sample(2);
                std::uint32_t a = 255;
                if (trns.size() >= 6 && depth == 8 && trns[1] == r && trns[3] == g && trns[5] == b) a = 0;
                px = (a << 24) | (r << 16) | (g << 8) | b;
            } else if (colorType == 4) {
                int v = sample(0);
                px = (static_cast<std::uint32_t>(sample(1)) << 24) | (v << 16) | (v << 8) | v;
            } else {
                px = (static_cast<std::uint32_t>(sample(3)) << 24) | (sample(0) << 16) | (sample(1) << 8) | sample(2);
            }
            dst[x] = px;
        }
        prev.swap(cur);
    }
    return true;
}

// ---------------------------------------------------------------- Image

static Ref<Image> imageFromPng(const std::uint8_t* data, std::size_t size) {
    Ref<Image> image = jnew<Image>();
    int w = 0, h = 0;
    bool alpha = false;
    if (!decodePng(data, size, w, h, image->pixels, alpha)) throwJava("java.io.IOException", "PNG decode failed");
    image->width = w;
    image->height = h;
    image->hasAlpha = alpha;
    return image;
}

Ref<Image> Image::createImage(const Ref<String>& resource) {
    if (!resource.p) throwNullPointer();
    std::string path = jutf8(resource);
    while (!path.empty() && path[0] == '/') path.erase(0, 1);
    std::vector<std::uint8_t> bytes;
    if (!hostReadResource(path, bytes)) throwJava("java.io.IOException", "missing resource " + path);
    return imageFromPng(bytes.data(), bytes.size());
}

Ref<Image> Image::createImage(jint width, jint height) {
    if (width <= 0 || height <= 0) throwJava("java.lang.IllegalArgumentException", "image size");
    Ref<Image> image = jnew<Image>();
    image->width = width;
    image->height = height;
    image->mutableImage = true;
    image->pixels.assign(static_cast<std::size_t>(width) * static_cast<std::size_t>(height), 0xffffffffu);
    return image;
}

Ref<Image> Image::createImage(const Arr<jbyte>& data, jint offset, jint length) {
    if (!data.p) throwNullPointer();
    if (offset < 0 || length < 0 || length > data.length() - offset) throwIndex(offset, data.length());
    return imageFromPng(reinterpret_cast<const std::uint8_t*>(data.p->data) + offset, static_cast<std::size_t>(length));
}

Ref<Image> Image::createRGBImage(const Arr<jint>& rgb, jint width, jint height, jboolean processAlpha) {
    if (!rgb.p) throwNullPointer();
    if (width <= 0 || height <= 0) throwJava("java.lang.IllegalArgumentException", "image size");
    if (static_cast<jlong>(width) * height > rgb.length()) throwIndex(width * height, rgb.length());
    Ref<Image> image = jnew<Image>();
    image->width = width;
    image->height = height;
    image->hasAlpha = processAlpha;
    image->pixels.resize(static_cast<std::size_t>(width) * static_cast<std::size_t>(height));
    for (std::size_t i = 0; i < image->pixels.size(); ++i) {
        std::uint32_t v = static_cast<std::uint32_t>(rgb.p->data[i]);
        image->pixels[i] = processAlpha ? v : (v | 0xff000000u);
    }
    return image;
}

Ref<Graphics> Image::getGraphics() {
    if (!mutableImage) throwJava("java.lang.IllegalStateException", "immutable image");
    return jnew<Graphics>(Ref<Image>(this));
}

void Image::getRGB(const Arr<jint>& out, jint offset, jint scanLength, jint x, jint y, jint w, jint h) {
    if (!out.p) throwNullPointer();
    if (x < 0 || y < 0 || w < 0 || h < 0 || x + w > width || y + h > height) throwJava("java.lang.IllegalArgumentException", "getRGB area");
    for (jint row = 0; row < h; ++row)
        for (jint col = 0; col < w; ++col)
            out[offset + row * scanLength + col] = static_cast<jint>(pixels[static_cast<std::size_t>((y + row) * width + x + col)]);
}

// ---------------------------------------------------------------- draw recording (render interpolation)

// One simulation tick of draw calls, tagged by layer. Between ticks the host replays them with each
// layer moved part of the way back towards where it was drawn on the previous tick. Nothing in the
// game is re-run, so collision, animation, timers and random numbers stay on the original tick.
namespace {
enum OpKind { OpFillRect, OpDrawRect, OpDrawLine, OpFillArc, OpDrawArc, OpFillTriangle, OpImage, OpRgb, OpText };
struct DrawOp {
    OpKind kind;
    int layer;
    jint a, b, c, d, e, f;      // already translated to surface coordinates
    jint sx, sy, transform;
    jint clipX, clipY, clipW, clipH;
    std::uint32_t color;
    Ref<Image> image;
    std::vector<std::uint32_t> pixels;
    std::u16string text;
    Ref<Font> font;
    bool alpha;
};
struct Layer {
    int kind;                   // 0 screen, 1 world, 2 tiles, 3 actor
    int id;                     // which tile layer (they scroll at different rates)
    const Object* key;
    Ref<Object> hold;           // keeps an actor alive (and its address unique) while it is a key
    jint x, y;                  // where the anchor of the layer was drawn
    jint clipX, clipY, clipW, clipH;
};
struct Recording {
    std::vector<DrawOp> ops;
    std::vector<Layer> layers;
    bool valid = false;
};
struct Recorder {
    Graphics* target = nullptr;
    bool replaying = false;
    Recording work, current, previous;
} g_rec;

inline bool recordingTo(const Graphics* g) { return g_rec.target == g && !g_rec.replaying; }

void pushLayer(int kind, const Ref<Object>& key, jint x, jint y, int id = 0) {
    if (!g_rec.target) return;
    Layer l;
    l.kind = kind;
    l.id = id;
    l.key = key.p;
    l.hold = key;
    l.x = x;
    l.y = y;
    l.clipX = g_rec.target->clipX;
    l.clipY = g_rec.target->clipY;
    l.clipW = g_rec.target->clipW;
    l.clipH = g_rec.target->clipH;
    if (kind == 3) {
        // An actor inherits the clip of the world layer it is drawn in, whatever the previous actor left behind.
        for (auto it = g_rec.work.layers.rbegin(); it != g_rec.work.layers.rend(); ++it)
            if (it->kind == 1) { l.clipX = it->clipX; l.clipY = it->clipY; l.clipW = it->clipW; l.clipH = it->clipH; break; }
    }
    g_rec.work.layers.push_back(l);
}

DrawOp& newOp(const Graphics& g, OpKind kind) {
    if (g_rec.work.layers.empty()) pushLayer(0, nullptr, 0, 0);
    g_rec.work.ops.emplace_back();
    DrawOp& op = g_rec.work.ops.back();
    op.kind = kind;
    op.layer = static_cast<int>(g_rec.work.layers.size()) - 1;
    op.clipX = g.clipX;
    op.clipY = g.clipY;
    op.clipW = g.clipW;
    op.clipH = g.clipH;
    op.color = g.color;
    op.sx = op.sy = op.transform = 0;
    op.alpha = false;
    return op;
}
void recordOp(const Graphics& g, OpKind kind, jint a, jint b, jint c, jint d, jint e, jint f) {
    DrawOp& op = newOp(g, kind);
    op.a = a; op.b = b; op.c = c; op.d = d; op.e = e; op.f = f;
}
void recordImageOp(const Graphics& g, const Ref<Image>& image, jint sx, jint sy, jint w, jint h, jint transform, jint dx, jint dy) {
    DrawOp& op = newOp(g, OpImage);
    op.image = image;
    op.sx = sx; op.sy = sy; op.transform = transform;
    op.a = dx; op.b = dy; op.c = w; op.d = h; op.e = op.f = 0;
}
void recordRgbOp(const Graphics& g, const Arr<jint>& rgb, jint offset, jint scan, jint x, jint y, jint w, jint h, bool alpha) {
    DrawOp& op = newOp(g, OpRgb);
    op.a = x; op.b = y; op.c = w; op.d = h; op.e = op.f = 0;
    op.alpha = alpha;
    op.pixels.resize(static_cast<std::size_t>(w) * static_cast<std::size_t>(h));
    for (jint row = 0; row < h; ++row)
        for (jint col = 0; col < w; ++col)
            op.pixels[static_cast<std::size_t>(row * w + col)] = static_cast<std::uint32_t>(rgb[offset + row * scan + col]);
}
void recordTextOp(const Graphics& g, const std::u16string& text, jint x, jint y) {
    DrawOp& op = newOp(g, OpText);
    op.a = x; op.b = y; op.c = op.d = op.e = op.f = 0;
    op.text = text;
    op.font = g.font;
}
} // namespace

namespace {
Image* g_wrapImage = nullptr;
jint g_wrapW = 0, g_wrapH = 0;
}
void PortNative::tileBuffer(const Ref<Image>& image, jint w, jint h) {
    g_wrapImage = image.get();
    g_wrapW = w;
    g_wrapH = h;
}
void PortNative::layerScreen() { pushLayer(0, nullptr, 0, 0); }
void PortNative::layerWorld(jint originX, jint originY) { pushLayer(1, nullptr, originX, originY); }
void PortNative::layerTiles(jint id, jint originX, jint originY) { pushLayer(2, nullptr, originX, originY, id); }
jint PortNative::settingCount(jint page) { return hostSettingCount(page); }
Ref<String> PortNative::settingName(jint page, jint index) { return jstr(hostSettingName(page, index)); }
Ref<String> PortNative::settingValue(jint page, jint index) { return jstr(hostSettingValue(page, index)); }
void PortNative::settingChange(jint page, jint index, jint direction) { hostSettingChange(page, index, direction); }
jboolean PortNative::settingCapturing() { return hostSettingCapturing(); }
void PortNative::settingsClosed() { hostSettingsClosed(); }
void PortNative::layerActor(const Ref<Object>& actor, jint screenX, jint screenY) { pushLayer(3, actor, screenX, screenY); }

void recorderStart(Graphics* target) {
    g_rec.target = target;
    g_rec.work.ops.clear();
    g_rec.work.layers.clear();
    g_rec.work.valid = false;
}
void recorderFinish() {
    if (!g_rec.target) return;
    g_rec.target = nullptr;
    g_rec.work.valid = true;
    std::swap(g_rec.previous, g_rec.current);
    std::swap(g_rec.current, g_rec.work);
}
void recorderDrop() {
    g_rec.target = nullptr;
    g_rec.work = Recording();
    g_rec.current = Recording();
    g_rec.previous = Recording();
}
bool recorderHasFrame() { return g_rec.current.valid; }

bool recorderReplay(double alpha, Graphics& out) {
    if (!g_rec.current.valid) return false;
    const Recording& cur = g_rec.current;
    const Recording& prev = g_rec.previous;
    const double back = alpha >= 1.0 ? 0.0 : alpha <= 0.0 ? 1.0 : 1.0 - alpha;
    // How far each layer moved on screen since the previous tick; large jumps (passages, restarts,
    // scene cuts, respawns) are shown at their new place immediately.
    std::vector<std::pair<jint, jint>> shift(cur.layers.size(), std::make_pair(0, 0));
    if (prev.valid && back > 0.0) {
        for (std::size_t i = 0; i < cur.layers.size(); ++i) {
            const Layer& l = cur.layers[i];
            if (l.kind == 0) continue;
            for (const Layer& p : prev.layers) {
                if (p.kind != l.kind || p.key != l.key || p.id != l.id) continue;
                const jint dx = p.x - l.x, dy = p.y - l.y;
                const jint limit = l.kind == 3 ? 48 : 64;
                if (dx >= -limit && dx <= limit && dy >= -limit && dy <= limit)
                    shift[i] = std::make_pair(static_cast<jint>(std::lround(dx * back)), static_cast<jint>(std::lround(dy * back)));
                break;
            }
        }
    }
    g_rec.replaying = true;
    out.tx = out.ty = 0;
    for (const DrawOp& op : cur.ops) {
        const Layer& l = cur.layers[static_cast<std::size_t>(op.layer)];
        const jint dx = shift[static_cast<std::size_t>(op.layer)].first, dy = shift[static_cast<std::size_t>(op.layer)].second;
        // A clip the layer inherited (the room/screen) stays put; a clip an actor set for itself moves with it.
        const bool ownClip = op.clipX != l.clipX || op.clipY != l.clipY || op.clipW != l.clipW || op.clipH != l.clipH;
        if (ownClip && (dx || dy)) {
            out.setClip(op.clipX + dx, op.clipY + dy, op.clipW, op.clipH);
            out.clipRect(l.clipX, l.clipY, l.clipW, l.clipH);
        } else {
            out.setClip(op.clipX, op.clipY, op.clipW, op.clipH);
        }
        out.color = op.color;
        switch (op.kind) {
            case OpFillRect: out.fillRect(op.a + dx, op.b + dy, op.c, op.d); break;
            case OpDrawRect: out.drawRect(op.a + dx, op.b + dy, op.c, op.d); break;
            case OpDrawLine: out.drawLine(op.a + dx, op.b + dy, op.c + dx, op.d + dy); break;
            case OpFillArc: out.fillArc(op.a + dx, op.b + dy, op.c, op.d, op.e, op.f); break;
            case OpDrawArc: out.drawArc(op.a + dx, op.b + dy, op.c, op.d, op.e, op.f); break;
            case OpFillTriangle: out.fillTriangle(op.a + dx, op.b + dy, op.c + dx, op.d + dy, op.e + dx, op.f + dy); break;
            case OpImage:
                if (op.image.get() == g_wrapImage && (dx || dy)) {
                    out.setClip(op.clipX, op.clipY, op.clipW, op.clipH);
                    for (jint oy = -g_wrapH; oy <= g_wrapH; oy += g_wrapH)
                        for (jint ox = -g_wrapW; ox <= g_wrapW; ox += g_wrapW)
                            out.drawRegion(op.image, op.sx, op.sy, op.c, op.d, op.transform, op.a + dx + ox, op.b + dy + oy, 0);
                } else {
                    out.drawRegion(op.image, op.sx, op.sy, op.c, op.d, op.transform, op.a + dx, op.b + dy, 0);
                }
                break;
            case OpText: out.font = op.font; out.drawText(op.text, op.a + dx, op.b + dy, 20); break;
            case OpRgb: {
                Image* dst = out.target.get();
                for (jint row = 0; row < op.d; ++row) {
                    const jint py = op.b + dy + row;
                    if (py < std::max(0, out.clipY) || py >= std::min(dst->height, out.clipY + out.clipH)) continue;
                    for (jint col = 0; col < op.c; ++col) {
                        const jint px = op.a + dx + col;
                        if (px < std::max(0, out.clipX) || px >= std::min(dst->width, out.clipX + out.clipW)) continue;
                        const std::uint32_t src = op.pixels[static_cast<std::size_t>(row * op.c + col)];
                        std::uint32_t& d = dst->pixels[static_cast<std::size_t>(py * dst->width + px)];
                        const std::uint32_t a = op.alpha ? (src >> 24) : 255;
                        if (a == 255) d = src | 0xff000000u;
                        else if (a) {
                            const std::uint32_t inv = 255 - a;
                            d = 0xff000000u | ((((src >> 16) & 0xff) * a + ((d >> 16) & 0xff) * inv + 127) / 255 << 16)
                                | ((((src >> 8) & 0xff) * a + ((d >> 8) & 0xff) * inv + 127) / 255 << 8)
                                | (((src & 0xff) * a + (d & 0xff) * inv + 127) / 255);
                        }
                    }
                }
                break;
            }
        }
    }
    g_rec.replaying = false;
    return true;
}

// ---------------------------------------------------------------- Graphics

static inline void blend(std::uint32_t& dst, std::uint32_t src) {
    std::uint32_t a = src >> 24;
    if (a == 255) { dst = src; return; }
    if (a == 0) return;
    std::uint32_t inv = 255 - a;
    std::uint32_t r = (((src >> 16) & 0xff) * a + ((dst >> 16) & 0xff) * inv + 127) / 255;
    std::uint32_t g = (((src >> 8) & 0xff) * a + ((dst >> 8) & 0xff) * inv + 127) / 255;
    std::uint32_t b = ((src & 0xff) * a + (dst & 0xff) * inv + 127) / 255;
    dst = 0xff000000u | (r << 16) | (g << 8) | b;
}

Graphics::Graphics(const Ref<Image>& image) : target(image) {
    clipW = image->width;
    clipH = image->height;
}

void Graphics::setClip(jint x, jint y, jint w, jint h) {
    clipX = x + tx;
    clipY = y + ty;
    clipW = w < 0 ? 0 : w;
    clipH = h < 0 ? 0 : h;
}

void Graphics::clipRect(jint x, jint y, jint w, jint h) {
    jint x1 = std::max(clipX, x + tx), y1 = std::max(clipY, y + ty);
    jint x2 = std::min(clipX + clipW, x + tx + w), y2 = std::min(clipY + clipH, y + ty + h);
    clipX = x1;
    clipY = y1;
    clipW = std::max(0, x2 - x1);
    clipH = std::max(0, y2 - y1);
}

namespace {
struct Bounds { jint x1, y1, x2, y2; };
inline Bounds bounds(const Graphics& g) {
    Image* t = g.target.get();
    Bounds b;
    b.x1 = std::max(0, g.clipX);
    b.y1 = std::max(0, g.clipY);
    b.x2 = std::min(t->width, g.clipX + g.clipW);
    b.y2 = std::min(t->height, g.clipY + g.clipH);
    return b;
}
}

void Graphics::plot(jint x, jint y) {
    Bounds b = bounds(*this);
    if (x < b.x1 || y < b.y1 || x >= b.x2 || y >= b.y2) return;
    blend(target.get()->pixels[static_cast<std::size_t>(y * target.get()->width + x)], color);
}

void Graphics::span(jint x1, jint x2, jint y) {
    Bounds b = bounds(*this);
    if (y < b.y1 || y >= b.y2) return;
    x1 = std::max(x1, b.x1);
    x2 = std::min(x2, b.x2 - 1);
    if (x1 > x2) return;
    std::uint32_t* row = target.get()->pixels.data() + static_cast<std::size_t>(y) * static_cast<std::size_t>(target.get()->width);
    if ((color >> 24) == 255) std::fill(row + x1, row + x2 + 1, color);
    else for (jint x = x1; x <= x2; ++x) blend(row[x], color);
}

void Graphics::fillRect(jint x, jint y, jint w, jint h) {
    if (w <= 0 || h <= 0) return;
    x += tx;
    y += ty;
    if (recordingTo(this)) recordOp(*this, OpFillRect, x, y, w, h, 0, 0);
    for (jint row = y; row < y + h; ++row) span(x, x + w - 1, row);
}

void Graphics::drawRect(jint x, jint y, jint w, jint h) {
    if (w < 0 || h < 0) return;
    x += tx;
    y += ty;
    if (recordingTo(this)) recordOp(*this, OpDrawRect, x, y, w, h, 0, 0);
    span(x, x + w, y);
    if (h > 0) span(x, x + w, y + h);
    for (jint row = y + 1; row < y + h; ++row) {
        plot(x, row);
        if (w > 0) plot(x + w, row);
    }
}

void Graphics::drawLine(jint x1, jint y1, jint x2, jint y2) {
    x1 += tx; x2 += tx; y1 += ty; y2 += ty;
    if (recordingTo(this)) recordOp(*this, OpDrawLine, x1, y1, x2, y2, 0, 0);
    if (y1 == y2) { span(std::min(x1, x2), std::max(x1, x2), y1); return; }
    jint dx = std::abs(x2 - x1), dy = -std::abs(y2 - y1);
    jint sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1;
    jint err = dx + dy;
    for (jint guard = 0; guard < 100000; ++guard) {
        plot(x1, y1);
        if (x1 == x2 && y1 == y2) break;
        jint e2 = 2 * err;
        if (e2 >= dy) { err += dy; x1 += sx; }
        if (e2 <= dx) { err += dx; y1 += sy; }
    }
}

void Graphics::fillArc(jint x, jint y, jint w, jint h, jint start, jint arc) {
    if (w <= 0 || h <= 0 || arc == 0) return;
    x += tx;
    y += ty;
    if (recordingTo(this)) recordOp(*this, OpFillArc, x, y, w, h, start, arc);
    // Only rows and columns inside the clip are visited; the game draws 640x640 wedges every tick
    // during boss deaths, and testing every pixel of the box with atan2 cost tens of milliseconds.
    const Bounds b = bounds(*this);
    const double rx = w / 2.0, ry = h / 2.0, cx = x + rx, cy = y + ry;
    const jint rowFirst = std::max(y, b.y1), rowLast = std::min(y + h, b.y2) - 1;
    const jint colFirst = std::max(x, b.x1), colLast = std::min(x + w, b.x2) - 1;
    if (rowFirst > rowLast || colFirst > colLast) return;
    // The wedge as half-plane tests (y up, as atan2(-dy, dx) measured it): from angle s0 through
    // `sweep` degrees counter-clockwise. Sweeps over 180 degrees test the complementary wedge.
    const bool full = arc >= 360 || arc <= -360;
    const double pi = 3.14159265358979323846;
    double s0 = arc >= 0 ? start : start + arc, sweep = std::abs(static_cast<double>(arc));
    const double sx0 = std::cos(s0 * pi / 180.0), sy0 = std::sin(s0 * pi / 180.0);
    const double sx1 = std::cos((s0 + sweep) * pi / 180.0), sy1 = std::sin((s0 + sweep) * pi / 180.0);
    auto inWedge = [&](double vx, double vy) {
        if (full) return true;
        if (sweep <= 180.0) return sx0 * vy - sy0 * vx >= -1e-12 && vx * sy1 - vy * sx1 >= -1e-12;
        return !(sx1 * vy - sy1 * vx > 1e-12 && vx * sy0 - vy * sx0 > 1e-12);
    };
    for (jint py = rowFirst; py <= rowLast; ++py) {
        const double dy = (py + 0.5 - cy) / ry;
        const double rest = 1.0 - dy * dy;
        if (rest < 0) continue;
        // Columns whose centre lies inside the ellipse, widened by one and then tested exactly.
        const double half = rx * std::sqrt(rest);
        jint left = std::max(colFirst, static_cast<jint>(std::floor(cx - half - 0.5)) - 1);
        jint right = std::min(colLast, static_cast<jint>(std::ceil(cx + half - 0.5)) + 1);
        auto inside = [&](jint px) {
            const double dx = (px + 0.5 - cx) / rx;
            return dx * dx + dy * dy <= 1.0 && inWedge(dx, -dy);
        };
        if (full) {
            while (left <= right && !inside(left)) ++left;
            while (right >= left && !inside(right)) --right;
            if (left <= right) span(left, right, py);
            continue;
        }
        jint runStart = 0;
        bool inRun = false;
        for (jint px = left; px <= right + 1; ++px) {
            const bool in = px <= right && inside(px);
            if (in && !inRun) { runStart = px; inRun = true; }
            else if (!in && inRun) { span(runStart, px - 1, py); inRun = false; }
        }
    }
}

void Graphics::drawArc(jint x, jint y, jint w, jint h, jint start, jint arc) {
    if (w < 0 || h < 0 || arc == 0) return;
    x += tx;
    y += ty;
    if (recordingTo(this)) recordOp(*this, OpDrawArc, x, y, w, h, start, arc);
    const double rx = w / 2.0, ry = h / 2.0, cx = x + rx, cy = y + ry;
    if (arc > 360) arc = 360;
    if (arc < -360) arc = -360;
    const int steps = std::max(16, (w + h) * 4);
    jint lastX = INT32_MIN, lastY = INT32_MIN, firstX = INT32_MIN, firstY = INT32_MIN;
    for (int i = 0; i <= steps; ++i) {
        double angle = (start + static_cast<double>(arc) * i / steps) * 3.14159265358979323846 / 180.0;
        jint px = static_cast<jint>(std::floor(cx + rx * std::cos(angle) + 0.5));
        jint py = static_cast<jint>(std::floor(cy - ry * std::sin(angle) + 0.5));
        if (px == lastX && py == lastY) continue;
        if (i > 0 && px == firstX && py == firstY) continue;
        if (firstX == INT32_MIN) { firstX = px; firstY = py; }
        lastX = px;
        lastY = py;
        plot(px, py);
    }
}

void Graphics::fillTriangle(jint x1, jint y1, jint x2, jint y2, jint x3, jint y3) {
    x1 += tx; x2 += tx; x3 += tx; y1 += ty; y2 += ty; y3 += ty;
    if (recordingTo(this)) recordOp(*this, OpFillTriangle, x1, y1, x2, y2, x3, y3);
    if (y1 > y2) { std::swap(x1, x2); std::swap(y1, y2); }
    if (y2 > y3) { std::swap(x2, x3); std::swap(y2, y3); }
    if (y1 > y2) { std::swap(x1, x2); std::swap(y1, y2); }
    auto edge = [](jint xa, jint ya, jint xb, jint yb, jint y) -> double {
        if (yb == ya) return xa;
        return xa + static_cast<double>(xb - xa) * (y - ya) / (yb - ya);
    };
    for (jint y = y1; y <= y3; ++y) {
        double a = edge(x1, y1, x3, y3, y);
        double lo = a, hi = a;
        if (y <= y2 && y2 != y1) { double b = edge(x1, y1, x2, y2, y); lo = std::min(lo, b); hi = std::max(hi, b); }
        if (y >= y2 && y3 != y2) { double b = edge(x2, y2, x3, y3, y); lo = std::min(lo, b); hi = std::max(hi, b); }
        if (y == y2) { lo = std::min(lo, static_cast<double>(x2)); hi = std::max(hi, static_cast<double>(x2)); }
        span(static_cast<jint>(std::floor(lo + 0.5)), static_cast<jint>(std::floor(hi + 0.5)), y);
    }
}

void Graphics::drawImage(const Ref<Image>& image, jint x, jint y, jint anchor) {
    if (!image.p) throwNullPointer();
    drawRegion(image, 0, 0, image.get()->width, image.get()->height, 0, x, y, anchor);
}

void Graphics::drawRegion(const Ref<Image>& image, jint sx, jint sy, jint w, jint h, jint transform, jint dx, jint dy, jint anchor) {
    if (!image.p) throwNullPointer();
    Image* src = image.get();
    if (w <= 0 || h <= 0) return;
    if (sx < 0 || sy < 0 || sx + w > src->width || sy + h > src->height) throwJava("java.lang.IllegalArgumentException", "drawRegion source");
    const bool swap = transform >= 4;
    const jint dw = swap ? h : w, dh = swap ? w : h;
    if (anchor & 1) dx -= dw / 2;
    else if (anchor & 8) dx -= dw;
    if (anchor & 2) dy -= dh / 2;
    else if (anchor & 32) dy -= dh;
    dx += tx;
    dy += ty;
    if (recordingTo(this)) recordImageOp(*this, image, sx, sy, w, h, transform, dx, dy);
    Bounds b = bounds(*this);
    jint x1 = std::max(dx, b.x1), y1 = std::max(dy, b.y1), x2 = std::min(dx + dw, b.x2), y2 = std::min(dy + dh, b.y2);
    if (x1 >= x2 || y1 >= y2) return;
    Image* dst = target.get();
    const bool alpha = src->hasAlpha;
    for (jint py = y1; py < y2; ++py) {
        std::uint32_t* row = dst->pixels.data() + static_cast<std::size_t>(py) * static_cast<std::size_t>(dst->width);
        const jint oy = py - dy;
        for (jint px = x1; px < x2; ++px) {
            const jint ox = px - dx;
            jint ux, uy;
            switch (transform) {
                case 2: ux = w - 1 - ox; uy = oy; break;
                case 1: ux = ox; uy = h - 1 - oy; break;
                case 3: ux = w - 1 - ox; uy = h - 1 - oy; break;
                case 5: ux = oy; uy = h - 1 - ox; break;
                case 6: ux = w - 1 - oy; uy = ox; break;
                case 7: ux = w - 1 - oy; uy = h - 1 - ox; break;
                case 4: ux = oy; uy = ox; break;
                default: ux = ox; uy = oy; break;
            }
            std::uint32_t s = src->pixels[static_cast<std::size_t>((sy + uy) * src->width + sx + ux)];
            if (alpha) blend(row[px], s);
            else row[px] = s | 0xff000000u;
        }
    }
}

void Graphics::drawRGB(const Arr<jint>& rgb, jint offset, jint scanLength, jint x, jint y, jint w, jint h, jboolean processAlpha) {
    if (!rgb.p) throwNullPointer();
    if (w <= 0 || h <= 0) return;
    x += tx;
    y += ty;
    if (recordingTo(this)) recordRgbOp(*this, rgb, offset, scanLength, x, y, w, h, processAlpha);
    Bounds b = bounds(*this);
    Image* dst = target.get();
    for (jint row = 0; row < h; ++row) {
        jint py = y + row;
        if (py < b.y1 || py >= b.y2) continue;
        for (jint col = 0; col < w; ++col) {
            jint px = x + col;
            if (px < b.x1 || px >= b.x2) continue;
            std::uint32_t s = static_cast<std::uint32_t>(rgb[offset + row * scanLength + col]);
            std::uint32_t& d = dst->pixels[static_cast<std::size_t>(py * dst->width + px)];
            if (processAlpha) blend(d, s);
            else d = s | 0xff000000u;
        }
    }
}


// ---------------------------------------------------------------- more Image constructors

Ref<Image> Image::createImage(const Ref<Image>& source) {
    if (!source.p) throwNullPointer();
    if (!source->mutableImage) return source;
    Ref<Image> image = jnew<Image>();
    image->width = source->width;
    image->height = source->height;
    image->hasAlpha = source->hasAlpha;
    image->pixels = source->pixels;
    return image;
}

Ref<Image> Image::createImage(const Ref<Image>& source, jint x, jint y, jint w, jint h, jint transform) {
    if (!source.p) throwNullPointer();
    if (x < 0 || y < 0 || w <= 0 || h <= 0 || x + w > source->width || y + h > source->height) throwJava("java.lang.IllegalArgumentException", "createImage region");
    const bool swap = transform >= 4;
    Ref<Image> image = jnew<Image>();
    image->width = swap ? h : w;
    image->height = swap ? w : h;
    image->hasAlpha = source->hasAlpha;
    image->pixels.assign(static_cast<std::size_t>(image->width) * static_cast<std::size_t>(image->height), 0);
    for (jint oy = 0; oy < image->height; ++oy)
        for (jint ox = 0; ox < image->width; ++ox) {
            jint ux, uy;
            switch (transform) {
                case 2: ux = w - 1 - ox; uy = oy; break;
                case 1: ux = ox; uy = h - 1 - oy; break;
                case 3: ux = w - 1 - ox; uy = h - 1 - oy; break;
                case 5: ux = oy; uy = h - 1 - ox; break;
                case 6: ux = w - 1 - oy; uy = ox; break;
                case 7: ux = w - 1 - oy; uy = h - 1 - ox; break;
                case 4: ux = oy; uy = ox; break;
                default: ux = ox; uy = oy; break;
            }
            image->pixels[static_cast<std::size_t>(oy * image->width + ox)] = source->pixels[static_cast<std::size_t>((y + uy) * source->width + x + ux)];
        }
    return image;
}

Ref<Image> Image::createImage(const Ref<InputStream>& stream) {
    if (!stream.p) throwNullPointer();
    std::vector<std::uint8_t> bytes;
    for (;;) {
        jint b = stream->read();
        if (b < 0) break;
        bytes.push_back(static_cast<std::uint8_t>(b));
    }
    return imageFromPng(bytes.data(), bytes.size());
}

Ref<Image> DirectUtils::createImage(jint w, jint h, jint argb) {
    Ref<Image> image = Image::createImage(w, h);
    std::fill(image->pixels.begin(), image->pixels.end(), static_cast<std::uint32_t>(argb));
    image->hasAlpha = true;
    return image;
}

// ---------------------------------------------------------------- fonts

namespace {
struct FontFace {
    int height = 0, ascent = 0;
    std::vector<int> advance;                       // per UTF-16 code unit < 0x2000, -1 = not yet measured
    std::vector<std::vector<std::uint8_t>> glyph;   // coverage masks, (cell width) x height
};
struct FontState {
    int px[3] = {12, 14, 16};                       // small, medium, large
    std::string face = "Tahoma";
    std::map<int, FontFace> faces;                  // key: size index * 8 + style bits
} g_fonts;

int sizeIndex(jint size) { return size == Font::SIZE_SMALL ? 0 : size == Font::SIZE_LARGE ? 2 : 1; }
} // namespace

#ifdef _WIN32
} // namespace pop
#ifndef NOMINMAX
#define NOMINMAX
#endif
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#include <windows.h>
namespace pop {
namespace {
// Rasterizes one face with GDI: no anti-aliasing, like the phone's bitmap system fonts.
struct GdiFont {
    HDC dc = nullptr;
    HFONT font = nullptr;
    HBITMAP bitmap = nullptr;
    void* bits = nullptr;
    int cellW = 0, cellH = 0;
    ~GdiFont() {
        if (dc) DeleteDC(dc);
        if (font) DeleteObject(font);
        if (bitmap) DeleteObject(bitmap);
    }
};
std::map<int, std::unique_ptr<GdiFont>> g_gdi;

GdiFont& gdiFont(int key) {
    auto& slot = g_gdi[key];
    if (slot) return *slot;
    slot = std::make_unique<GdiFont>();
    GdiFont& f = *slot;
    const int style = key & 7, px = g_fonts.px[key >> 3];
    std::wstring face(g_fonts.face.begin(), g_fonts.face.end());
    f.font = CreateFontW(-px, 0, 0, 0, (style & Font::STYLE_BOLD) ? FW_BOLD : FW_NORMAL, (style & Font::STYLE_ITALIC) ? TRUE : FALSE,
                         FALSE, FALSE, DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, NONANTIALIASED_QUALITY,
                         DEFAULT_PITCH | FF_SWISS, face.c_str());
    f.dc = CreateCompatibleDC(nullptr);
    SelectObject(f.dc, f.font);
    f.cellW = px * 3;
    f.cellH = px * 2;
    BITMAPINFO bi = {};
    bi.bmiHeader.biSize = sizeof(bi.bmiHeader);
    bi.bmiHeader.biWidth = f.cellW;
    bi.bmiHeader.biHeight = -f.cellH;
    bi.bmiHeader.biPlanes = 1;
    bi.bmiHeader.biBitCount = 32;
    bi.bmiHeader.biCompression = BI_RGB;
    f.bitmap = CreateDIBSection(f.dc, &bi, DIB_RGB_COLORS, &f.bits, nullptr, 0);
    SelectObject(f.dc, f.bitmap);
    SetBkMode(f.dc, OPAQUE);
    SetBkColor(f.dc, RGB(0, 0, 0));
    SetTextColor(f.dc, RGB(255, 255, 255));
    SetTextAlign(f.dc, TA_TOP | TA_LEFT);
    return f;
}

FontFace& fontFace(int key) {
    auto it = g_fonts.faces.find(key);
    if (it != g_fonts.faces.end()) return it->second;
    FontFace& ff = g_fonts.faces[key];
    GdiFont& f = gdiFont(key);
    TEXTMETRICW tm;
    GetTextMetricsW(f.dc, &tm);
    ff.height = tm.tmHeight;
    ff.ascent = tm.tmAscent;
    ff.advance.assign(0x2000, -1);
    ff.glyph.resize(0x2000);
    return ff;
}

void measure(int key, FontFace& ff, char16_t c) {
    if (c >= 0x2000 || ff.advance[c] >= 0) return;
    GdiFont& f = gdiFont(key);
    wchar_t wc = static_cast<wchar_t>(c);
    SIZE size = {};
    GetTextExtentPoint32W(f.dc, &wc, 1, &size);
    const int w = std::min<int>(size.cx, f.cellW);
    ff.advance[c] = w;
    std::memset(f.bits, 0, static_cast<std::size_t>(f.cellW) * f.cellH * 4);
    TextOutW(f.dc, 0, 0, &wc, 1);
    GdiFlush();
    auto& mask = ff.glyph[c];
    // italic glyphs overhang their advance; keep the cell width they may touch
    const int cw = std::min(f.cellW, w + ff.height / 2 + 1);
    mask.assign(static_cast<std::size_t>(cw) * ff.height, 0);
    const std::uint32_t* px = static_cast<const std::uint32_t*>(f.bits);
    for (int y = 0; y < ff.height && y < f.cellH; ++y)
        for (int x = 0; x < cw; ++x)
            mask[static_cast<std::size_t>(y * cw + x)] = (px[y * f.cellW + x] & 0xff) > 127 ? 1 : 0;
}
} // namespace
#else
namespace {
FontFace& fontFace(int key) {
    FontFace& ff = g_fonts.faces[key];
    if (ff.advance.empty()) {
        ff.height = g_fonts.px[key >> 3] + 2;
        ff.ascent = g_fonts.px[key >> 3];
        ff.advance.assign(0x2000, -1);
        ff.glyph.resize(0x2000);
    }
    return ff;
}
void measure(int, FontFace& ff, char16_t c) {
    if (c < 0x2000 && ff.advance[c] < 0) ff.advance[c] = (ff.height * 5) / 9;
}
} // namespace
#endif

void fontConfigure(int smallPx, int mediumPx, int largePx, const char* faceName) {
    g_fonts.px[0] = smallPx;
    g_fonts.px[1] = mediumPx;
    g_fonts.px[2] = largePx;
    g_fonts.face = faceName;
    g_fonts.faces.clear();
#ifdef _WIN32
    g_gdi.clear();
#endif
}

namespace {
int fontKey(const Font* f) { return sizeIndex(f ? f->size : 0) * 8 + ((f ? f->style : 0) & 3); }
int advanceOf(int key, char16_t c) {
    FontFace& ff = fontFace(key);
    if (c >= 0x2000) c = u'?';
    measure(key, ff, c);
    return ff.advance[c];
}
} // namespace

Ref<Font> Font::getFont(jint face, jint style, jint size) {
    Ref<Font> f = jnew<Font>();
    f->face = face;
    f->style = style;
    f->size = size;
    return f;
}
jint Font::getHeight() { return fontFace(fontKey(this)).height; }
jint Font::getBaselinePosition() { return fontFace(fontKey(this)).ascent; }
jint Font::charWidth(jchar c) { return advanceOf(fontKey(this), c); }
jint Font::charsWidth(const Arr<jchar>& chars, jint offset, jint length) {
    jint w = 0;
    for (jint i = 0; i < length; ++i) w += charWidth(chars[offset + i]);
    return w;
}
jint Font::stringWidth(const Ref<String>& s) {
    if (!s.p) throwNullPointer();
    jint w = 0;
    for (char16_t c : s->s) w += charWidth(c);
    return w;
}
jint Font::substringWidth(const Ref<String>& s, jint offset, jint length) {
    if (!s.p) throwNullPointer();
    if (offset < 0 || length < 0 || offset + length > s->length()) throwIndex(offset, s->length());
    jint w = 0;
    for (jint i = 0; i < length; ++i) w += charWidth(s->s[static_cast<std::size_t>(offset + i)]);
    return w;
}

Ref<Font> Graphics::getFont() {
    if (!font.p) font = Font::getDefaultFont();
    return font;
}

void Graphics::drawText(const std::u16string& text, jint x, jint y, jint anchor) {
    Ref<Font> f = getFont();
    const int key = fontKey(f.get());
    FontFace& ff = fontFace(key);
    jint width = 0;
    for (char16_t c : text) width += advanceOf(key, c);
    if (anchor & HCENTER) x -= width / 2;
    else if (anchor & RIGHT) x -= width;
    if (anchor & BOTTOM) y -= ff.height;
    else if (anchor & BASELINE) y -= ff.ascent;
    else if (anchor & VCENTER) y -= ff.height / 2;
    x += tx;
    y += ty;
    if (recordingTo(this)) recordTextOp(*this, text, x, y);
    const Bounds b = bounds(*this);
    std::uint32_t* pixels = target.get()->pixels.data();
    const int stride = target.get()->width;
    jint pen = x;
    for (char16_t c : text) {
        if (c >= 0x2000) c = u'?';
        measure(key, ff, c);
        const auto& mask = ff.glyph[c];
        const int cw = ff.height ? static_cast<int>(mask.size()) / ff.height : 0;
        for (int gy = 0; gy < ff.height; ++gy) {
            const jint py = y + gy;
            if (py < b.y1 || py >= b.y2) continue;
            for (int gx = 0; gx < cw; ++gx) {
                const jint px = pen + gx;
                if (px < b.x1 || px >= b.x2 || !mask[static_cast<std::size_t>(gy * cw + gx)]) continue;
                blend(pixels[static_cast<std::size_t>(py) * stride + px], color);
            }
        }
        pen += ff.advance[c];
    }
    if (f->style & Font::STYLE_UNDERLINED) span(x, x + width - 1, y + ff.ascent + 1);
}

void Graphics::drawString(const Ref<String>& s, jint x, jint y, jint anchor) {
    if (!s.p) throwNullPointer();
    drawText(s->s, x, y, anchor);
}
void Graphics::drawSubstring(const Ref<String>& s, jint offset, jint length, jint x, jint y, jint anchor) {
    if (!s.p) throwNullPointer();
    if (offset < 0 || length < 0 || offset + length > s->length()) throwIndex(offset, s->length());
    drawText(s->s.substr(static_cast<std::size_t>(offset), static_cast<std::size_t>(length)), x, y, anchor);
}
void Graphics::drawChar(jchar c, jint x, jint y, jint anchor) { drawText(std::u16string(1, c), x, y, anchor); }
void Graphics::drawChars(const Arr<jchar>& chars, jint offset, jint length, jint x, jint y, jint anchor) {
    std::u16string s;
    for (jint i = 0; i < length; ++i) s.push_back(chars[offset + i]);
    drawText(s, x, y, anchor);
}

// ---------------------------------------------------------------- more Graphics

void Graphics::fillRoundRect(jint x, jint y, jint w, jint h, jint aw, jint ah) {
    if (w <= 0 || h <= 0) return;
    const jint rx = std::min(aw / 2, w / 2), ry = std::min(ah / 2, h / 2);
    for (jint row = 0; row < h; ++row) {
        jint inset = 0;
        const jint dy = row < ry ? ry - row : row >= h - ry ? row - (h - ry - 1) : 0;
        if (dy > 0 && ry > 0) inset = rx - static_cast<jint>(std::lround(rx * std::sqrt(std::max(0.0, 1.0 - double(dy) * dy / (double(ry) * ry)))));
        fillRect(x + inset, y + row, w - 2 * inset, 1);
    }
}
void Graphics::drawRoundRect(jint x, jint y, jint w, jint h, jint aw, jint ah) {
    if (w < 0 || h < 0) return;
    const jint rx = std::min(aw / 2, w / 2), ry = std::min(ah / 2, h / 2);
    drawLine(x + rx, y, x + w - rx, y);
    drawLine(x + rx, y + h, x + w - rx, y + h);
    drawLine(x, y + ry, x, y + h - ry);
    drawLine(x + w, y + ry, x + w, y + h - ry);
    if (rx > 0 && ry > 0) {
        drawArc(x, y, 2 * rx, 2 * ry, 90, 90);
        drawArc(x + w - 2 * rx, y, 2 * rx, 2 * ry, 0, 90);
        drawArc(x, y + h - 2 * ry, 2 * rx, 2 * ry, 180, 90);
        drawArc(x + w - 2 * rx, y + h - 2 * ry, 2 * rx, 2 * ry, 270, 90);
    }
}
void Graphics::copyArea(jint sx, jint sy, jint w, jint h, jint dx, jint dy, jint anchor) {
    if (w <= 0 || h <= 0) return;
    if (anchor & HCENTER) dx -= w / 2; else if (anchor & RIGHT) dx -= w;
    if (anchor & VCENTER) dy -= h / 2; else if (anchor & BOTTOM) dy -= h;
    Image* img = target.get();
    std::vector<std::uint32_t> copy(static_cast<std::size_t>(w) * h, 0);
    for (jint r = 0; r < h; ++r)
        for (jint c = 0; c < w; ++c) {
            const jint px = sx + tx + c, py = sy + ty + r;
            if (px >= 0 && py >= 0 && px < img->width && py < img->height) copy[static_cast<std::size_t>(r * w + c)] = img->pixels[static_cast<std::size_t>(py * img->width + px)];
        }
    const Bounds b = bounds(*this);
    for (jint r = 0; r < h; ++r)
        for (jint c = 0; c < w; ++c) {
            const jint px = dx + tx + c, py = dy + ty + r;
            if (px >= b.x1 && py >= b.y1 && px < b.x2 && py < b.y2) img->pixels[static_cast<std::size_t>(py * img->width + px)] = copy[static_cast<std::size_t>(r * w + c)];
        }
}

// ---------------------------------------------------------------- Nokia DirectGraphics

jint nokiaTransform(jint manipulation) {
    // Nokia: flips first, then a counter-clockwise rotation. As (mirror, clockwise rotation):
    bool mirror = (manipulation & DirectGraphics::FLIP_HORIZONTAL) != 0;
    int ccw = manipulation & 0x1ff;
    if (manipulation & DirectGraphics::FLIP_VERTICAL) { mirror = !mirror; ccw += 180; }
    const int cw = (360 - (ccw % 360)) % 360;
    static const jint plain[4] = {0, 5, 3, 6}, mirrored[4] = {2, 7, 1, 4};
    return (mirror ? mirrored : plain)[cw / 90];
}

void DirectGraphics::drawImage(const Ref<Image>& image, jint x, jint y, jint anchor, jint manipulation) {
    if (!image.p) throwNullPointer();
    g->drawRegion(image, 0, 0, image->width, image->height, nokiaTransform(manipulation), x, y, anchor);
}

static void fillPolygonImpl(Graphics& g, const std::vector<std::pair<double, double>>& pts) {
    if (pts.size() < 3) return;
    double minY = pts[0].second, maxY = pts[0].second;
    for (const auto& p : pts) { minY = std::min(minY, p.second); maxY = std::max(maxY, p.second); }
    for (jint y = static_cast<jint>(std::floor(minY)); y <= static_cast<jint>(std::ceil(maxY)); ++y) {
        std::vector<double> xs;
        const double sy = y + 0.5;
        for (std::size_t i = 0; i < pts.size(); ++i) {
            const auto& a = pts[i];
            const auto& b = pts[(i + 1) % pts.size()];
            if ((a.second <= sy && b.second > sy) || (b.second <= sy && a.second > sy))
                xs.push_back(a.first + (sy - a.second) * (b.first - a.first) / (b.second - a.second));
        }
        std::sort(xs.begin(), xs.end());
        for (std::size_t i = 0; i + 1 < xs.size(); i += 2) {
            const jint x1 = static_cast<jint>(std::ceil(xs[i] - 0.5)), x2 = static_cast<jint>(std::floor(xs[i + 1] - 0.5));
            if (x2 >= x1) g.span(x1, x2, y);
        }
    }
}

void DirectGraphics::fillPolygon(const Arr<jint>& xs, jint xOffset, const Arr<jint>& ys, jint yOffset, jint n, jint argb) {
    std::vector<std::pair<double, double>> pts;
    for (jint i = 0; i < n; ++i) pts.emplace_back(xs[xOffset + i] + g->tx, ys[yOffset + i] + g->ty);
    const std::uint32_t save = g->color;
    g->color = static_cast<std::uint32_t>(argb);
    fillPolygonImpl(*g.get(), pts);
    g->color = save;
}
void DirectGraphics::drawPolygon(const Arr<jint>& xs, jint xOffset, const Arr<jint>& ys, jint yOffset, jint n, jint argb) {
    const std::uint32_t save = g->color;
    g->color = static_cast<std::uint32_t>(argb);
    for (jint i = 0; i < n; ++i) g->drawLine(xs[xOffset + i], ys[yOffset + i], xs[xOffset + (i + 1) % n], ys[yOffset + (i + 1) % n]);
    g->color = save;
}
void DirectGraphics::fillTriangle(jint x1, jint y1, jint x2, jint y2, jint x3, jint y3, jint argb) {
    const std::uint32_t save = g->color;
    g->color = static_cast<std::uint32_t>(argb);
    g->fillTriangle(x1, y1, x2, y2, x3, y3);
    g->color = save;
}
void DirectGraphics::drawTriangle(jint x1, jint y1, jint x2, jint y2, jint x3, jint y3, jint argb) {
    const std::uint32_t save = g->color;
    g->color = static_cast<std::uint32_t>(argb);
    g->drawLine(x1, y1, x2, y2);
    g->drawLine(x2, y2, x3, y3);
    g->drawLine(x3, y3, x1, y1);
    g->color = save;
}

static std::uint32_t fromNative(std::uint32_t v, jint format, bool transparency) {
    std::uint32_t argb;
    switch (format) {
        case DirectGraphics::TYPE_USHORT_4444_ARGB: {
            const std::uint32_t a = (v >> 12) & 15, r = (v >> 8) & 15, gg = (v >> 4) & 15, b = v & 15;
            argb = (a * 17) << 24 | (r * 17) << 16 | (gg * 17) << 8 | (b * 17);
            break;
        }
        case DirectGraphics::TYPE_USHORT_565_RGB: {
            const std::uint32_t r = (v >> 11) & 31, gg = (v >> 5) & 63, b = v & 31;
            argb = 0xff000000u | ((r * 255 / 31) << 16) | ((gg * 255 / 63) << 8) | (b * 255 / 31);
            break;
        }
        default: argb = v; break;
    }
    return transparency ? argb : (argb | 0xff000000u);
}

template<class T> static void drawPixelsImpl(DirectGraphics& dg, const Arr<T>& pixels, bool transparency, jint offset, jint scan, jint x, jint y, jint w, jint h, jint manipulation, jint format) {
    if (!pixels.p) throwNullPointer();
    if (w <= 0 || h <= 0) return;
    Ref<Image> tmp = jnew<Image>();
    tmp->width = w;
    tmp->height = h;
    tmp->hasAlpha = true;
    tmp->pixels.resize(static_cast<std::size_t>(w) * h);
    for (jint r = 0; r < h; ++r)
        for (jint c = 0; c < w; ++c)
            tmp->pixels[static_cast<std::size_t>(r * w + c)] = fromNative(static_cast<std::uint32_t>(static_cast<std::make_unsigned_t<T>>(pixels[offset + r * scan + c])), format, transparency);
    dg.g->drawRegion(tmp, 0, 0, w, h, nokiaTransform(manipulation), x, y, 20);
}
void DirectGraphics::drawPixels(const Arr<jint>& pixels, jboolean transparency, jint offset, jint scan, jint x, jint y, jint w, jint h, jint manipulation, jint format) {
    drawPixelsImpl(*this, pixels, transparency, offset, scan, x, y, w, h, manipulation, format);
}
void DirectGraphics::drawPixels(const Arr<jshort>& pixels, jboolean transparency, jint offset, jint scan, jint x, jint y, jint w, jint h, jint manipulation, jint format) {
    drawPixelsImpl(*this, pixels, transparency, offset, scan, x, y, w, h, manipulation, format);
}
void DirectGraphics::getPixels(const Arr<jint>& pixels, jint offset, jint scan, jint x, jint y, jint w, jint h, jint) {
    Image* img = g->target.get();
    for (jint r = 0; r < h; ++r)
        for (jint c = 0; c < w; ++c) {
            const jint px = x + g->tx + c, py = y + g->ty + r;
            pixels[offset + r * scan + c] = (px >= 0 && py >= 0 && px < img->width && py < img->height) ? static_cast<jint>(img->pixels[static_cast<std::size_t>(py * img->width + px)]) : 0;
        }
}
void DirectGraphics::getPixels(const Arr<jshort>& pixels, jint offset, jint scan, jint x, jint y, jint w, jint h, jint format) {
    Image* img = g->target.get();
    for (jint r = 0; r < h; ++r)
        for (jint c = 0; c < w; ++c) {
            const jint px = x + g->tx + c, py = y + g->ty + r;
            const std::uint32_t v = (px >= 0 && py >= 0 && px < img->width && py < img->height) ? img->pixels[static_cast<std::size_t>(py * img->width + px)] : 0;
            std::uint32_t out;
            if (format == TYPE_USHORT_565_RGB) out = (((v >> 19) & 31) << 11) | (((v >> 10) & 63) << 5) | ((v >> 3) & 31);
            else out = (((v >> 28) & 15) << 12) | (((v >> 20) & 15) << 8) | (((v >> 12) & 15) << 4) | ((v >> 4) & 15);
            pixels[offset + r * scan + c] = static_cast<jshort>(out);
        }
}

// ---------------------------------------------------------------- Canvas

jint Canvas::getGameAction(jint keyCode) {
    switch (keyCode) {
        case -1: case KEY_NUM2: return UP;
        case -2: case KEY_NUM8: return DOWN;
        case -3: case KEY_NUM4: return LEFT;
        case -4: case KEY_NUM6: return RIGHT;
        case -5: case KEY_NUM5: return FIRE;
        case KEY_NUM7: return GAME_A;
        case KEY_NUM9: return GAME_B;
        case KEY_STAR: return GAME_C;
        case KEY_POUND: return GAME_D;
        default: return 0;
    }
}
jint Canvas::getKeyCode(jint gameAction) {
    switch (gameAction) {
        case UP: return -1;
        case DOWN: return -2;
        case LEFT: return -3;
        case RIGHT: return -4;
        case FIRE: return -5;
        case GAME_A: return KEY_NUM7;
        case GAME_B: return KEY_NUM9;
        case GAME_C: return KEY_STAR;
        case GAME_D: return KEY_POUND;
        default: return 0;
    }
}

// ---------------------------------------------------------------- audio

namespace {
// The one source the host is playing, so getState() of a source that was replaced reports stopped.
const Object* g_audioOwner = nullptr;
}

void Sound::init(const Arr<jbyte>& bytes, jint format) {
    if (!bytes.p) throwNullPointer();
    data.assign(reinterpret_cast<const std::uint8_t*>(bytes.p->data), reinterpret_cast<const std::uint8_t*>(bytes.p->data) + bytes.p->len);
    type = format;
}
void Sound::init(jint, jlong) {
    data.clear();
    type = FORMAT_TONE;
}
void Sound::play(jint loops) {
    if (data.empty()) return;
    hostAudioStart(data, type == FORMAT_WAV ? AudioWave : AudioTone, loops <= 0 ? -1 : loops, gain);
    g_audioOwner = this;
    started = true;
}
void Sound::stop() {
    if (started && g_audioOwner == this) hostAudioStop();
    started = false;
}
jint Sound::getState() {
    if (data.empty()) return SOUND_UNINITIALIZED;
    if (started && (g_audioOwner != this || !hostAudioPlaying())) started = false;
    return started ? SOUND_PLAYING : SOUND_STOPPED;
}

void Player::start() {
    if (state == CLOSED) throwJava("java.lang.IllegalStateException", "closed player");
    const int gain = volume->muted ? 0 : volume->level * 255 / 100;
    hostAudioStart(data, kind, loops, gain);
    g_audioOwner = this;
    state = STARTED;
}
void Player::stop() {
    if (state == STARTED) {
        if (g_audioOwner == this) hostAudioStop();
        state = PREFETCHED;
    }
}
void Player::close() {
    if (state == STARTED && g_audioOwner == this) hostAudioStop();
    state = CLOSED;
}
jint Player::getState() {
    if (state == STARTED && (g_audioOwner != this || !hostAudioPlaying())) state = PREFETCHED;
    return state;
}
Ref<Control> Player::getControl(const Ref<String>& name) {
    if (name.p && name->s.find(u"VolumeControl") != std::u16string::npos) return volume;
    return nullptr;
}

Ref<Player> Manager::createPlayer(const Ref<InputStream>& stream, const Ref<String>& type) {
    if (!stream.p) throwNullPointer();
    Ref<Player> player = jnew<Player>();
    for (;;) {
        jint b = stream->read();
        if (b < 0) break;
        player->data.push_back(static_cast<std::uint8_t>(b));
    }
    const bool riff = player->data.size() >= 4 && std::memcmp(player->data.data(), "RIFF", 4) == 0;
    const bool wavType = type.p && type->s.find(u"wav") != std::u16string::npos;
    player->kind = (riff || wavType) ? AudioWave : AudioMidi;
    return player;
}

// ---------------------------------------------------------------- record stores

// File layout: "PRMS" version(1) nextId(4) count(4) { id(4) length(4) bytes }.
Ref<RecordStore> RecordStore::openRecordStore(const Ref<String>& name, jboolean create) {
    if (!name.p) throwNullPointer();
    Ref<RecordStore> store = jnew<RecordStore>();
    store->name = jutf8(name);
    while (!store->name.empty() && store->name[0] == '/') store->name.erase(0, 1);
    std::vector<std::uint8_t> bytes;
    if (hostLoadSave(store->name, bytes)) {
        auto rd = [&](std::size_t at) -> std::uint32_t {
            return at + 4 <= bytes.size() ? (std::uint32_t(bytes[at]) << 24 | std::uint32_t(bytes[at + 1]) << 16 | std::uint32_t(bytes[at + 2]) << 8 | bytes[at + 3]) : 0;
        };
        if (bytes.size() >= 13 && std::memcmp(bytes.data(), "PRMS", 4) == 0) {
            store->nextId = static_cast<jint>(rd(5));
            const std::uint32_t count = rd(9);
            std::size_t at = 13;
            for (std::uint32_t i = 0; i < count && at + 8 <= bytes.size(); ++i) {
                const jint id = static_cast<jint>(rd(at));
                const std::uint32_t len = rd(at + 4);
                at += 8;
                if (at + len > bytes.size()) break;
                store->records.emplace_back(id, std::vector<std::uint8_t>(bytes.begin() + static_cast<std::ptrdiff_t>(at), bytes.begin() + static_cast<std::ptrdiff_t>(at + len)));
                at += len;
            }
        }
    } else if (!create) {
        throwJava("javax.microedition.rms.RecordStoreNotFoundException", store->name);
    } else {
        store->persist(); // creating a store makes it exist, empty, for later opens without create
    }
    return store;
}
void RecordStore::deleteRecordStore(const Ref<String>& name) {
    if (!name.p) throwNullPointer();
    std::string n = jutf8(name);
    while (!n.empty() && n[0] == '/') n.erase(0, 1);
    if (!hostDeleteSave(n)) throwJava("javax.microedition.rms.RecordStoreNotFoundException", n);
}
void RecordStore::persist() {
    std::vector<std::uint8_t> out = {'P', 'R', 'M', 'S', 1};
    auto put = [&](std::uint32_t v) { for (int s = 24; s >= 0; s -= 8) out.push_back(static_cast<std::uint8_t>(v >> s)); };
    put(static_cast<std::uint32_t>(nextId));
    put(static_cast<std::uint32_t>(records.size()));
    for (const auto& r : records) {
        put(static_cast<std::uint32_t>(r.first));
        put(static_cast<std::uint32_t>(r.second.size()));
        out.insert(out.end(), r.second.begin(), r.second.end());
    }
    if (!hostStoreSave(name, out)) throwJava("javax.microedition.rms.RecordStoreException", "save failed");
}
std::vector<std::uint8_t>* RecordStore::find(jint id) {
    for (auto& r : records) if (r.first == id) return &r.second;
    return nullptr;
}
jint RecordStore::getSize() {
    jint n = 0;
    for (const auto& r : records) n += static_cast<jint>(r.second.size());
    return n;
}
jint RecordStore::addRecord(const Arr<jbyte>& data, jint offset, jint length) {
    std::vector<std::uint8_t> bytes;
    if (length > 0) {
        if (!data.p) throwNullPointer();
        if (offset < 0 || length > data.length() - offset) throwIndex(offset, data.length());
        bytes.assign(reinterpret_cast<const std::uint8_t*>(data.p->data) + offset, reinterpret_cast<const std::uint8_t*>(data.p->data) + offset + length);
    }
    const jint id = nextId++;
    records.emplace_back(id, std::move(bytes));
    persist();
    return id;
}
void RecordStore::setRecord(jint id, const Arr<jbyte>& data, jint offset, jint length) {
    std::vector<std::uint8_t>* r = find(id);
    if (!r) throwJava("javax.microedition.rms.InvalidRecordIDException", std::to_string(id));
    if (length > 0) {
        if (!data.p) throwNullPointer();
        if (offset < 0 || length > data.length() - offset) throwIndex(offset, data.length());
        r->assign(reinterpret_cast<const std::uint8_t*>(data.p->data) + offset, reinterpret_cast<const std::uint8_t*>(data.p->data) + offset + length);
    } else {
        r->clear();
    }
    persist();
}
void RecordStore::deleteRecord(jint id) {
    for (auto it = records.begin(); it != records.end(); ++it)
        if (it->first == id) { records.erase(it); persist(); return; }
    throwJava("javax.microedition.rms.InvalidRecordIDException", std::to_string(id));
}
jint RecordStore::getRecord(jint id, const Arr<jbyte>& out, jint offset) {
    std::vector<std::uint8_t>* r = find(id);
    if (!r) throwJava("javax.microedition.rms.InvalidRecordIDException", std::to_string(id));
    if (!out.p) throwNullPointer();
    if (offset < 0 || static_cast<jint>(r->size()) > out.length() - offset) throwIndex(offset, out.length());
    if (!r->empty()) std::memcpy(out.p->data + offset, r->data(), r->size());
    return static_cast<jint>(r->size());
}
Arr<jbyte> RecordStore::getRecord(jint id) {
    std::vector<std::uint8_t>* r = find(id);
    if (!r) throwJava("javax.microedition.rms.InvalidRecordIDException", std::to_string(id));
    if (r->empty()) return nullptr;
    Arr<jbyte> a = Arr<jbyte>::make(static_cast<jint>(r->size()));
    std::memcpy(a.p->data, r->data(), r->size());
    return a;
}
jint RecordStore::getRecordSize(jint id) {
    std::vector<std::uint8_t>* r = find(id);
    if (!r) throwJava("javax.microedition.rms.InvalidRecordIDException", std::to_string(id));
    return static_cast<jint>(r->size());
}
Ref<RecordEnumeration> RecordStore::enumerateRecords(const Ref<Object>&, const Ref<Object>&, jboolean) {
    Ref<RecordEnumeration> e = jnew<RecordEnumeration>();
    e->store = Ref<RecordStore>(this);
    // the reference implementation enumerates newest first
    for (auto it = records.rbegin(); it != records.rend(); ++it) e->ids.push_back(it->first);
    return e;
}
jint RecordEnumeration::nextRecordId() {
    if (pos >= ids.size()) throwJava("javax.microedition.rms.InvalidRecordIDException", "end of enumeration");
    return ids[pos++];
}

// ---------------------------------------------------------------- MIDlet

Ref<String> MIDlet::getAppProperty(const Ref<String>& key) {
    if (!key.p) throwNullPointer();
    const std::string v = hostAppProperty(jutf8(key));
    if (v.empty()) return nullptr;
    return jstr(v);
}
void MIDlet::notifyDestroyed() { hostQuit(); }

} // namespace pop

namespace pop {
// Font metrics as the oracle (tools/reference_checks) reads them: one line per size and style,
// "size style height ascent" and the advance of characters 0..255.
std::string fontMetrics() {
    std::string out;
    const jint sizes[3] = {Font::SIZE_SMALL, Font::SIZE_MEDIUM, Font::SIZE_LARGE};
    for (jint size : sizes)
        for (jint style = 0; style < 4; ++style) {
            Ref<Font> f = Font::getFont(0, style, size);
            out += std::to_string(size) + " " + std::to_string(style) + " " + std::to_string(f->getHeight()) + " " + std::to_string(f->getBaselinePosition());
            for (int c = 0; c < 256; ++c) out += " " + std::to_string(f->charWidth(static_cast<jchar>(c)));
            out += "\n";
        }
    return out;
}
} // namespace pop
