package pop2;

import java.io.InputStream;

public class b {
   static d i = new d();
   static a a = new a();
   static int[] b = new int[8];
   static a[][] p = new a[8][];
   static int[][][] r = new int[8][][];
   static int[][] c = new int[8][];
   static byte[][] s = new byte[8][];
   static byte[][] j = new byte[8][];
   static byte[][] g = new byte[8][];
   static c[] f;
   static boolean k;
   private int h;
   private int q;
   static int d;
   static int l;
   static int e;
   static a n;
   static a[] m = new a[100];
   static short[] o = new short[100];
   static boolean[] t = new boolean[100];

   public b() {
      for (int var1 = 0; var1 < 100; var1++) {
         m[var1] = new a();
         o[var1] = -1;
         t[var1] = false;
      }
   }

   public int a(InputStream var1, int var2) throws Exception {
      int var3 = pop2.e.au;
      s[var3] = new byte[var2];
      j[var3] = new byte[var2];
      g[var3] = new byte[var2];
      pop2.e.a(var1, j[var3], 0, var2);
      System.arraycopy(j[var3], 0, s[var3], 0, var2);
      b[var3] = pop2.e.a(j[var3], 0);
      r[var3] = new int[b[var3]][];
      p[var3] = new a[b[var3]];
      c[var3] = new int[b[var3]];
      b(true);
      var2 = 2;

      for (int var4 = 0; var4 < b[var3]; var4++) {
         c[var3][var4] = var2;
         if (j[var3][var2 + 2] == 7) {
            p[var3][var4] = i;
         } else if (var3 != 0 && j[var3][var2 + 2] == 6 && (j[var3][var2 + 4] & 4) != 0) {
            p[var3][var4] = a;
         }

         r[var3][var4] = new int[4];
         r[var3][var4][0] = pop2.e.a(j[var3], var2 + 10);
         r[var3][var4][1] = pop2.e.a(j[var3], var2 + 12);
         r[var3][var4][2] = pop2.e.a(j[var3], var2 + 14);
         r[var3][var4][3] = pop2.e.a(j[var3], var2 + 16);
         var2 += 2 + pop2.e.a(j[var3], var2);
      }

      return var2;
   }

   public void d() {
      int var2 = pop2.e.au;
      if (f != null) {
         for (int var1 = 0; var1 < f.length; var1++) {
            f[var1].h = null;
         }
      }

      if (p[var2] != null) {
         for (int var3 = 0; var3 < b[var2]; var3++) {
            if (p[var2][var3] != null && !p[var2][var3].equals(i) && !p[var2][var3].equals(a)) {
               p[var2][var3] = null;
            }
         }
      }

      for (int var4 = 0; var4 < 100; var4++) {
         o[var4] = -1;
         t[var4] = false;
      }
   }

   static a c(int var0) {
      int var1 = pop2.e.au;
      a var2 = a(var0);
      p[var1][var0] = var2;
      var2.b(j[var1], c[var1][var0]);
      if (var2.b != 6 || (var2.z < 8 || var2.z > 12) && var2.z != 16) {
         if (var2.b == 8) {
            var2.b(1, true);
         }
      } else {
         var2.d = a;
      }

      return var2;
   }

   public void d(int var1) {
      int var2 = pop2.e.au;
      p[var2][var1].a(j[var2], c[var2][var1]);
      if (n != null && n.equals(p[var2][var1])) {
         n = null;
      }

      p[var2][var1] = null;
      b(var1);
   }

   static void b(boolean var0) {
      int var1 = pop2.e.au;
      if (var0) {
         if (a != null) {
            a.H[3] = false;
         }

         for (int var2 = 0; var2 < b[var1]; var2++) {
            if (p[var1][var2] != null) {
               p[var1][var2].a(j[var1], c[var1][var2]);
            }
         }

         System.arraycopy(j[var1], 0, g[var1], 0, j[var1].length);
      } else {
         System.arraycopy(g[var1], 0, j[var1], 0, j[var1].length);

         for (int var3 = 0; var3 < b[var1]; var3++) {
            if (p[var1][var3] != null && !p[var1][var3].equals(i) && !p[var1][var3].equals(a)) {
               p[var1][var3] = null;
               t[var3] = false;
            }
         }

         if (a != null) {
            a.H[3] = false;
         }

         n = null;
      }
   }

   public void c() {
      int var1 = pop2.e.au;

      for (int var2 = 0; var2 < f.length; var2++) {
         f[var2].a();
      }

      System.arraycopy(s[var1], 0, j[var1], 0, s[var1].length);
      System.arraycopy(s[var1], 0, g[var1], 0, s[var1].length);
      a.b();
      i.b();
      i.q();

      for (int var4 = 0; var4 < b[var1]; var4++) {
         a var3 = p[var1][var4];
         if (var3 != null) {
            var3.b(j[var1], c[var1][var4]);
         }
      }

      n = null;
      a(true);

      for (int var5 = 0; var5 < b[var1]; var5++) {
         if (this.a(d, l, var5) && p[var1][var5] == null) {
            c(var5);
         }
      }

      this.h = 0;
      this.q = 0;
      i.e(true);
   }

   public void b() {
      int var1 = pop2.e.au;

      for (int var3 = 0; var3 < b[var1]; var3++) {
         a var2 = p[var1][var3];
         if (var2 != null && var2.H[3]) {
            var2.c(d, l - 0);
         }
      }

      pop2.e.aE.setClip(0, 0, 176, 208);
      pop2.e.aE.clipRect(0, 0, 176, 208);
      if (i != null && k) {
         this.e();
      }
   }

   private void e() {
      int var1 = 24;
      byte var2 = 6;
      byte var4 = 6;
      byte var6 = 63;
      int var3 = var6 * i.L / 7;
      int var5 = var6 * i.E / 7;
      this.h = var5;
      pop2.e.aE.setColor(16711680);
      pop2.e.aE.fillRect(var1, var2, this.h, var4);
      pop2.e.aE.setColor(2097152);
      pop2.e.aE.fillRect(var1 + this.h, var2, var3 - this.h, var4);
      pop2.e.aE.setColor(16777215);
      pop2.e.aE.drawRect(var1 - 1, var2 - 1, var3, var4);
      if (pop2.d.B[2] > 0) {
         var3 = var6 * i.O / 100;
         var5 = var6 * i.K / 100;
         if (pop2.e.aw != 0 || pop2.e.i % 600L <= 300L || var5 * 10 / var3 >= 4 && pop2.e.av != 19) {
            this.q = var5;
            var1 = 173 - var3;
            pop2.e.aE.setColor(33008);
            pop2.e.aE.fillRect(var1 + var3 - this.q, var2, this.q, var4);
            pop2.e.aE.setColor(4128);
            pop2.e.aE.fillRect(var1, var2, var3 - this.q, var4);
            pop2.e.aE.setColor(16777215);
            pop2.e.aE.drawRect(var1 - 1, var2 - 1, var3, var4);
         }
      }
   }

   static a a(a var0) {
      int var1 = pop2.e.au;

      for (int var2 = b[var1] - 1; var2 >= 0; var2--) {
         a var3 = p[var1][var2];
         if (var3 != null && var3.H[3] && var3 != var0 && (var3.b == 3 || var3.b == 7 || var3.b == 4 || var3.b == 6) && var0.b(var3)) {
            return var3;
         }
      }

      return null;
   }

   static boolean b(a var0) {
      int var1 = pop2.e.au;

      for (int var2 = b[var1] - 1; var2 >= 0; var2--) {
         a var3 = p[var1][var2];
         if (var3 != null && var3.H[3] && (var3.b == 7 || var3.b == 8 || var3.b == 6) && var0.a(var3) && var3.M - var0.M < 2048) {
            return true;
         }
      }

      return false;
   }

   public void a() {
      int var1 = pop2.e.au;

      for (int var3 = b[var1] - 1; var3 >= 0; var3--) {
         a var2 = p[var1][var3];
         if (this.a(d, l, var3)) {
            if (var2 == null) {
               var2 = c(var3);
            }
         } else if (var2 != null && !var2.H[2] && !var2.equals(i) && !var2.equals(a)) {
            if (var2.b == 8 && var2.z == 3 && var2.d != null) {
               var2.d.d = null;
            }

            this.d(var3);
            continue;
         }

         if (var2 != null) {
            var2.b(d, l);
         }
      }

      a(false);
   }

   boolean a(int var1, int var2, int var3) {
      int var4 = pop2.e.au;
      return var1 <= r[var4][var3][2] && var2 <= r[var4][var3][3] ? var1 + 176 >= r[var4][var3][0] && var2 + 208 >= r[var4][var3][1] : false;
   }

   static boolean a(int var0, int var1, boolean var2) {
      int var3 = pop2.e.au;

      for (int var7 = 0; var7 < b[var3]; var7++) {
         a var4 = p[var3][var7];
         if (var4 != null && var4.H[3]) {
            if (var4.b == 8) {
               int var5 = var4.e >> 8;
               int var6 = var4.M >> 8;
               if (var6 > var0 && var6 - 32 < var0 + 54 && (!var2 && var5 + 34 < var1 && var5 + 34 > var1 - 28 || var2 && var5 > var1 && var5 < var1 + 28)) {
                  return true;
               }
            } else if (var4.b == 6) {
               int var8 = var4.e >> 8;
               int var9 = var4.M >> 8;
               if (var9 > var0
                  && var9 - 44 < var0 + 54
                  && (!var2 && var8 - 12 < var1 && var8 + 12 > var1 - 28 || var2 && var8 + 12 > var1 && var8 - 12 < var1 + 28)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   static a a(int var0) {
      for (int var1 = 0; var1 < 100; var1++) {
         if (!t[var1]) {
            o[var1] = (short)var0;
            t[var1] = true;
            m[var1].b();
            return m[var1];
         }
      }

      return null;
   }

   static void b(int var0) {
      for (int var1 = 0; var1 < 100; var1++) {
         if (o[var1] == var0) {
            t[var1] = false;
            m[var1].b();
            return;
         }
      }
   }

   private static int a(int var0, int var1) {
      if (Math.abs(var0) > Math.abs(var1)) {
         if (var1 != 0 && var0 / var1 > 0) {
            var0 -= var1;
         }

         if (var0 > 0) {
            if (var0 < 8) {
               var0 = 1;
            } else if (var0 < 32) {
               var0 = 2;
            } else {
               var0 = 4;
            }

            if (var1 > 0) {
               var0 += var1;
            }
         } else {
            if (var0 > -8) {
               var0 = -1;
            } else if (var0 > -32) {
               var0 = -2;
            } else {
               var0 = -4;
            }

            if (var1 < 0) {
               var0 += var1;
            }
         }
      }

      return var0;
   }

   static void a(boolean var0) {
      int var1 = -1;
      int var2 = -1;
      if (n != null) {
         var1 = i.e - n.e >> 8;
         var2 = i.M - n.M >> 8;
         if (var1 < 0) {
            var1 = -var1;
         }

         if (var2 < 0) {
            var2 = -var1;
         }

         if (var1 < 132 && var2 < 156) {
            var1 = ((i.e + n.e) / 2 >> 8) - 88 - d;
            var2 = ((i.M + n.M) / 2 >> 8) + (i.d()[3] + i.d()[1]) / 2 - 104 - l;
         } else {
            var1 = -1;
            var2 = -1;
         }
      }

      if (var1 == -1) {
         var1 = (i.e >> 8) - 88;
         var2 = (i.M >> 8) + (i.d()[3] + i.d()[1]) / 2 - 104;
         var1 += pop2.d.u - d;
         var2 += pop2.d.I - l;
      }

      if (var0) {
         pop2.e.b = true;
         d += var1;
         l += var2;
      } else if (!pop2.d.x) {
         d = d + a(var1, i.i >> 8);
         l = l + a(var2, i.j >> 8);
         if (e > 0) {
            l = l + (e % 2 == 1 ? 8 : -8);
            e--;
         }
      }

      int var3 = pop2.e.au;
      if (d < 0) {
         d = 0;
      }

      if (d > pop2.e.q[var3] * pop2.e.W - 176) {
         d = pop2.e.q[var3] * pop2.e.W - 176;
      }

      if (l < 0) {
         l = 0;
      }

      if (l > pop2.e.as[var3] * pop2.e.aC - 208) {
         l = pop2.e.as[var3] * pop2.e.aC - 208;
      }
   }
}
