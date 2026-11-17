#!/usr/bin/env python3
"""Draw the launcher's banner and icon: original artwork, generated, no game data.

The launcher (sot/port/launcher) embeds a banner JPEG and an .ico. They are painted here from
scratch with Pillow: a desert palace at dusk under a large moon, dunes, drifting sand, and an
hourglass for the icon. Nothing is taken from the game or its publishers; the palette and the
skyline are this script's own, and the title is set in a system font.

    python tools/make_art.py            # writes sot/port/launcher/assets/banner.jpg and pop_sot.ico

Needs Pillow (pip install pillow).
"""
import math
import random
import struct
import io
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "sot" / "port" / "launcher" / "assets"

# Per game: sky gradient (top, horizon), dune colours, moon, accent, title lines.
STYLE = {
    "sky": [(18, 14, 48), (92, 40, 86), (226, 120, 64)],
    "dunes": [(122, 62, 46), (88, 42, 38), (52, 26, 30)],
    "palace": (24, 14, 26),
    "moon": (255, 226, 170),
    "accent": (255, 196, 92),
    "title": "PRINCE OF PERSIA",
    "subtitle": "THE SANDS OF TIME",
    "seed": 2003,
}
W, H = 1200, 896


def lerp(a, b, t):
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))


def gradient(stops, t):
    t = max(0.0, min(1.0, t))
    seg = t * (len(stops) - 1)
    i = min(int(seg), len(stops) - 2)
    return lerp(stops[i], stops[i + 1], seg - i)


def font(size, bold=True):
    for name in (("georgiab.ttf" if bold else "georgia.ttf"), ("timesbd.ttf" if bold else "times.ttf"), "arialbd.ttf", "arial.ttf"):
        try:
            return ImageFont.truetype(name, size)
        except OSError:
            continue
    return ImageFont.load_default()


def palace(draw, base, x0, scale, color):
    """A skyline of domes, towers and minarets standing on y = base."""
    def dome(cx, w, top):
        r = w / 2
        draw.rectangle([cx - r, top + r * 0.9, cx + r, base], fill=color)
        draw.ellipse([cx - r, top, cx + r, top + 1.8 * r], fill=color)
        draw.polygon([(cx - r * 0.12, top + r * 0.2), (cx, top - r * 0.55), (cx + r * 0.12, top + r * 0.2)], fill=color)

    def minaret(cx, w, top):
        draw.rectangle([cx - w / 2, top, cx + w / 2, base], fill=color)
        draw.rectangle([cx - w * 0.85, top + w * 1.2, cx + w * 0.85, top + w * 1.6], fill=color)
        draw.ellipse([cx - w * 0.75, top - w * 1.1, cx + w * 0.75, top + w * 0.4], fill=color)
        draw.polygon([(cx - w * 0.1, top - w), (cx, top - w * 2.4), (cx + w * 0.1, top - w)], fill=color)

    s = scale
    draw.rectangle([x0 - 260 * s, base - 120 * s, x0 + 260 * s, base], fill=color)
    dome(x0, 220 * s, base - 330 * s)
    dome(x0 - 170 * s, 110 * s, base - 210 * s)
    dome(x0 + 170 * s, 110 * s, base - 210 * s)
    minaret(x0 - 300 * s, 34 * s, base - 420 * s)
    minaret(x0 + 300 * s, 34 * s, base - 420 * s)
    for k in range(-5, 6):
        ax = x0 + k * 44 * s
        draw.rectangle([ax - 12 * s, base - 150 * s, ax + 12 * s, base - 120 * s], fill=color)


def banner(style):
    rnd = random.Random(style["seed"])
    img = Image.new("RGB", (W, H))
    px = img.load()
    horizon = int(H * 0.62)
    for y in range(H):
        c = gradient(style["sky"], y / horizon)
        for x in range(W):
            px[x, y] = c
    # stars
    d = ImageDraw.Draw(img)
    for _ in range(260):
        x, y = rnd.randrange(W), rnd.randrange(int(horizon * 0.7))
        b = rnd.randrange(120, 255)
        d.point((x, y), fill=(b, b, min(255, b + 20)))
    # moon with a soft halo
    glow = Image.new("L", (W, H), 0)
    gd = ImageDraw.Draw(glow)
    mx, my, mr = int(W * 0.72), int(H * 0.26), 92
    gd.ellipse([mx - mr * 2.4, my - mr * 2.4, mx + mr * 2.4, my + mr * 2.4], fill=90)
    glow = glow.filter(ImageFilter.GaussianBlur(60))
    img = Image.composite(Image.new("RGB", (W, H), style["moon"]), img, glow)
    d = ImageDraw.Draw(img)
    d.ellipse([mx - mr, my - mr, mx + mr, my + mr], fill=style["moon"])
    # palace
    palace(d, horizon + 18, int(W * 0.30), 1.0, style["palace"])
    # dunes, back to front
    for i, col in enumerate(style["dunes"]):
        top = horizon + 10 + i * 70
        pts = [(0, H)]
        phase, amp, freq = rnd.uniform(0, 6.28), 26 + i * 12, 0.004 + i * 0.0012
        for x in range(0, W + 8, 8):
            y = top + amp * math.sin(x * freq + phase) + 0.5 * amp * math.sin(x * freq * 2.3 + phase * 1.7)
            pts.append((x, y))
        pts.append((W, H))
        d.polygon(pts, fill=col)
    # drifting sand
    sand = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    sd = ImageDraw.Draw(sand)
    for _ in range(1400):
        x, y = rnd.uniform(0, W), rnd.uniform(horizon - 120, H)
        a = rnd.randrange(40, 140)
        sd.point((x, y), fill=style["accent"] + (a,))
    sand = sand.filter(ImageFilter.GaussianBlur(0.6))
    img = Image.alpha_composite(img.convert("RGBA"), sand).convert("RGB")
    # title, with a dark shadow
    d = ImageDraw.Draw(img)
    f1, f2 = font(96), font(44)
    for text, f, y in ((style["title"], f1, 70), (style["subtitle"], f2, 182)):
        w = d.textlength(text, font=f)
        x = (W - w) / 2
        for ox, oy in ((3, 4), (2, 2)):
            d.text((x + ox, y + oy), text, font=f, fill=(10, 6, 12))
        d.text((x, y), text, font=f, fill=style["accent"] if f is f2 else (250, 236, 210))
    return img


def hourglass(size, style):
    s = size * 4
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    r = s * 0.48
    c = s / 2
    d.ellipse([c - r, c - r, c + r, c + r], fill=style["sky"][0] + (255,), outline=style["accent"] + (255,), width=max(2, s // 28))
    gold, sandc = style["accent"] + (255,), style["moon"] + (255,)
    top, bot, half = s * 0.2, s * 0.8, s * 0.2
    d.rectangle([c - half * 1.25, top - s * 0.04, c + half * 1.25, top], fill=gold)
    d.rectangle([c - half * 1.25, bot, c + half * 1.25, bot + s * 0.04], fill=gold)
    d.polygon([(c - half, top), (c + half, top), (c + s * 0.03, c), (c - s * 0.03, c)], outline=gold, width=max(2, s // 40))
    d.polygon([(c - half, bot), (c + half, bot), (c + s * 0.03, c), (c - s * 0.03, c)], outline=gold, width=max(2, s // 40))
    d.polygon([(c - half * 0.55, top + s * 0.1), (c + half * 0.55, top + s * 0.1), (c, c - s * 0.02)], fill=sandc)
    d.polygon([(c - half * 0.9, bot - s * 0.005), (c + half * 0.9, bot - s * 0.005), (c, bot - s * 0.13)], fill=sandc)
    d.line([(c, c), (c, bot - s * 0.12)], fill=sandc, width=max(1, s // 64))
    return img.resize((size, size), Image.LANCZOS)


def ico(style, path):
    sizes = [16, 24, 32, 48, 64, 128, 256]
    pngs = []
    for z in sizes:
        buf = io.BytesIO()
        hourglass(z, style).save(buf, "PNG")
        pngs.append(buf.getvalue())
    out = bytearray(struct.pack("<HHH", 0, 1, len(sizes)))
    offset = 6 + 16 * len(sizes)
    for z, data in zip(sizes, pngs):
        out += struct.pack("<BBBBHHII", z % 256, z % 256, 0, 0, 1, 32, len(data), offset)
        offset += len(data)
    for data in pngs:
        out += data
    path.write_bytes(bytes(out))


def main():
    ASSETS.mkdir(parents=True, exist_ok=True)
    banner(STYLE).save(ASSETS / "banner.jpg", quality=90)
    ico(STYLE, ASSETS / "pop_sot.ico")
    print("wrote", ASSETS / "banner.jpg", "and", ASSETS / "pop_sot.ico")


if __name__ == "__main__":
    main()
