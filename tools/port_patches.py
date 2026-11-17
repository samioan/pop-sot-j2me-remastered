"""Desktop-port changes, applied to sot/src before translation.

Each entry is (file, old, new, expected_match_count); tools/gen_port.py refuses to run when a count
differs. sot/src itself is never modified. Rules:

  * No field is added to the game's classes (port state lives in tools/port_java).
  * Every helper returns the original value at PortConfig.screenWidth == 176 with the original
    menus; that configuration must stay state-identical to the original bytecode
    (tools/check_engine.py).
  * 176 is never replaced globally. Each site was read and classified: world view (camera clamp,
    centring, tile buffer, activation), full-screen fill/clip, centred interface, or screen edge.
"""

V = "PortConfig.view()"        # width of the world view
S = "PortConfig.screenWidth"   # width of the screen
U = "PortConfig.ui()"          # left edge of a centred 176-wide layout

PORT_PATCHES: list[tuple[str, str, str, int]] = [
    # ------------------------------------------------------------------ world view
    # camera: centre the prince (and the prince/enemy pair) in the view, clamp to the level
    ("Level.java", "            var1 = ((i.e + n.e) / 2 >> 8) - 88 - d;\n",
     f"            var1 = ((i.e + n.e) / 2 >> 8) - {V} / 2 - d;\n", 1),
    ("Level.java", "         var1 = (i.e >> 8) - 88;\n", f"         var1 = (i.e >> 8) - {V} / 2;\n", 1),
    ("Level.java", "      if (d > Game.q[var3] * Game.W - 176) {\n         d = Game.q[var3] * Game.W - 176;\n",
     f"      if (d > Game.q[var3] * Game.W - {V}) {{\n         d = Game.q[var3] * Game.W - {V};\n", 1),
    # actors in a region are active while the region overlaps the view
    ("Level.java", "? var1 + 176 >= r[var4][var3][0]", f"? var1 + {V} >= r[var4][var3][0]", 1),
    # actor layer clip after drawing actors; layer markers for interpolation
    ("Level.java", "      Game.aE.setClip(0, 0, 176, 208);\n      Game.aE.clipRect(0, 0, 176, 208);\n      if (i != null && k) {\n",
     f"      Game.aE.setClip(0, 0, {V}, 208);\n      Game.aE.clipRect(0, 0, {V}, 208);\n      if (i != null && k) {{\n         PortConfig.actor(i);\n", 1),
    ("Level.java", "         if (var2 != null && var2.H[3]) {\n            var2.c(d, l - 0);\n",
     "         if (var2 != null && var2.H[3]) {\n            PortConfig.actor(var2);\n            var2.c(d, l - 0);\n", 1),
    # tile scroll buffer: wide enough for the widest view; columns and blits cover the view
    ("Game.java", "               am = 176 + W * 2 & ~(W - 1);\n", "               am = PortConfig.bufferWidth() + W * 2 & ~(W - 1);\n", 1),
    ("Game.java", "      int var5 = var3 + 176 / W + 1;\n", f"      int var5 = var3 + {V} / W + 1;\n", 1),
    ("Game.java", "      int var10 = (var1 + 176) % am;\n", f"      int var10 = (var1 + {V}) % am;\n", 1),
    ("Game.java", "            this.a(var9, var11, 176, 208, 0, 0);\n", f"            this.a(var9, var11, {V}, 208, 0, 0);\n", 1),
    ("Game.java", "            this.a(var9, var11, 176, 208 - var12, 0, 0);\n            this.a(var9, 0, 176, var12, 0, 208 - var12);\n",
     f"            this.a(var9, var11, {V}, 208 - var12, 0, 0);\n            this.a(var9, 0, {V}, var12, 0, 208 - var12);\n", 1),
    ("Game.java", "         this.a(var9, var11, 176 - var10, 208, 0, 0);\n         this.a(0, var11, var10, 208, 176 - var10, 0);\n",
     f"         this.a(var9, var11, {V} - var10, 208, 0, 0);\n         this.a(0, var11, var10, 208, {V} - var10, 0);\n", 1),
    ("Game.java",
     "         this.a(var9, var11, 176 - var10, 208 - var12, 0, 0);\n         this.a(var9, 0, 176 - var10, var12, 0, 208 - var12);\n"
     "         this.a(0, var11, var10, 208 - var12, 176 - var10, 0);\n         this.a(0, 0, var10, var12, 176 - var10, 208 - var12);\n",
     f"         this.a(var9, var11, {V} - var10, 208 - var12, 0, 0);\n         this.a(var9, 0, {V} - var10, var12, 0, 208 - var12);\n"
     f"         this.a(0, var11, var10, 208 - var12, {V} - var10, 0);\n         this.a(0, 0, var10, var12, {V} - var10, 208 - var12);\n", 1),
    # the world (tiles, then actors) is drawn inside the view, centred between bars when narrower
    ("Game.java",
     "               } else {\n                  this.c(Level.d, Level.l);\n                  ap.b();\n",
     "               } else {\n                  PortConfig.beginWorld(aE);\n                  this.c(Level.d, Level.l);\n                  ap.b();\n"
     "                  PortConfig.endWorld(aE);\n", 1),

    # ------------------------------------------------------------------ interface
    # text: full-screen clip, centring on the screen
    ("Game.java", "         var1 = (176 - C.stringWidth(var0)) / 2;\n      }\n\n      aE.setFont(C);\n      aE.setClip(0, 0, 176, 208);\n      aE.clipRect(0, 0, 176, 208);\n",
     f"         var1 = ({S} - C.stringWidth(var0)) / 2;\n      }}\n\n      aE.setFont(C);\n      aE.setClip(0, 0, {S}, 208);\n      aE.clipRect(0, 0, {S}, 208);\n", 1),
    ("Game.java", "            a(var9, (176 - C.stringWidth(var9)) / 2 + 1, var8 + 1, 4473924, -1);\n",
     f"            a(var9, ({S} - C.stringWidth(var9)) / 2 + 1, var8 + 1, 4473924, -1);\n", 1),
    # right softkey label at the right edge of the screen
    ("Game.java", "         a(var1, 176 - C.stringWidth(var1) - 2, 208 - A, 16711422, 0);\n",
     f"         a(var1, {S} - C.stringWidth(var1) - 2, 208 - A, 16711422, 0);\n", 1),
    # backgrounds: black or the story picture centred, title pictures centred
    ("Game.java",
     "   private void c(int var1) {\n      aE.setClip(0, 0, 176, 208);\n      aE.clipRect(0, 0, 176, 208);\n      switch (var1) {\n"
     "         case 0:\n            aE.setColor(0);\n            aE.fillRect(0, 0, 176, 208);\n            break;\n"
     "         case 1:\n            aE.drawImage(ae[0], 0, 0, 20);\n            break;\n"
     "         case 2:\n            aE.setColor(0);\n            aE.fillRect(0, 0, 176, 208);\n"
     "            aE.drawImage(ae[2], 88, 208, 33);\n            aE.drawImage(ae[1], 88, 0, 17);\n",
     f"   private void c(int var1) {{\n      aE.setClip(0, 0, {S}, 208);\n      aE.clipRect(0, 0, {S}, 208);\n      switch (var1) {{\n"
     f"         case 0:\n            aE.setColor(0);\n            aE.fillRect(0, 0, {S}, 208);\n            break;\n"
     f"         case 1:\n            if ({S} > 176) {{\n               aE.setColor(0);\n               aE.fillRect(0, 0, {S}, 208);\n            }}\n\n"
     f"            aE.drawImage(ae[0], {U}, 0, 20);\n            break;\n"
     f"         case 2:\n            aE.setColor(0);\n            aE.fillRect(0, 0, {S}, 208);\n"
     f"            aE.drawImage(ae[2], {S} / 2, 208, 33);\n            aE.drawImage(ae[1], {S} / 2, 0, 17);\n", 1),
    # Gameloft logo on white
    ("Game.java",
     "                  aE.setClip(0, 0, 176, 208);\n                  aE.clipRect(0, 0, 176, 208);\n                  aE.setColor(255, 255, 255);\n"
     "                  aE.fillRect(0, 0, 176, 208);\n                  aE.drawImage(ae[0], 88, 104, 3);\n",
     f"                  aE.setClip(0, 0, {S}, 208);\n                  aE.clipRect(0, 0, {S}, 208);\n                  aE.setColor(255, 255, 255);\n"
     f"                  aE.fillRect(0, 0, {S}, 208);\n                  aE.drawImage(ae[0], {S} / 2, 104, 3);\n", 1),
    # menu panels (translucent polygons) keep the 176 layout, centred
    ("Game.java", "   void a(int var1, int var2, int var3, int var4) {\n      aE.setClip(0, 0, 176, 208);\n      aE.clipRect(0, 0, 176, 208);\n",
     f"   void a(int var1, int var2, int var3, int var4) {{\n      var1 += {U};\n      aE.setClip(0, 0, {S}, 208);\n      aE.clipRect(0, 0, {S}, 208);\n", 1),
    # the "more" arrow sits in the bottom-right corner; the loading picture is centred
    ("Game.java", "            aE.setClip(161, 197, 14, 8);\n            aE.clipRect(161, 197, 14, 8);\n            aE.drawImage(ao, 61, 173, 20);\n",
     f"            aE.setClip({S} - 15, 197, 14, 8);\n            aE.clipRect({S} - 15, 197, 14, 8);\n            aE.drawImage(ao, {S} - 115, 173, 20);\n", 1),
    ("Game.java", "               aE.setClip(0, 0, 176, 208);\n               aE.clipRect(0, 0, 176, 208);\n               aE.drawImage(ae[0], 88, 188, 33);\n",
     f"               aE.setClip(0, 0, {S}, 208);\n               aE.clipRect(0, 0, {S}, 208);\n               aE.drawImage(ae[0], {S} / 2, 188, 33);\n", 1),
    # loading bar, centred
    ("Game.java",
     "      aE.setClip(16, 150 + A * 2, 144, 5);\n      aE.clipRect(16, 150 + A * 2, 144, 5);\n      aE.setColor(16777215);\n"
     "      aE.drawRect(16, 150 + A * 2, 144, 5);\n      if (var1 > 0) {\n         aE.fillRect(16, 150 + A * 2, var1 * 144 / var2, 5);\n",
     f"      aE.setClip({U} + 16, 150 + A * 2, 144, 5);\n      aE.clipRect({U} + 16, 150 + A * 2, 144, 5);\n      aE.setColor(16777215);\n"
     f"      aE.drawRect({U} + 16, 150 + A * 2, 144, 5);\n      if (var1 > 0) {{\n         aE.fillRect({U} + 16, 150 + A * 2, var1 * 144 / var2, 5);\n", 1),

    # ------------------------------------------------------------------ OPTIONS screen (tools/port_java/PortMenu.java)
    # state 40: update in Game.d(), drawing in Game.paint()
    ("Game.java", "         case 20:\n            if (B && P != -1) {\n               aw = 2;\n            }\n",
     "         case 40:\n            PortMenu.update();\n            break;\n         case 20:\n            if (B && P != -1) {\n               aw = 2;\n            }\n", 1),
    ("Game.java", "            case 6:\n               a(0, \"/gameloft.png\");\n",
     "            case 40:\n               PortMenu.paint(this);\n               break;\n            case 6:\n               a(0, \"/gameloft.png\");\n", 1),
    # the Settings menu (main 16, in-game 17) gains "PC options" before Back once the host installs
    # the desktop menus (PortConfig.settings*)
    ("Game.java",
     "            var4 = 5 * A;\n            this.a(40, 80 - (A >> 1), 96, var4);\n            var2 = (var2 + 4) % 4;\n\n"
     "            for (int var10 = 0; var10 < 4; var10++) {\n               var7 = this.ad[var10];\n",
     "            var4 = (PortConfig.settingsCount() + 1) * A;\n            this.a(40, 80 - (A >> 1), 96, var4);\n"
     "            var2 = (var2 + PortConfig.settingsCount()) % PortConfig.settingsCount();\n\n"
     "            for (int var10 = 0; var10 < PortConfig.settingsCount(); var10++) {\n               var7 = PortConfig.settingsLabel(this.ad, var10);\n", 1),
    ("Game.java", "         case 17:\n            if (ay[6] >= 1) {\n               switch (p) {\n",
     "         case 17:\n            if (ay[6] >= 1) {\n               switch (PortConfig.settingsAction(p)) {\n"
     "                  case 99:\n                     PortMenu.open();\n                     break;\n", 1),
    ("Game.java", "               p = (p + 4 - 1) % 4;\n", "               p = (p + PortConfig.settingsCount() - 1) % PortConfig.settingsCount();\n", 1),
    ("Game.java", "               p = (p + 1) % 4;\n", "               p = (p + 1) % PortConfig.settingsCount();\n", 1),
    # helpers PortMenu draws with (the game's own private routines)
    ("Game.java", "   static void a() {\n      a = 0;\n      P = -1;\n",
     "   static void portText(String var0, int var1, int var2, int var3) {\n      a(var0, var1, var2, var3, 0);\n   }\n\n"
     "   static void portSoftkeys(String var0, String var1) {\n      a(var0, var1);\n   }\n\n"
     "   void portPanel(int var1, int var2, int var3, int var4) {\n      this.a(var1, var2, var3, var4);\n   }\n\n"
     "   void portBackground(int var1) {\n      this.c(var1);\n   }\n\n"
     "   static void portSound(int var0) {\n      a(var0, 1);\n   }\n\n"
     "   static void portMenus() {\n   }\n\n"
     "   static void a() {\n      a = 0;\n      P = -1;\n", 1),
]
