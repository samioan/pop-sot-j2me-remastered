package pop2;

import java.io.InputStream;
import javax.microedition.lcdui.Image;

// $VF: renamed from: pop2.c
public class Sprite {
   Image h;
   int e = -1;
   byte[] c;
   byte[] i;
   byte[][] a;
   byte[][] d;
   byte[] b;
   byte[] g;
   short[] f;

   public int a(InputStream var1) throws Exception {
      int var2 = 0;
      this.e = Game.b(var1);
      var2 += 2;
      int var3 = Game.b(var1);
      var2 += 2;
      this.c = new byte[var3 * 4];
      var2 += Game.a(var1, this.c, 0, var3 * 4);
      var3 = Game.b(var1);
      var2 += 2;
      this.i = new byte[var3 * 8];
      this.a = new byte[var3][];

      for (int var7 = 0; var7 < var3; var7++) {
         int var5 = Game.a(var1);
         int var6 = Game.a(var1);
         int var4 = Game.a(var1);
         var2 += 3;
         this.a[var7] = new byte[var4 * 4];
         var2 += Game.a(var1, this.a[var7], 0, var4 * 4);
         if ((var6 & 128) != 0) {
            var2 += Game.a(var1, this.i, var7 * 8, 4);
         }

         if ((var6 & 64) != 0) {
            var2 += Game.a(var1, this.i, var7 * 8 + 4, 4);
         }
      }

      var3 = Game.b(var1);
      var2 += 2;
      this.b = new byte[var3];
      this.d = new byte[var3][];
      this.g = new byte[var3];
      this.f = new short[var3 * 4];

      for (int var8 = 0; var8 < var3; var8++) {
         int var21 = Game.a(var1);
         int var20 = Game.a(var1);
         var2 += 2;
         this.b[var8] = (byte)var20;
         this.d[var8] = new byte[var20 * 2];
         var2 += Game.a(var1, this.d[var8], 0, var20 * 2);
         this.g[var8] = (byte)Game.a(var1);
         var2++;
         var21 = (var21 - 1 - var20 * 2 - 1) / 2;

         for (int var9 = 0; var9 < var21; var9++) {
            this.f[var8 * 4 + var9] = (short)Game.b(var1);
            var2 += 2;
         }
      }

      return var2;
   }

   boolean a() {
      switch (this.e) {
         case 28:
            this.h = Game.ao;
            break;
         case 29:
            this.h = Game.m;
            break;
         case 30:
            this.h = Game.V;
            break;
         case 31:
            this.h = Game.n;
            break;
         case 32:
            this.h = Game.aj;
      }

      return true;
   }

   public int a(int var1, int var2) {
      if (var1 < 0 || var1 >= this.b.length) {
         return -1;
      } else {
         return var2 >= 0 && var2 < this.b[var1] ? this.d[var1][var2 * 2 + 1] : -1;
      }
   }

   public int b(int var1, int var2) {
      if (var1 < 0 || var1 >= this.b.length) {
         return -1;
      } else {
         return var2 >= 0 && var2 < this.b[var1] ? this.d[var1][var2 * 2] : -1;
      }
   }

   public void a(int var1, int var2, boolean var3, boolean var4, int var5) {
      byte[] var17 = this.a[var5];
      int var18 = this.h.getWidth();
      int var19 = this.h.getHeight();

      for (int var6 = 0; var6 < var17.length; var6 += 4) {
         int var10 = (var17[var6] & 255) * 4;
         int var9 = var17[var6 + 1] & 255;
         int var11 = this.c[var10] & 255;
         int var12 = this.c[var10 + 1] & 255;
         int var13 = this.c[var10 + 2] & 255;
         int var14 = this.c[var10 + 3] & 255;
         if (var11 + var13 > var18) {
            var13 = var18 - var11;
         }

         if (var12 + var14 > var19) {
            var14 = var19 - var12;
         }

         if (var13 > 0 && var14 > 0) {
            int var7 = var1;
            int var8 = var2;
            if (var3) {
               var7 -= var17[var6 + 2];
               var9 ^= 1;
            } else {
               var7 += var17[var6 + 2];
            }

            if (var4) {
               var8 -= var17[var6 + 3];
               var9 ^= 2;
            } else {
               var8 += var17[var6 + 3];
            }

            int var15;
            int var16;
            if ((var9 & 4) != 0) {
               if ((var9 & 1) == 0) {
                  var7 -= var14;
               }

               if ((var9 & 2) != 0) {
                  var8 -= var13;
               }

               var15 = var14;
               var16 = var13;
            } else {
               if ((var9 & 1) != 0) {
                  var7 -= var13;
               }

               if ((var9 & 2) != 0) {
                  var8 -= var14;
               }

               var15 = var13;
               var16 = var14;
            }

            short var23;
            switch (var9) {
               case 1:
                  var23 = 8192;
                  var11 = var18 - var11 - var13;
                  break;
               case 2:
                  var23 = 16384;
                  var12 = var19 - var12 - var14;
                  break;
               case 3:
                  var23 = 180;
                  var11 = var18 - var11 - var13;
                  var12 = var19 - var12 - var14;
                  break;
               case 4:
                  var23 = 0;
                  int var26 = var11;
                  var11 = var19 - var12 - var14;
                  var12 = var26;
                  break;
               case 5:
                  var23 = 0;
                  int var25 = var11;
                  var11 = var12;
                  var12 = var25;
                  break;
               case 6:
                  var23 = 0;
                  int var24 = var11;
                  var11 = var19 - var12 - var14;
                  var12 = var18 - var24 - var13;
                  break;
               case 7:
                  var23 = 0;
                  int var20 = var11;
                  var11 = var12;
                  var12 = var18 - var20 - var13;
                  break;
               default:
                  var23 = 0;
            }

            if (var8 + var16 >= 0 && var8 < 208) {
               if (var8 < 0) {
                  Game.aE.setClip(var7, 0, var15, var16 - 0 + var8);
                  Game.aE.clipRect(var7, 0, var15, var16 - 0 + var8);
               } else if (var8 + var16 > 208) {
                  Game.aE.setClip(var7, var8, var15, 208 - var8);
                  Game.aE.clipRect(var7, var8, var15, 208 - var8);
               } else {
                  Game.aE.setClip(var7, var8, var15, var16);
                  Game.aE.clipRect(var7, var8, var15, var16);
               }

               if (var23 == 0) {
                  Game.aE.drawImage(this.h, var7 - var11, var8 - var12, 20);
               } else {
                  Game.L.drawImage(this.h, var7 - var11, var8 - var12, 20, var23);
               }
            }
         }
      }
   }
}
