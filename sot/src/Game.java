package pop2;

import com.nokia.mid.sound.Sound;
import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;
import com.nokia.mid.ui.FullCanvas;
import java.io.InputStream;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

// $VF: renamed from: pop2.e
public class Game extends FullCanvas implements Runnable {
   PrinceSOT aJ;
   static Level ap;
   static byte l = 1;
   static int u;
   static Sound[] az;
   static byte[] ay = new byte[8];
   static byte[] ax = new byte[8];
   static byte P = -1;
   static byte a;
   static boolean e = false;
   static boolean s = false;
   static long ab = 0L;
   static Graphics aE;
   static DirectGraphics L;
   static Image[] ae = new Image[3];
   static Image n;
   static Image aj;
   static Image ao;
   static Image m;
   static Image V;
   static Font C;
   static short A;
   static Image X;
   static Graphics ak;
   static DirectGraphics aH;
   Image ac;
   Graphics O;
   static int aa;
   static Image[] S = new Image[3];
   static byte[][] ar = new byte[8][];
   static int[] q = new int[8];
   static int[] as = new int[8];
   static int W = 16;
   static int aC = 16;
   static boolean b;
   static int J;
   int ah;
   int k;
   int o;
   int N;
   int j;
   static int am;
   static int I;
   static String[] aA;
   static boolean M;
   static int av;
   static int t;
   static long Q;
   static int aG;
   static int c;
   static boolean B;
   final String[] v = new String[]{"Continue", "New game", "Levels", "Settings", "Instructions", "About", "Exit"};
   final String[] d = new String[]{"Resume", "Main menu", "Settings", "Exit"};
   final String[] ad = new String[]{"Hints: ", "Sound: ", "Controls", "Back"};
   final String[] an = new String[]{"Type ", "Back"};
   final String ag = "You are the Prince,^and your mission is^to seek out the sands^of time which, ^when set free, spelled^doom for your people.^^To thrive in the^adventure, you'll^have to shift chests^and find potions,^while avoiding^sly, lethal traps,^like sharp stakes^that spring up^on your path.^Pit your wits against^the many puzzles and^prove your skill in^handling the sword^and the dagger when^confronting guards,^bowmen and^bats - and emerge^victorious from^these epic conflicts!";
   final String aB = "Version ";
   static String Y;
   final String al = "Info and support:^^www.gameloft.com^support@gameloft.com^^Prince of Persia^Sands of Time^";
   final String r = "^Copyright 2003^Gameloft SA^Credits^^Project Manager^Philippe Laurens^^Producer^Ding Xue Song^^Game Designer^Ding Xue Song^Programmers^Su Ning^Cao Heng^Qiu Wei Min^^Sound Designer^Philippe Arsenault^^Artists^Zhi Yong^Tian Yan^Huang Mei^^Quality Assurance^Benjamin Goulet^Jean-Claude Labelle^Nicolas Gauvreau^Richard Cloutier^Jean-Virgile Laprise^Mathieu Ducharme^Yannick Belzil^F.M. St-Hilaire^Nguyen Hung Tam^Ricardo Filippi^Dominic Auclair^Gao Bo^Wang Xun^Ma Ning";
   final String[] af = new String[]{
      "Move Player:^Pad or^Keys 2,4,6,8^Strike:^Key 5^Jump:^Pad Up or Key 2",
      "Move Player:^Pad^Strike:^Keys 1 to 0^Jump:^Pad Up,^Keys * or #",
      "Move Player:^Pad^Strike:^Key 7^Jump:^Pad Up or Key 5",
      "Move Player:^Pad or^Keys 2,4,6,8^Strike:^Key 5^Jump:^Pad Up or Key 2",
      "Move Player:^Pad or^Keys 2,4,6,8^Strike:^Key 5^Jump:^Pad Up or Key 2"
   };
   final String Z = "Many moons ago,^in the ancient^kingdom of Persia,^King Shahraman and his^young son, the Prince,^were at war with^Maharajah of India.^They were helped^by a Vizier who^betrayed the Maharajah^and allowed the Persian^army to sack the^palace. When the palace^was plundered, the^Vizier asked the^Prince to steal a magic^dagger which was^locked up in the^Maharajah's vaults,^under heavy guard.^Little did the Prince^know, however, that the^Vizier had his greedy^eye on quite another^prize: the Sands^of Time!";
   final String K = "And so it was the^Prince's dagger that^struck home and the^Vizier breathed his last.^The Sands of Time^came back to bless^the people of India.^And long may those^good souls continue^to live in peace and^felicity, for our hero^- the Prince -^dwells on, forever^in their hearts!";
   final String[] g = new String[]{"Qualified", "Skilled", "Expert", "Master", "GrandMaster"};
   final int[] D = new int[]{65278, 8322814, 16711168, 16678526, 16646144};
   static final int[] aq = new int[]{50, 60, 60, 70, 100, 160, 160, 220};
   static byte[] F = new byte[8];
   static byte aI = 1;
   static byte w = 1;
   static byte[] y = new byte[50];
   static short[] E = new short[8];
   static int aw;
   static int au;
   static int T;
   static int x;
   static int f = -1;
   static int p = -1;
   static int R = -1;
   static int U;
   static byte h = 0;
   static boolean at = false;
   static boolean G = false;
   static long H = 80L;
   static long z;
   static long i;
   static long ai;
   boolean aF;
   static byte[] aD = new byte[4];

   public Game(PrinceSOT var1) {
      this.aJ = var1;
      C = Font.getFont(64, 0, 8);
      A = (short)C.getHeight();
      aG = 0;
      this.aF = false;
   }

   void i() {
      String var1 = System.getProperty("microedition.platform");
      if (var1.startsWith("Nokia3650")) {
         h = 1;
      } else if (var1.startsWith("NokiaN-Gage")) {
         h = 2;
      } else if (var1.startsWith("Nokia6600")) {
         at = true;
         h = 3;
      } else if (var1.startsWith("Nokia3660")) {
         at = true;
         h = 4;
      } else {
         h = 0;
      }

      if (ae[1] == null) {
         a(1, "/menu.png");
      }

      if (ae[2] == null) {
         a(2, "/splash.png");
      }

      j();
      T = -1;
      Y = "Info and support:^^www.gameloft.com^support@gameloft.com^^Prince of Persia^Sands of Time^Version "
         + this.aJ.getAppProperty("MIDlet-Version")
         + " Java"
         + "^Copyright 2003^Gameloft SA^Credits^^Project Manager^Philippe Laurens^^Producer^Ding Xue Song^^Game Designer^Ding Xue Song^Programmers^Su Ning^Cao Heng^Qiu Wei Min^^Sound Designer^Philippe Arsenault^^Artists^Zhi Yong^Tian Yan^Huang Mei^^Quality Assurance^Benjamin Goulet^Jean-Claude Labelle^Nicolas Gauvreau^Richard Cloutier^Jean-Virgile Laprise^Mathieu Ducharme^Yannick Belzil^F.M. St-Hilaire^Nguyen Hung Tam^Ricardo Filippi^Dominic Auclair^Gao Bo^Wang Xun^Ma Ning";
      this.ac = Image.createImage(16, 16);
      this.O = this.ac.getGraphics();
      a(0, "/ubilogo.png");
      c = 0;
      aw = 13;
      if (F[0] == 0) {
         x = 1;
      } else if (F[7] > 0) {
         x = 2;
      } else {
         x = 0;
      }

      this.aF = true;
   }

   public void run() {
      if (!this.aF) {
         this.aF = true;
         e = true;
         aw = 6;
         this.repaint();
         this.serviceRepaints();
         this.aF = false;
         this.i();

         try {
            Thread.sleep(1000L);
         } catch (Exception var3) {
         }

         a();
      }

      while (l != 0) {
         i = System.currentTimeMillis();
         if (this.isShown()) {
            this.d();
            this.repaint();
            this.serviceRepaints();
         }

         try {
            z = System.currentTimeMillis() - i;
            if (z > 0L && z < H) {
               Thread.sleep(H - z);
            }
         } catch (Exception var2) {
         }
      }

      this.aJ.notifyDestroyed();
      this.aJ.destroyApp(true);
   }

   void g() {
      if (aw == 0) {
         f = 0;
         aw = 3;
         ai = i - ai;
      }
   }

   void a(boolean var1) {
      if (var1) {
         F[au] = 1;
         ai = (i - ai) / 1000L;
         E[au] = (short)(500L - ai - U * 30 + aq[T]);
         if (E[au] < 0) {
            E[au] = 0;
         }

         a(8, 1);
         n();
         aw = 5;
      } else {
         aw = 12;
      }
   }

   static void e() {
      a++;

      for (int var0 = 0; var0 <= 4; var0++) {
         if (ax[var0] == 0) {
            if (ay[var0] == 1) {
               ay[var0] = 2;
            } else if (ay[var0] == -1) {
               ay[var0] = 0;
            }
         } else {
            ay[var0] = ax[var0];
            ax[var0] = 0;
         }
      }

      ay[5] = 0;
      ay[6] = 0;
      ay[7] = 0;
   }

   void d() {
      if (e) {
         e = false;
      }

      switch (aw) {
         case 0:
            if (av < 0) {
               if (ay[5] < 1 && ay[6] < 1 && ay[7] < 1) {
                  ap.a();
               } else {
                  a();
                  l();
                  this.g();
               }
            } else {
               if (Q == 0L && P != -1) {
                  t++;
                  e = true;
                  l();
               } else if (i > Q) {
                  Q = 0L;
               }

               a();
            }
            break;
         case 1:
            if (ay[5] >= 1) {
               aw = f < 0 ? 2 : 3;
            } else if (ay[6] >= 1) {
               l = 0;
            }
            break;
         case 2:
            if (ay[6] >= 1) {
               switch (x) {
                  case 0:
                     for (int var5 = 0; var5 < 8; var5++) {
                        if (F[var5] == 0) {
                           au = var5;
                           break;
                        }
                     }

                     aw = 4;
                     a = 0;
                     break;
                  case 1:
                     if (F[0] > 0) {
                        aw = 7;
                     } else {
                        c = 0;
                        aw = 9;
                     }
                     break;
                  case 2:
                     int var2 = 0;

                     while (var2 < 8 && F[var2] > 0) {
                        au = var2++;
                     }

                     aw = 8;
                     break;
                  case 3:
                     p = 3;
                     aw = 16;
                     break;
                  case 4:
                     c = 0;
                     aw = 20;
                     break;
                  case 5:
                     c = 0;
                     aw = 11;
                     break;
                  case 6:
                     aw = 1;
               }

               a(11, 1);
               a();
            } else if (ay[0] == 1 || ay[2] == 1) {
               a(9, 1);
               x = (x + 7 - 1) % 7;
               if (x == 0 && (F[7] > 0 || F[0] == 0)) {
                  x = 6;
               }

               if (x == 2 && F[0] == 0) {
                  x = 1;
               }

               l();
            } else if (ay[1] == 1 || ay[3] == 1) {
               a(9, 1);
               x = (x + 1) % 7;
               if (x == 0 && (F[7] > 0 || F[0] == 0)) {
                  x = 1;
               }

               if (x == 2 && F[0] == 0) {
                  x = 3;
               }

               l();
            }
            break;
         case 3:
            if (ay[6] >= 1) {
               switch (f) {
                  case 0:
                     s = false;
                     ab = 0L;
                     f = -1;
                     aw = 0;
                     ai = i - ai;
                     G = true;
                     break;
                  case 1:
                     f = -1;
                     aw = 2;
                     a(12, 1);
                     break;
                  case 2:
                     p = 3;
                     aw = 17;
                     break;
                  case 3:
                     aw = 1;
               }

               a(11, 1);
               a();
            } else if (ay[0] == 1 || ay[2] == 1) {
               a(9, 1);
               f = (f + 4 - 1) % 4;
               l();
            } else if (ay[1] == 1 || ay[3] == 1) {
               a(9, 1);
               f = (f + 1) % 4;
               l();
            }
            break;
         case 4:
            e = true;
            if (a == 1) {
               ap.d();
               ap.c();
               b = true;
               if (au < 2) {
                  aa = 0;
               } else if (au > 4) {
                  aa = 2;
               } else {
                  aa = 1;
               }

               this.ah = S[aa].getWidth() / W;
               aG = 0;
               av = -1;
               t = -1;
               Q = 0L;
               U = 0;
               ai = i;
               T = au;
               a(7, 1);
            } else if (a > 20) {
               l();
               a();
               aw = 0;
            }
            break;
         case 5:
            if (ay[6] >= 1) {
               if (au == 7) {
                  au = 0;
                  c = 0;
                  a();
                  aw = 10;
                  a(14, 1);
               } else {
                  au++;
                  aw = 4;
                  a = 0;
               }
            } else if (ay[5] >= 1 && au != 7) {
               au++;
               aw = 2;
               a(12, 1);
            }
         case 6:
         default:
            break;
         case 7:
            if (ay[6] >= 1) {
               c = 0;
               aw = 9;
               a();
            } else if (ay[5] >= 1) {
               aw = 2;
            }
            break;
         case 8:
            if (ay[5] >= 1) {
               a(11, 1);
               aw = 2;
            } else if (ay[6] >= 1) {
               if (F[au] > 0) {
                  a(11, 1);
                  aw = 4;
                  a = 0;
               }
            } else {
               int var1 = 0;
               var1 = 0;

               while (var1 < 8 && F[var1] != 0) {
                  var1++;
               }

               if (var1 > 0) {
                  if (ay[0] == 1) {
                     a(9, 1);
                     au = (au + var1 - 1) % var1;
                     l();
                  } else if (ay[1] == 1) {
                     a(9, 1);
                     au = (au + 1) % var1;
                     l();
                  }
               }
            }
            break;
         case 9:
            if (B && P != -1) {
               f();
               au = 0;
               aw = 4;
               a = 0;
            }
            break;
         case 10:
            if (B && P != -1) {
               aw = 15;
               x = 2;
            }
            break;
         case 11:
            if (B && P != -1) {
               aw = 2;
            }
            break;
         case 12:
            if (ay[6] >= 1) {
               aw = 4;
               a = 0;
            } else if (ay[5] >= 1) {
               aw = 2;
               a(12, 1);
            }
            break;
         case 13:
            e = true;
            if (a > 20) {
               a = 0;
               if (B) {
                  ae[0] = null;
                  H = 0L;
                  aw = 14;
               } else {
                  P = 4;
               }
            }
            break;
         case 14:
            try {
               e = true;
               this.h();
            } catch (Exception var3) {
               aG = 0;
               aw = 2;
            }
            break;
         case 15:
            if (P != -1) {
               a(12, 1);
               aw = 2;
               H = 70L;
            }
            break;
         case 16:
         case 17:
            if (ay[6] >= 1) {
               switch (p) {
                  case 0:
                     w = (byte)(1 - w);
                     n();
                     break;
                  case 1:
                     aI = (byte)(1 - aI);
                     if (aI == 0) {
                        o();
                     }

                     n();
                     break;
                  case 2:
                     R = 1;
                     if (aw == 16) {
                        aw = 18;
                     } else {
                        aw = 19;
                     }
                     break;
                  case 3:
                     p = -1;
                     if (aw == 16) {
                        aw = 2;
                     } else {
                        aw = 3;
                     }
               }

               a(11, 1);
               a();
            } else if (ay[0] == 1 || ay[2] == 1) {
               a(9, 1);
               p = (p + 4 - 1) % 4;
               l();
            } else if (ay[1] == 1 || ay[3] == 1) {
               a(9, 1);
               p = (p + 1) % 4;
               l();
            }
            break;
         case 18:
         case 19:
            if (ay[6] >= 1) {
               switch (R) {
                  case 0:
                     h = (byte)((h + 1) % 5);
                     break;
                  case 1:
                     R = -1;
                     if (aw == 18) {
                        aw = 16;
                     } else {
                        aw = 17;
                     }

                     n();
               }

               a(11, 1);
               a();
            } else if (ay[0] == 1 || ay[2] == 1) {
               a(9, 1);
               R = (R + 2 - 1) % 2;
               l();
            } else if (ay[1] == 1 || ay[3] == 1) {
               a(9, 1);
               R = (R + 1) % 2;
               l();
            }
            break;
         case 20:
            if (B && P != -1) {
               aw = 2;
            }
      }

      e();
   }

   public void paint(Graphics var1) {
      if (this.aF && var1 != null) {
         aE = var1;
         L = DirectUtils.getDirectGraphics(aE);
         switch (aw) {
            case 0:
            case 1:
            case 3:
            case 17:
            case 19:
               if (aw == 1 && f < 0) {
                  this.c(2);
               } else {
                  this.c(Level.d, Level.l);
                  ap.b();
                  if (Level.k) {
                     b(1);
                  }
               }

               if (aw == 1) {
                  a("Do you want to", -1, 70 - A, 16711422, 0);
                  a("quit the game?", -1, 70, 16711422, 0);
                  a("No", "Yes");
               } else {
                  switch (aw) {
                     case 0:
                        if (av >= 0) {
                           boolean var3 = a(aA[av], t);
                           if (av >= 19 && av <= 21) {
                              M = true;
                           }

                           if (var3) {
                              if (av >= 16 && av <= 18) {
                                 Level.i.a(6, 1);
                              }

                              if (av >= 13 && av <= 15) {
                                 Prince.v = true;
                              }

                              av = -1;
                              t = -1;
                           }
                        }
                        break;
                     case 3:
                        this.a(80, f, 16711422);
                        break;
                     case 17:
                        this.a(80, p, 16711422);
                        break;
                     case 19:
                        this.a(60, R, 16711422);
                  }

                  b(0);
               }
               break;
            case 2:
               if (x == 0 && F[7] > 0) {
                  x = 2;
               }

               this.c(2);
               this.a(80, x, 16711422);
               b(0);
               break;
            case 4:
               this.c(2);
               a("Level " + (au + 1), -1, 150, 16711422, 0);
               a("Loading...", -1, 150 + A, 16711422, 0);
               break;
            case 5:
               this.c(1);
               a("TIME:  " + ai + "s", -1, 60 - A, 16711422, 0);
               a("HURT:  " + U, -1, 60, 16711422, 0);
               a("SCORE: " + E[T], -1, 60 + A, 16711422, 0);
               int var2 = E[T] / 100;
               if (var2 > 4) {
                  var2 = 4;
               }

               if (i % 1000L > 500L) {
                  a(this.g[var2], -1, 60 + A * 2, this.D[var2], 0);
               }

               if (T == 7) {
                  a(null, "Next");
               } else {
                  a("Menu", "Next");
               }
               break;
            case 6:
               a(0, "/gameloft.png");
               if (ae[0] != null) {
                  aE.setClip(0, 0, 176, 208);
                  aE.clipRect(0, 0, 176, 208);
                  aE.setColor(255, 255, 255);
                  aE.fillRect(0, 0, 176, 208);
                  aE.drawImage(ae[0], 88, 104, 3);
                  ae[0] = null;
               }
               break;
            case 7:
               this.c(2);
               a("Do you want to", -1, 70 - A, 16711422, 0);
               a("reset data?", -1, 70, 16711422, 0);
               a("No", "Yes");
               break;
            case 8:
               this.c(1);
               this.a(30);
               a("Menu", "Select");
               break;
            case 9:
               this.c(1);
               this.a(
                  "Many moons ago,^in the ancient^kingdom of Persia,^King Shahraman and his^young son, the Prince,^were at war with^Maharajah of India.^They were helped^by a Vizier who^betrayed the Maharajah^and allowed the Persian^army to sack the^palace. When the palace^was plundered, the^Vizier asked the^Prince to steal a magic^dagger which was^locked up in the^Maharajah's vaults,^under heavy guard.^Little did the Prince^know, however, that the^Vizier had his greedy^eye on quite another^prize: the Sands^of Time!",
                  30,
                  10,
                  16770143
               );
               b(0);
               break;
            case 10:
               this.c(1);
               this.a(
                  "And so it was the^Prince's dagger that^struck home and the^Vizier breathed his last.^The Sands of Time^came back to bless^the people of India.^And long may those^good souls continue^to live in peace and^felicity, for our hero^- the Prince -^dwells on, forever^in their hearts!",
                  30,
                  10,
                  16770143
               );
               b(0);
               break;
            case 11:
               this.c(1);
               this.a(Y, 30, 10, 16770143);
               b(0);
               break;
            case 12:
               this.c(1);
               a("Level Failed", -1, 70, 16711422, 0);
               a("Menu", "Again");
               break;
            case 13:
               this.c(0);
               this.a(
                  "^© 2003 Gameloft.^Based on Prince^of Persia created^by Jordan Mechner.^All Rights Reserved.^Gameloft and^the Gameloft logo^are trademarks^of Gameloft^in the US and/or^other countries.^^Prince of Persia^The Sands of Time^is a trademark of^Jordan Mechner^used under license^by Gameloft.^^Available on^PlayStation 2,^PC CD-ROM^and GameBoyAdvance^www.^prince-of-persia.com^",
                  30,
                  10,
                  16770143
               );
               if (B) {
                  b(2);
               }
               break;
            case 14:
               this.c(2);
               if (aG < 30) {
                  a("Loading (" + aG * 100 / 30 + "%)", -1, 150 + A, 16711422, 0);
                  this.b(aG, 31);
               }
               break;
            case 15:
               this.c(2);
               if (i % 400L < 200L) {
                  a("Press any key", -1, 150, 16770143, 0);
               }
               break;
            case 16:
               this.c(2);
               this.a(80, p, 16711422);
               b(0);
               break;
            case 18:
               this.c(2);
               this.a(60, R, 16711422);
               b(0);
               break;
            case 20:
               this.c(1);
               this.a(
                  "You are the Prince,^and your mission is^to seek out the sands^of time which, ^when set free, spelled^doom for your people.^^To thrive in the^adventure, you'll^have to shift chests^and find potions,^while avoiding^sly, lethal traps,^like sharp stakes^that spring up^on your path.^Pit your wits against^the many puzzles and^prove your skill in^handling the sword^and the dagger when^confronting guards,^bowmen and^bats - and emerge^victorious from^these epic conflicts!",
                  30,
                  10,
                  16770143
               );
               b(0);
         }

         aE = null;
         L = null;
      }
   }

   static void a(String var0, int var1, int var2, int var3, int var4) {
      if (var1 == -1) {
         var1 = (176 - C.stringWidth(var0)) / 2;
      }

      aE.setFont(C);
      aE.setClip(0, 0, 176, 208);
      aE.clipRect(0, 0, 176, 208);
      if (var4 >= 0) {
         aE.setColor(var4);
         aE.drawString(var0, var1 + 1, var2, 20);
         aE.drawString(var0, var1 - 1, var2, 20);
         aE.drawString(var0, var1, var2 + 1, 20);
         aE.drawString(var0, var1, var2 - 1, 20);
      }

      aE.setColor(var3);
      aE.drawString(var0, var1, var2, 20);
   }

   private void c(int var1) {
      aE.setClip(0, 0, 176, 208);
      aE.clipRect(0, 0, 176, 208);
      switch (var1) {
         case 0:
            aE.setColor(0);
            aE.fillRect(0, 0, 176, 208);
            break;
         case 1:
            aE.drawImage(ae[0], 0, 0, 20);
            break;
         case 2:
            aE.setColor(0);
            aE.fillRect(0, 0, 176, 208);
            aE.drawImage(ae[2], 88, 208, 33);
            aE.drawImage(ae[1], 88, 0, 17);
      }
   }

   void a(int var1, int var2, int var3, int var4) {
      aE.setClip(0, 0, 176, 208);
      aE.clipRect(0, 0, 176, 208);
      L.fillPolygon(
         new int[]{var1, var1 + var3, var1 + var3, var1 + var3 - 1, var1},
         0,
         new int[]{var2, var2, var2 + var4 + 1, var2 + var4, var2 + var4},
         0,
         5,
         1627389951
      );
   }

   private void a(int var1, int var2, int var3) {
      int var4 = 0;
      boolean var5 = false;
      boolean var6 = false;
      String var7 = "";
      switch (aw) {
         case 2:
            var6 = F[0] == 0;
            var5 = F[7] > 0 || F[0] == 0;
            if (var5 && var6) {
               var4 = 6 * A;
            } else if (!var5 && !var6) {
               var4 = 8 * A;
            } else {
               var4 = 7 * A;
            }

            this.a(40, 80 - (A >> 1), 96, var4);
            var2 = (var2 + 7) % 7;

            for (int var8 = 0; var8 < 7; var8++) {
               if ((var8 != 0 || !var5) && (var8 != 2 || !var6)) {
                  if (var8 == var2) {
                     a(this.v[var8], -1, var1, 16770143, 0);
                  } else {
                     a(this.v[var8], -1, var1, var3, 0);
                  }

                  var1 += A;
               }
            }
            break;
         case 3:
            var4 = 5 * A;
            this.a(40, 80 - (A >> 1), 96, var4);
            var2 = (var2 + 4) % 4;

            for (int var9 = 0; var9 < 4; var9++) {
               var7 = this.d[var9];
               if (var9 == var2) {
                  a(var7, -1, var1, 16770143, 0);
               } else {
                  a(var7, -1, var1, var3, 0);
               }

               var1 += A;
            }
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            break;
         case 16:
         case 17:
            var4 = 5 * A;
            this.a(40, 80 - (A >> 1), 96, var4);
            var2 = (var2 + 4) % 4;

            for (int var10 = 0; var10 < 4; var10++) {
               var7 = this.ad[var10];
               if (var10 == 0) {
                  var7 = var7 + (w != 0 ? "On" : "Off");
               } else if (var10 == 1) {
                  var7 = var7 + (aI != 0 ? "On" : "Off");
               }

               if (var10 == var2) {
                  a(var7, -1, var1, 16770143, 0);
               } else {
                  a(var7, -1, var1, var3, 0);
               }

               var1 += A;
            }
            break;
         case 18:
         case 19:
            var4 = 10 * A;
            this.a(40, 60 - (A >> 1), 96, var4);
            var2 = (var2 + 2) % 2;

            for (int var11 = 0; var11 < 2; var11++) {
               var7 = this.an[var11];
               if (var11 == 0) {
                  var7 = var7 + "<";
                  switch (h) {
                     case 0:
                        var7 = var7 + "7650";
                        break;
                     case 1:
                        var7 = var7 + "3650";
                        break;
                     case 2:
                        var7 = var7 + "N-Gage";
                        break;
                     case 3:
                        var7 = var7 + "6600";
                        break;
                     case 4:
                        var7 = var7 + "3660";
                        break;
                     default:
                        var7 = var7 + "7650";
                  }

                  var7 = var7 + ">";
                  int var12 = var1 + A;
                  int var13 = 0;
                  String var15 = this.af[h];
                  int var17 = 0;

                  while (true) {
                     int var14 = var15.indexOf(94, var13);
                     if (var14 == -1) {
                        var14 = var15.length();
                     }

                     String var16 = var15.substring(var13, var14);
                     a(var16, -1, var12, var3, 0);
                     var12 += A;
                     var13 = var14 + 1;
                     if (var13 > var15.length()) {
                        break;
                     }

                     var17++;
                  }
               } else {
                  var1 += A * 8;
               }

               if (var11 == var2) {
                  a(var7, -1, var1, 16770143, 0);
               } else {
                  a(var7, -1, var1, var3, 0);
               }
            }
      }
   }

   private void a(int var1) {
      for (int var2 = 0; var2 < F.length && F[var2] != 0; var2++) {
         if (var2 == au) {
            a("Level " + (var2 + 1), -1, var1 + A * var2, 16770143, 0);
         } else {
            a("Level " + (var2 + 1), -1, var1 + A * var2, 16711422, 0);
         }
      }
   }

   private static void a(String var0, String var1) {
      if (var0 != null) {
         a(var0, 3, 208 - A, 16711422, 0);
      }

      if (var1 != null) {
         a(var1, 176 - C.stringWidth(var1) - 2, 208 - A, 16711422, 0);
      }
   }

   private static void b(int var0) {
      switch (var0) {
         case 0:
            aE.setClip(161, 197, 14, 8);
            aE.clipRect(161, 197, 14, 8);
            aE.drawImage(ao, 61, 173, 20);
            break;
         case 1:
            aE.setClip(3, 3, 18, 22);
            aE.clipRect(3, 3, 18, 22);
            aE.drawImage(ao, -107, -88, 20);
            break;
         case 2:
            if (ae[0] != null) {
               aE.setClip(0, 0, 176, 208);
               aE.clipRect(0, 0, 176, 208);
               aE.drawImage(ae[0], 88, 188, 33);
            }
      }
   }

   private void b(int var1, int var2) {
      aE.setClip(16, 150 + A * 2, 144, 5);
      aE.clipRect(16, 150 + A * 2, 144, 5);
      aE.setColor(16777215);
      aE.drawRect(16, 150 + A * 2, 144, 5);
      if (var1 > 0) {
         aE.fillRect(16, 150 + A * 2, var1 * 144 / var2, 5);
      }
   }

   void a(String var1, int var2, int var3, int var4) {
      int var5 = 0;
      byte var7 = 10;
      int var8 = 30 - c * (A + 1);
      B = false;

      for (int var10 = 0; var8 < 30 + (A + 1) * var7; var10++) {
         int var6 = var1.indexOf(94, var5);
         if (var6 == -1) {
            var6 = var1.length();
         }

         String var9 = var1.substring(var5, var6);
         if (var8 >= 30) {
            a(var9, (176 - C.stringWidth(var9)) / 2 + 1, var8 + 1, 4473924, -1);
            a(var9, -1, var8, var4, -1);
         }

         var8 += A + 1;
         var5 = var6 + 1;
         if (var5 > var1.length()) {
            B = true;
            return;
         }
      }

      if (P != -1) {
         c += var7;
         l();
         a();
      }
   }

   static boolean a(String var0, int var1) {
      int var2 = 0;
      boolean var4 = true;
      int var6 = 0;

      while (true) {
         int var3 = var0.indexOf(94, var2);
         if (var3 == -1) {
            var3 = var0.length();
         }

         if (var6 == var1) {
            String var5 = var0.substring(var2, var3);
            a(var5, -1, 208 - A - 1, 16777215, 0);
            var4 = false;
            break;
         }

         var2 = var3 + 1;
         if (var2 > var0.length()) {
            break;
         }

         var6++;
      }

      return var4;
   }

   static boolean a(int var0, String var1) {
      try {
         ae[var0] = Image.createImage(var1);
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   void h() {
      switch (++aG) {
         case 1:
            ap = new Level();
            break;
         case 2:
            this.p();
            break;
         case 3:
            this.q();
            break;
         case 4:
            this.b();
            break;
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
            m();
            break;
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
            this.k();
            break;
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
            this.c();
            break;
         case 31:
            aG = 0;
            l();
            a();
            aw = 15;
      }
   }

   static void j() {
      try {
         RecordStore var0 = RecordStore.openRecordStore("POP2RECORD", true);
         if (var0.getNumRecords() > 0 && var0.getRecord(1, y, 0) == 50) {
            for (int var1 = 0; var1 < 10; var1++) {
               Prince.B[var1] = y[var1];
            }

            for (int var3 = 0; var3 < 8; var3++) {
               F[var3] = y[var3 + 10];
            }

            aI = y[20];
            w = y[21];
            h = y[22];

            for (int var4 = 0; var4 < 8; var4++) {
               E[var4] = (short)((y[var4 * 2 + 30] & 255) + (y[var4 * 2 + 31] << 8));
            }
         } else {
            f();
         }

         var0.closeRecordStore();
      } catch (Exception var2) {
      }
   }

   static void f() {
      for (int var0 = 0; var0 < 10; var0++) {
         Prince.B[var0] = 0;
      }

      for (int var1 = 0; var1 < 8; var1++) {
         F[var1] = 0;
      }
   }

   static void n() {
      try {
         RecordStore var0 = RecordStore.openRecordStore("POP2RECORD", false);

         for (int var1 = 0; var1 < 10; var1++) {
            y[var1] = Prince.B[var1];
         }

         for (int var3 = 0; var3 < 8; var3++) {
            y[var3 + 10] = F[var3];
         }

         y[20] = aI;
         y[21] = w;
         y[22] = h;

         for (int var4 = 0; var4 < 8; var4++) {
            y[var4 * 2 + 30] = (byte)E[var4];
            y[var4 * 2 + 31] = (byte)(E[var4] >> 8);
         }

         if (var0.getNumRecords() > 0) {
            var0.setRecord(1, y, 0, 50);
         } else {
            var0.addRecord(y, 0, 50);
         }

         var0.closeRecordStore();
      } catch (Exception var2) {
      }
   }

   static final int a(byte[] var0, int var1) {
      int var2 = var0[var1++];
      if (var2 < 0) {
         var2 += 256;
      }

      var2 <<= 8;
      var2 += var0[var1];
      if (var0[var1] < 0) {
         var2 += 256;
      }

      return var2;
   }

   static final int b(byte[] var0, int var1) {
      int var2 = 0;
      int var3 = 0;

      while (var3 < 4) {
         var2 <<= 8;
         var2 += var0[var1];
         if (var0[var1] < 0) {
            var2 += 256;
         }

         var3++;
         var1++;
      }

      return var2;
   }

   static int a(byte[] var0, int var1, int var2) {
      int var3 = 0;

      for (int var4 = var2 - 1; var4 >= 0; var4--) {
         var3 <<= 8;
         var3 += var0[var4 + var1];
         if (var0[var4 + var1] < 0) {
            var3 += 256;
         }
      }

      return var3;
   }

   static int b(InputStream var0) {
      try {
         for (int var1 = 0; var1 < 2; var1++) {
            aD[var1] = (byte)var0.read();
         }

         return a(aD, 0);
      } catch (Exception var2) {
         return -1;
      }
   }

   static int a(InputStream var0) {
      try {
         return var0.read();
      } catch (Exception var2) {
         return -1;
      }
   }

   static int a(InputStream var0, byte[] var1, int var2, int var3) {
      try {
         for (int var4 = 0; var4 < var3; var4++) {
            var1[var2 + var4] = (byte)var0.read();
         }

         return var3;
      } catch (Exception var5) {
         return -1;
      }
   }

   static int a(InputStream var0, int var1) {
      try {
         for (int var2 = 0; var2 < var1; var2++) {
            var0.read();
         }
      } catch (Exception var3) {
      }

      return -1;
   }

   int q() {
      try {
         int var2 = 0;
         InputStream var3 = this.getClass().getResourceAsStream("/s.bin");
         int var1 = b(var3);
         a(var3, var1 * 4);
         var2 += 2 + var1 * 4;
         byte[] var4 = new byte[256];
         int var5 = 0;
         if (aA == null) {
            aA = new String[var1];
         }

         for (int var6 = 0; var6 < var1; var6++) {
            var5 = b(var3);
            a(var3, var4, 0, var5);
            aA[var6] = new String(var4, 0, var5, "UTF-8");
            var2 += var5;
         }

         var3.close();
         Object var9 = null;
         Object var10 = null;
         return var2;
      } catch (Exception var7) {
         return 0;
      }
   }

   int k() {
      try {
         byte[] var5 = null;
         InputStream var6 = this.getClass().getResourceAsStream("/p.bin");
         int var1 = b(var6);
         var5 = new byte[var1 * 4];
         a(var6, var5, 0, var1 * 4);
         int var4 = 2 + var1 * 4;

         for (int var7 = 0; var7 < var1; var7++) {
            int var3;
            if (var7 > 0) {
               var3 = b(var5, (var7 - 1) * 4);
            } else {
               var3 = 0;
            }

            int var2 = b(var5, var7 * 4) - var3;
            var3 = b(var5, (var1 - 1) * 4) - var2 - var3;
            J = b(var6);
            q[var7] = b(var6);
            as[var7] = b(var6);
            W = b(var6);
            aC = b(var6);
            var4 += 10;
            var2 -= 10;
            ar[var7] = new byte[var2];
            var4 += a(var6, ar[var7], 0, var2);
         }

         var6.close();
         Object var13 = null;
         return var4;
      } catch (Exception var8) {
         return 0;
      }
   }

   int c() {
      try {
         int var4 = 0;
         byte[] var5 = null;
         InputStream var6 = this.getClass().getResourceAsStream("/c.bin");
         int var1 = b(var6);
         var5 = new byte[var1 * 4];
         a(var6, var5, 0, var1 * 4);
         var4 = 2 + var1 * 4;

         for (int var7 = 0; var7 < var1; var7++) {
            au = var7;
            int var3;
            if (var7 > 0) {
               var3 = b(var5, (var7 - 1) * 4);
            } else {
               var3 = 0;
            }

            int var2 = b(var5, var7 * 4) - var3;
            var4 += ap.a(var6, var2);
         }

         var6.close();
         Object var11 = null;
         return var4;
      } catch (Exception var8) {
         return 0;
      }
   }

   int b() {
      try {
         InputStream var4 = this.getClass().getResourceAsStream("/a.bin");
         int var1 = b(var4);
         int var3 = 2;
         Level.f = new Sprite[var1];

         for (int var5 = 0; var5 < var1; var5++) {
            int var2 = b(var4);
            var3 += 2;
            Sprite var6 = new Sprite();
            Level.f[var5] = var6;
            var3 += var6.a(var4);
         }

         var4.close();
         return var3;
      } catch (Exception var7) {
         return 0;
      }
   }

   static void m() {
      try {
         switch (aG - 5) {
            case 0:
               am = 176 + W * 2 & ~(W - 1);
               I = 208 + aC * 2 & ~(aC - 1);
               X = Image.createImage(am, I);
               ak = X.getGraphics();
               aH = DirectUtils.getDirectGraphics(ak);
               break;
            case 1:
               if (ae[0] == null) {
                  a(0, "/story.png");
               }
               break;
            case 2:
               if (ao == null) {
                  ao = Image.createImage("/misc.png");
               }
               break;
            case 3:
               if (m == null) {
                  m = Image.createImage("/prince.png");
               }
               break;
            case 4:
               if (V == null) {
                  V = Image.createImage("/sandman.png");
               }
               break;
            case 5:
               if (S[0] == null) {
                  S[0] = Image.createImage("/level1_tile.png");
               }
               break;
            case 6:
               if (S[1] == null) {
                  S[1] = Image.createImage("/level2_tile.png");
               }
               break;
            case 7:
               if (S[2] == null) {
                  S[2] = Image.createImage("/level3_tile.png");
               }
               break;
            case 8:
               if (n == null) {
                  n = Image.createImage("/fire.png");
               }
               break;
            case 9:
               if (aj == null) {
                  aj = Image.createImage("/water.png");
               }
         }
      } catch (Exception var1) {
      }
   }

   void p() {
      try {
         InputStream var8 = this.getClass().getResourceAsStream("/sound.bin");
         byte[] var7 = new byte[4];
         a(var8, var7, 0, 4);
         u = a(var7, 0, 4) - 1;
         var7 = new byte[4 * u + 4];
         a(var8, var7, 0, 4 * u + 4);
         int var3 = 0;
         az = new Sound[u];

         for (int var1 = 0; var1 < u; var1++) {
            int var4 = a(var7, 4 * var1 + 4, 4);
            int var5 = var4 - var3;
            byte[] var9 = new byte[var5];
            int var6 = a(var8, var9, 0, var5);
            var3 = var4;
            az[var1] = new Sound(var9, 1);
         }

         var8.close();
      } catch (Exception var10) {
      }
   }

   static void a(int var0, int var1) {
      if (aI != 0 && var0 < u) {
         o();
         if (var0 >= 0 && az != null && az[var0] != null) {
            try {
               az[var0].play(var1);
            } catch (Exception var3) {
            }
         }
      }
   }

   static void o() {
      try {
         for (int var0 = 0; var0 < u; var0++) {
            if (az[var0].getState() == 0) {
               az[var0].stop();
            }

            if (at) {
               az[var0].setGain(96);
            }
         }
      } catch (Exception var1) {
      }
   }

   void c(int var1, int var2) {
      Graphics var13 = ak;
      int var3 = var1 / W;
      int var5 = var3 + 176 / W + 1;
      int var4 = var2 / aC;
      int var6 = var4 + 208 / aC + 1;
      if (b) {
         b = false;
         this.a(var13, var3, var4, var5, var6);
         this.k = var3;
         this.N = var4;
         this.o = var5;
         this.j = var6;
      }

      if (this.k != var3) {
         int var7;
         int var8;
         if (this.k < var3) {
            var7 = this.o + 1;
            var8 = var5;
         } else {
            var7 = var3;
            var8 = this.k - 1;
         }

         this.a(var13, var7, var4, var8, var6);
         this.k = var3;
         this.o = var5;
      }

      if (this.N != var4) {
         int var14;
         int var15;
         if (this.N < var4) {
            var14 = this.j + 1;
            var15 = var6;
         } else {
            var14 = var4;
            var15 = this.N - 1;
         }

         this.a(var13, var3, var14, var5, var15);
         this.N = var4;
         this.j = var6;
      }

      int var9 = var1 % am;
      int var11 = var2 % I;
      int var10 = (var1 + 176) % am;
      int var12 = (var2 + 208) % I;
      if (var10 > var9) {
         if (var12 > var11) {
            this.a(var9, var11, 176, 208, 0, 0);
         } else {
            this.a(var9, var11, 176, 208 - var12, 0, 0);
            this.a(var9, 0, 176, var12, 0, 208 - var12);
         }
      } else if (var12 > var11) {
         this.a(var9, var11, 176 - var10, 208, 0, 0);
         this.a(0, var11, var10, 208, 176 - var10, 0);
      } else {
         this.a(var9, var11, 176 - var10, 208 - var12, 0, 0);
         this.a(var9, 0, 176 - var10, var12, 0, 208 - var12);
         this.a(0, var11, var10, 208 - var12, 176 - var10, 0);
         this.a(0, 0, var10, var12, 176 - var10, 208 - var12);
      }
   }

   void a(Graphics var1, int var2, int var3, int var4, int var5) {
      int var14 = au;
      DirectGraphics var16 = aH;
      int var7 = var3 * aC % I;

      for (int var11 = var3; var11 <= var5; var11++) {
         int var6 = var2 * W % am;

         for (int var10 = var2; var10 <= var4; var10++) {
            int var12 = this.d(var10, var11);
            var1.setClip(var6, var7, W, aC);
            var1.clipRect(var6, var7, W, aC);
            byte var15;
            if (var12 >= 0 && (var15 = ar[var14][var12]) != -1) {
               int var8 = (ar[var14][var12 + 1] & 255) + ((var15 & 1) << 8);
               int var9 = var8 / this.ah * aC;
               var8 = var8 % this.ah * W;
               if ((var15 & 192) == 0) {
                  var1.drawImage(S[aa], var6 - var8, var7 - var9, 20);
               } else {
                  this.O.drawImage(S[aa], -var8, -var9, 20);
                  int var13 = 0;
                  if ((var15 & 64) != 0) {
                     if ((var15 & 128) != 0) {
                        var13 |= 180;
                     } else {
                        var13 |= 8192;
                     }
                  } else if ((var15 & 128) != 0) {
                     var13 |= 16384;
                  }

                  var16.drawImage(this.ac, var6, var7, 20, var13);
               }
            } else {
               var1.setColor(0);
               var1.fillRect(var6, var7, W, aC);
            }

            var6 += W;
            if (var6 >= am) {
               var6 -= am;
            }
         }

         var7 += aC;
         if (var7 >= I) {
            var7 -= I;
         }
      }
   }

   int d(int var1, int var2) {
      int var3 = au;
      return var1 >= 0 && var1 < q[var3] && var2 >= 0 && var2 < as[var3] ? (var2 * q[var3] + var1) * 2 : -1;
   }

   static byte e(int var0, int var1) {
      int var2 = au;
      if (var0 >= 0 && var0 < q[var2] && var1 >= 0 && var1 < as[var2]) {
         byte var3 = (byte)((ar[var2][(var1 * q[var2] + var0) * 2] & 62) >> 1);
         if (var3 == 2) {
            return 1;
         } else {
            return var3 == 1 ? 3 : var3;
         }
      } else {
         return 3;
      }
   }

   void a(int var1, int var2, int var3, int var4, int var5, int var6) {
      aE.setClip(var5, var6, var3, var4);
      aE.clipRect(var5, var6, var3, var4);
      aE.drawImage(X, var5 - var1, var6 - var2, 20);
   }

   static void a() {
      a = 0;
      P = -1;

      for (int var0 = 0; var0 < 8; var0++) {
         ay[var0] = 0;
         ax[var0] = 0;
      }
   }

   static void l() {
      s = true;
      ab = System.currentTimeMillis();
   }

   protected void keyPressed(int var1) {
      if (!e) {
         if (s) {
            if (System.currentTimeMillis() - ab <= 300L) {
               return;
            }

            s = false;
            ab = 0L;
         }

         byte var2;
         var2 = -1;
         label71:
         switch (h) {
            case 0:
            case 3:
            case 4:
               switch (var1) {
                  case -11:
                     l = 0;
                  case -10:
                  case -9:
                  case -8:
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                  case 38:
                  case 39:
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 47:
                  default:
                     break label71;
                  case -7:
                     var2 = 6;
                     break label71;
                  case -6:
                     var2 = 5;
                     break label71;
                  case -5:
                  case 48:
                  case 49:
                  case 51:
                  case 53:
                  case 55:
                  case 57:
                     P = -1;
                     if (aw == 0) {
                        var2 = 4;
                     } else {
                        var2 = 6;
                     }
                     break label71;
                  case -4:
                  case 54:
                     var2 = 3;
                     break label71;
                  case -3:
                  case 52:
                     var2 = 2;
                     break label71;
                  case -2:
                  case 56:
                     var2 = 1;
                     break label71;
                  case -1:
                  case 50:
                     var2 = 0;
                     break label71;
               }
            case 1:
               switch (var1) {
                  case -11:
                     l = 0;
                  case -10:
                  case -9:
                  case -8:
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 36:
                  case 37:
                  case 38:
                  case 39:
                  case 40:
                  case 41:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 47:
                  default:
                     break label71;
                  case -7:
                     var2 = 6;
                     break label71;
                  case -6:
                     var2 = 5;
                     break label71;
                  case -5:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                     P = -1;
                     if (aw == 0) {
                        var2 = 4;
                     } else {
                        var2 = 6;
                     }
                     break label71;
                  case -4:
                     var2 = 3;
                     break label71;
                  case -3:
                     var2 = 2;
                     break label71;
                  case -2:
                     var2 = 1;
                     break label71;
                  case -1:
                  case 35:
                  case 42:
                     var2 = 0;
                     break label71;
               }
            case 2:
               switch (var1) {
                  case -11:
                     l = 0;
                  case -10:
                  case -9:
                  case -8:
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                  case 38:
                  case 39:
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 47:
                  default:
                     break;
                  case -7:
                     var2 = 6;
                     break;
                  case -6:
                     var2 = 5;
                     break;
                  case -5:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                     P = -1;
                     if (aw == 0) {
                        var2 = 4;
                     } else {
                        var2 = 6;
                     }
                     break;
                  case -4:
                     var2 = 3;
                     break;
                  case -3:
                     var2 = 2;
                     break;
                  case -2:
                     var2 = 1;
                     break;
                  case -1:
                     var2 = 0;
                     break;
                  case 53:
                     if (aw == 0) {
                        var2 = 0;
                     } else {
                        var2 = 6;
                     }
               }
         }

         if (var2 > -1) {
            if (aw == 0 && P == var2 && (P == 2 || P == 3) && a <= 4) {
               ay[var2] = 3;
            } else {
               P = var2;
               a = 0;
               if (ay[var2] == -1) {
                  ax[var2] = 1;
               } else {
                  ay[var2] = 1;
                  ax[var2] = 0;
               }
            }
         }
      }
   }

   protected void keyReleased(int var1) {
      if (!e) {
         byte var2;
         var2 = -1;
         label50:
         switch (h) {
            case 0:
            case 3:
            case 4:
               switch (var1) {
                  case -5:
                  case 48:
                  case 49:
                  case 51:
                  case 53:
                  case 55:
                  case 57:
                     if (aw == 0) {
                        var2 = 4;
                     }
                     break label50;
                  case -4:
                  case 54:
                     var2 = 3;
                     break label50;
                  case -3:
                  case 52:
                     var2 = 2;
                     break label50;
                  case -2:
                  case 56:
                     var2 = 1;
                     break label50;
                  case -1:
                  case 50:
                     var2 = 0;
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                  case 38:
                  case 39:
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 47:
                  default:
                     break label50;
               }
            case 1:
               switch (var1) {
                  case -5:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                     if (aw == 0) {
                        var2 = 4;
                     }
                     break label50;
                  case -4:
                     var2 = 3;
                     break label50;
                  case -3:
                     var2 = 2;
                     break label50;
                  case -2:
                     var2 = 1;
                     break label50;
                  case -1:
                  case 35:
                  case 42:
                     var2 = 0;
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 36:
                  case 37:
                  case 38:
                  case 39:
                  case 40:
                  case 41:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 47:
                  default:
                     break label50;
               }
            case 2:
               switch (var1) {
                  case -5:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                     if (aw == 0) {
                        var2 = 4;
                     }
                     break;
                  case -4:
                     var2 = 3;
                     break;
                  case -3:
                     var2 = 2;
                     break;
                  case -2:
                     var2 = 1;
                     break;
                  case -1:
                     var2 = 0;
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                  case 38:
                  case 39:
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 47:
                  default:
                     break;
                  case 53:
                     if (aw == 0) {
                        var2 = 0;
                     }
               }
         }

         if (var2 > -1) {
            if (ay[var2] == 1) {
               ax[var2] = -1;
            } else {
               ay[var2] = -1;
               ax[var2] = 0;
            }
         }
      }
   }

   protected void hideNotify() {
      this.g();
   }

   protected void showNotify() {
   }
}
