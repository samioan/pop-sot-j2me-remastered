package pop2;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class PrinceSOT extends MIDlet {
   static Display b = null;
   static Game c = null;
   public static long a = 2306706297927831552L;

   protected void startApp() {
      try {
         if (b == null) {
            b = Display.getDisplay(this);
         }

         if (c == null) {
            c = new Game(this);
            b.setCurrent(c);
            new Thread(c).start();
         }
      } catch (Exception var3) {
         this.destroyApp(false);
         this.notifyDestroyed();
      } catch (Error var4) {
         this.destroyApp(false);
         this.notifyDestroyed();
      }
   }

   protected void pauseApp() {
   }

   protected void destroyApp(boolean var1) {
      if (c != null) {
         Game.n();
      }
   }
}
