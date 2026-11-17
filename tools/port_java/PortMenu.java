package pop2;

/**
 * The desktop OPTIONS screen, drawn with the game's own translucent panel, system font, colours,
 * softkey labels and menu sounds so it reads as part of the original interface.
 *
 * Reached from a "PC options" entry the host adds to the Settings menu (main menu and in-game menu)
 * when it installs the desktop menus; headless equivalence runs never install it, so the original
 * menus and their state are untouched there. It runs as game state 40 and returns to the Settings
 * menu it came from. The settings themselves live in the host (PortNative.setting*): this class
 * lists, steps and activates them.
 *
 * Game members used (see sot/docs/names.map): aw state, ay[] keys this tick (0 up, 1 down, 2 left,
 * 3 right, 4 fire, 5 left softkey, 6 right softkey), A font height, C font, Game.a() clear input,
 * Game.l() input debounce, and the port helpers patched into Game (portText, portSoftkeys,
 * portPanel, portBackground, portSound).
 */
final class PortMenu {
   static final int STATE = 40;
   static final String ENTRY = "PC options";
   private static final String[] PAGES = new String[]{"Display", "Audio", "Keyboard", "Gamepad"};
   private static final int SELECTED = 16770143;
   private static final int NORMAL = 16711422;
   private static final int TITLE = 16770143;

   static int returnState;
   static int page = -1;
   static int row;
   static int topRow;
   static int scroll;

   static void open() {
      returnState = Game.aw;
      page = -1;
      row = 0;
      topRow = 0;
      scroll = 0;
      Game.aw = STATE;
      Game.a();
   }

   private static void close() {
      PortNative.settingsClosed();
      Game.aw = returnState;
      Game.a();
   }

   private static int count() {
      return page < 0 ? PAGES.length : PortNative.settingCount(page);
   }

   private static int visibleRows() {
      return (208 - 30 - Game.A * 2) / Game.A;
   }

   /** Game.d() for state 40: input. */
   static void update() {
      int n = count();
      if (PortNative.settingCapturing()) {
         Game.a();
         return;
      }

      if (n > 0 && (Game.ay[0] == 1)) {
         row = (row + n - 1) % n;
         Game.portSound(9);
         Game.l();
      } else if (n > 0 && (Game.ay[1] == 1)) {
         row = (row + 1) % n;
         Game.portSound(9);
         Game.l();
      } else if (Game.ay[5] >= 1) {
         Game.portSound(11);
         if (page < 0) {
            close();
         } else {
            page = -1;
            row = topRow;
            scroll = 0;
            Game.a();
         }
      } else if (page < 0) {
         if (Game.ay[6] >= 1 || Game.ay[4] >= 1 || Game.ay[3] == 1) {
            Game.portSound(11);
            topRow = row;
            page = row;
            row = 0;
            scroll = 0;
            Game.a();
         }
      } else if (n > 0) {
         if (Game.ay[2] == 1) {
            PortNative.settingChange(page, row, -1);
            Game.portSound(9);
            Game.l();
         } else if (Game.ay[3] == 1) {
            PortNative.settingChange(page, row, 1);
            Game.portSound(9);
            Game.l();
         } else if (Game.ay[6] >= 1 || Game.ay[4] >= 1) {
            PortNative.settingChange(page, row, 0);
            Game.portSound(11);
            Game.a();
         }
      }
   }

   /** Game.paint() for state 40. */
   static void paint(Game game) {
      int n = count();
      if (row >= n) {
         row = n > 0 ? n - 1 : 0;
      }

      int rows = visibleRows();
      if (row < scroll) {
         scroll = row;
      }

      if (row >= scroll + rows) {
         scroll = row - rows + 1;
      }

      game.portBackground(2);
      int a = Game.A;
      int shown = n - scroll < rows ? n - scroll : rows;
      game.portPanel(6, 26 - (a >> 1) + 2, 164, (shown + 1) * a);
      int u = PortConfig.ui();
      Game.portText(page < 0 ? "Options" : PAGES[page], -1, 8, TITLE);
      for (int i = scroll; i < n && i < scroll + rows; i++) {
         int y = 30 + (i - scroll) * a;
         int color = i == row ? SELECTED : NORMAL;
         if (page < 0) {
            Game.portText(PAGES[i], -1, y, color);
         } else {
            Game.portText(PortNative.settingName(page, i), u + 12, y, color);
            String value = PortNative.settingValue(page, i);
            Game.portText(value, u + 164 - Game.C.stringWidth(value), y, color);
         }
      }

      if (scroll > 0) {
         Game.portText("^", -1, 30 - a + 2, NORMAL);
      }

      if (scroll + rows < n) {
         Game.portText("v", -1, 30 + rows * a - 2, NORMAL);
      }

      boolean capturing = PortNative.settingCapturing();
      Game.portSoftkeys(capturing ? null : "Back", capturing ? null : (page < 0 ? "Select" : "Change"));
   }
}
