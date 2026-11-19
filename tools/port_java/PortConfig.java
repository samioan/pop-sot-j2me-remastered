package pop2;

import javax.microedition.lcdui.Graphics;

/**
 * Desktop configuration and the wide view, read by the patched game (tools/port_patches.py).
 *
 * The phone's screen is 176x208. The port's logical screen is screenWidth x 208. In a level the
 * camera (Level.d, Level.l: the top-left of the view in world pixels) keeps its original rules with
 * the view width in place of 176: it centres the prince, clamps to the level, and the tile scroll
 * buffer and the actor activation test use the same width, so the wider screen shows more of the
 * level and enemies wake at its real edges. A level narrower than the screen is shown whole,
 * centred between black bars (worldOffset).
 *
 * Interface screens keep their 176-pixel layout centred on the wide screen (ui), except what
 * belongs to a screen edge: softkey labels and the HUD icons.
 *
 * At screenWidth 176 with the original menus every helper returns the original value, which is the
 * configuration tools/check_engine.py compares with the original class files.
 */
final class PortConfig {
   static final int BASE = 176;
   static final int HEIGHT = 208;
   /** The widest logical screen the host offers (21:9). */
   static final int MAX = 486;

   static int screenWidth = BASE;
   /** Render interpolation is on: draw calls are recorded with layer markers. */
   static boolean directTiles;
   /** The host installed the desktop menus (OPTIONS entry); the configuration is not the phone's. */
   static boolean desktop;

   static void desktopMenus() {
      desktop = true;
      Game.portMenus();
   }

   /** Width of the world view: the screen, or the whole level when it is narrower. */
   static int view() {
      if (screenWidth <= BASE) {
         return BASE;
      }

      int level = Game.q[Game.au] * Game.W;
      return level < screenWidth ? (level < BASE ? BASE : level) : screenWidth;
   }

   /** Where the world view starts on the screen (centred). */
   static int worldOffset() {
      return (screenWidth - view()) / 2;
   }

   /** Where a 176-wide interface layout starts on the screen (centred). */
   static int ui() {
      return (screenWidth - BASE) / 2;
   }

   /** Width the tile scroll buffer is created with: the widest view when the screen may widen. */
   static int bufferWidth() {
      return desktop || screenWidth > BASE ? MAX : BASE;
   }

   /** The screen width changed: the tile buffer is redrawn in full next frame. */
   static void widthChanged() {
      Game.b = true;
   }

   static void beginWorld(Graphics g) {
      int off = worldOffset();
      if (off > 0) {
         g.setClip(0, 0, screenWidth, HEIGHT);
         g.setColor(0);
         g.fillRect(0, 0, off, HEIGHT);
         g.fillRect(off + view(), 0, screenWidth - off - view(), HEIGHT);
         g.translate(off, 0);
      }

      if (directTiles) {
         PortNative.tileBuffer(Game.X, Game.am, Game.I);
         PortNative.layerWorld(-Level.d, -Level.l);
      }
   }

   static void endWorld(Graphics g) {
      int off = worldOffset();
      if (off > 0) {
         g.translate(-off, 0);
      }

      if (directTiles) {
         PortNative.layerScreen();
      }
   }

   /** Entries of the Settings menu: Hints, Sound, Controls, [PC options,] Back. */
   static int settingsCount() {
      return desktop ? 5 : 4;
   }

   static String settingsLabel(String[] labels, int i) {
      if (!desktop || i < 3) {
         return labels[i];
      }

      return i == 3 ? PortMenu.ENTRY : labels[3];
   }

   /** The original action for a Settings row (0..3), or 99 for the OPTIONS screen. */
   static int settingsAction(int row) {
      if (!desktop || row < 3) {
         return row;
      }

      return row == 3 ? 99 : 3;
   }

   /** The health and sand bars: drawn in place, not moved with an actor between ticks. */
   static void hud() {
      if (directTiles) {
         PortNative.layerScreen();
      }
   }

   static void actor(Actor a) {
      if (directTiles) {
         PortNative.layerActor(a, (a.e >> 8) - Level.d, (a.M >> 8) - Level.l);
      }
   }
}
