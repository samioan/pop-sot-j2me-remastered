package pop2;

// $VF: renamed from: pop2.a
public class Actor {
   Sprite h;
   int b;
   int e;
   int M;
   int i;
   int j;
   int p;
   int G;
   int s;
   int l;
   boolean[] H = new boolean[4];
   byte[] w = new byte[4];
   byte m;
   byte F;
   byte A;
   byte o;
   int a;
   int z;
   int D;
   byte t;
   boolean c;
   boolean k;
   byte[] g = new byte[4];
   byte[] P = new byte[4];
   Actor d;
   int E;
   int L;
   int n;
   int y;
   int C;

   public void b() {
      this.h = null;
      this.b = 0;
      this.e = 0;
      this.M = 0;
      this.i = 0;
      this.j = 0;
      this.p = 0;
      this.G = 0;
      this.s = 0;
      this.l = 0;

      for (int var1 = 0; var1 < 4; var1++) {
         this.H[var1] = false;
         this.w[var1] = 0;
         this.g[var1] = 0;
         this.P[var1] = 0;
      }

      this.m = 0;
      this.F = 0;
      this.A = 0;
      this.o = 0;
      this.a = 0;
      this.z = 0;
      this.D = 0;
      this.t = 0;
      this.c = false;
      this.k = false;
      this.d = null;
      this.E = 0;
      this.L = 0;
      this.n = 0;
      this.y = 0;
      this.C = 0;
   }

   public void b(byte[] var1, int var2) {
      var2 += 2;
      this.b = var1[var2++];
      this.h = Level.f[this.b];
      this.z = var1[var2++];
      byte var3 = var1[var2++];
      this.H[0] = (var3 & 1) != 0;
      this.H[1] = (var3 & 2) != 0;
      this.H[2] = (var3 & 4) != 0;
      this.H[3] = (var3 & 8) != 0;
      byte var4 = var1[var2++];
      this.e = Game.a(var1, var2) << 8;
      var2 += 2;
      this.M = Game.a(var1, var2) << 8;
      var2 += 2;
      var2 += 8;
      if (var4 > 0) {
         for (int var5 = 0; var5 < 4; var5++) {
            this.w[var5] = 0;
         }

         System.arraycopy(var1, var2, this.w, 0, var4);
      }

      this.a(this.z, 1);
      this.u();
   }

   public void a(byte[] var1, int var2) {
      var2 += 3;
      if (this.b != 4 && this.b != 3) {
         if (this.b == 6) {
            if (this.z == 12) {
               if (this.n != 0 && this.y != 0) {
                  this.z = 2;
               } else {
                  this.z = 4;
               }
            } else if (this.z == 7) {
               this.z = 8;
            } else if (this.z == 15) {
               this.H[3] = false;
            }

            var1[var2] = (byte)this.z;
         } else if (this.b == 9) {
            var1[var2] = 3;
         } else {
            var1[var2] = (byte)this.z;
         }
      }

      var2++;
      byte var3 = 0;
      if (this.H[0]) {
         var3++;
      }

      if (this.H[1]) {
         var3 = (byte)(var3 + 2);
      }

      if (this.H[2]) {
         var3 = (byte)(var3 + 4);
      }

      if (this.H[3]) {
         var3 = (byte)(var3 + 8);
      }

      var1[var2++] = var3;
      if (this.b != 4 && this.b != 3 && this.b != 9) {
         var2++;
         int var4 = this.e >> 8;
         var1[var2++] = (byte)(var4 >> 8);
         var1[var2++] = (byte)var4;
         var4 = this.M >> 8;
         var1[var2++] = (byte)(var4 >> 8);
         var1[var2++] = (byte)var4;
      }
   }

   boolean b(int var1, int var2) {
      if (!this.H[3]) {
         return false;
      }

      if (this.a == -1) {
         return false;
      }

      switch (this.b) {
         case 3:
         case 6:
         case 7:
            this.e(false);
            break;
         case 4:
            this.e = this.e + this.i;
            this.M = this.M + this.j;
         case 5:
         default:
            break;
         case 8:
            if (this.z != 3) {
               this.e(false);
            }
      }

      this.i = this.i + this.p;
      this.j = this.j + this.G;
      if (this.j > 4096) {
         this.j = 4096;
      }

      switch (this.b) {
         case 2:
            this.F();
            break;
         case 3:
            this.i();
            break;
         case 4:
            this.z();
            break;
         case 5:
            this.o();
            break;
         case 6:
            this.k();
         case 7:
         default:
            break;
         case 8:
            this.r();
            break;
         case 9:
            this.l();
            break;
         case 10:
            this.h();
      }

      return true;
   }

   void a(int var1, int var2) {
      this.k = false;
      this.z = var1;
      if (this.z == -1) {
         this.a = -1;
         this.D = -1;
         this.c();
      } else {
         this.D = 0;
         this.t = 0;
         this.a = this.h.b(this.z, this.D);
         this.c = false;
         this.c();
         if ((var2 & 1) > 0) {
            byte var3 = this.h.g[this.z];
            if ((var3 & 1) != 0) {
               this.i = this.h.f[this.z * 4 + 0];
               if (this.H[0]) {
                  this.i = -this.i;
               }
            }

            if ((var3 & 2) != 0) {
               this.j = this.h.f[this.z * 4 + 1];
               if (this.H[1]) {
                  this.j = -this.j;
               }
            }

            if ((var3 & 4) != 0) {
               this.p = this.h.f[this.z * 4 + 2];
               if (this.H[0]) {
                  this.p = -this.p;
               }
            }

            if ((var3 & 8) != 0) {
               this.G = this.h.f[this.z * 4 + 3];
               if (this.H[1]) {
                  this.G = -this.G;
               }
            }
         }
      }
   }

   void c(boolean var1) {
      if (var1) {
         this.t = -1;
      } else {
         this.c = true;
      }
   }

   int d(boolean var1) {
      if (this.a == -1) {
         return -1;
      }

      if (this.t < 0) {
         return -1;
      }

      if (!var1) {
         this.t++;
         if (this.t < this.h.a(this.z, this.D)) {
            return this.a;
         }
      }

      this.D++;
      if (this.D >= this.h.b[this.z]) {
         if (this.c) {
            this.D--;
         } else {
            this.D = 0;
         }

         this.k = true;
      }

      this.a = this.h.b(this.z, this.D);
      this.c();
      this.t = 0;
      return this.a;
   }

   public void c(int var1, int var2) {
      var1 = (this.e >> 8) - var1;
      var2 = (this.M >> 8) - var2;
      this.h.a(var1, var2, this.H[0], this.H[1], this.a);
      if (Game.G) {
         Game.G = false;
      } else {
         if (Game.av < 0 && Game.aw == 0) {
            this.d(false);
         }
      }
   }

   byte[] c() {
      for (int var1 = 0; var1 < 4; var1++) {
         this.g[var1] = 0;
      }

      byte[] var2 = this.h.i;
      if (this.a != -1 && var2 != null) {
         this.g[1] = var2[this.a * 8 + 1];
         this.g[3] = var2[this.a * 8 + 3];
         if (this.H[0]) {
            this.g[0] = (byte)(-var2[this.a * 8 + 2]);
            this.g[2] = (byte)(-var2[this.a * 8 + 0]);
         } else {
            this.g[0] = var2[this.a * 8 + 0];
            this.g[2] = var2[this.a * 8 + 2];
         }
      }

      return this.g;
   }

   byte[] d() {
      if (this.g == null) {
         this.c();
      }

      return this.g;
   }

   byte[] D() {
      byte[] var1 = this.h.i;

      for (int var2 = 0; var2 < 4; var2++) {
         this.P[var2] = 0;
      }

      if (this.a != -1 && var1 != null) {
         this.P[1] = var1[this.a * 8 + 4 + 1];
         this.P[3] = var1[this.a * 8 + 4 + 3];
         if (this.H[0]) {
            this.P[0] = (byte)(-var1[this.a * 8 + 4 + 2]);
            this.P[2] = (byte)(-var1[this.a * 8 + 4 + 0]);
         } else {
            this.P[0] = var1[this.a * 8 + 4 + 0];
            this.P[2] = var1[this.a * 8 + 4 + 2];
         }

         if (this.b != 7 && this.b != 6) {
            this.P[0] = (byte)(this.P[0] << 1);
            this.P[1] = (byte)(this.P[1] << 1);
            this.P[2] = (byte)(this.P[2] << 1);
            this.P[3] = (byte)(this.P[3] << 1);
         }
      }

      return this.P;
   }

   void b(boolean var1) {
      if (this.H[0] != var1) {
         this.H[0] = var1;
         this.i = -this.i;
         this.p = -this.p;
         this.c();
      }
   }

   boolean b(Actor var1) {
      return this.a(var1.e, var1.M, var1.D(), 0);
   }

   boolean a(Actor var1) {
      return this.a(var1.e, var1.M, var1.d(), 0);
   }

   boolean a(int var1, int var2, byte[] var3, int var4) {
      if (var3[0] == var3[2]) {
         return false;
      }

      byte[] var5 = this.d();
      var1 = var1 - this.e >> 8;
      var2 = var2 - this.M >> 8;
      return var1 + var3[0] > var5[2] + var4 || var2 + var3[1] > var5[3] + var4 ? false : var1 + var3[2] >= var5[0] - var4 && var2 + var3[3] >= var5[1] - var4;
   }

   boolean e(boolean var1) {
      if (!var1 && this.i == 0 && this.j == 0) {
         return false;
      }

      boolean var2 = this.i == 0;
      boolean var3 = this.j == 0;
      int var4 = Math.max(Math.abs(this.i / Game.W) >> 8, Math.abs(this.j / Game.aC) >> 8) + 1;
      int var5 = this.i / var4;
      int var6 = this.j / var4;
      this.m = -1;
      this.A = -1;
      this.F = -1;
      this.o = -1;
      if (var3) {
         this.a(0, false);
      }

      if (this.H[0]) {
         if (this.a(-1, false) && var5 < 0) {
            var5 = 0;
         }

         if (this.a(1, false) && var5 > 0) {
            var5 = 0;
         }
      } else {
         if (this.a(1, false) && var5 > 0) {
            var5 = 0;
         }

         if (this.a(-1, false) && var5 < 0) {
            var5 = 0;
         }
      }

      for (int var7 = 0; var7 < var4; var7++) {
         this.e += var5;
         if (this.i != 0) {
            if (this.i > 0) {
               var2 = this.a(1, false);
            } else {
               var2 = this.a(-1, false);
            }

            if (var2) {
               var5 = 0;
            }
         }

         this.M += var6;
         if (this.j != 0) {
            if (this.j > 0) {
               var3 = this.b(1, false);
            } else {
               var3 = this.b(-1, false);
            }

            if (var3) {
               var6 = 0;
            }
         }

         if (this.d != null && this.c(this.d)) {
            if (this.i == 0) {
               var5 = 0;
            }

            if (this.j == 0) {
               var6 = 0;
            }
         }
      }

      if (var6 == 0) {
         if (this.j > 0) {
            this.G = 0;
         }

         this.j = 0;
      }

      if (this.m == -1) {
         this.a(-1, true);
      }

      if (this.A == -1) {
         this.a(1, true);
      }

      if (this.F == -1) {
         this.b(-1, true);
      }

      if (this.o == -1) {
         this.b(1, true);
      }

      return true;
   }

   boolean c(Actor var1) {
      int var2 = this.e - var1.e >> 8;
      int var3 = this.M - var1.M >> 8;
      byte[] var4 = var1.d();
      if (var2 + this.g[0] > var4[2] || var2 + this.g[2] < var4[0]) {
         return false;
      }

      if (var3 + this.g[1] <= var4[3] && var3 + this.g[3] >= var4[1]) {
         if (this.j > 0) {
            if (var3 + this.g[3] >= var4[1]) {
               var3 = var3 + this.g[3] - var4[1] << 8;
               if (var3 <= this.j) {
                  this.M -= var3;
                  this.o = 2;
                  this.i = 0;
                  this.j = 0;
                  this.G = 0;
                  return true;
               }
            }
         } else if (this.j < 0) {
            if (var3 + this.g[1] <= var4[3]) {
               var3 = var3 + this.g[1] - var4[3] << 8;
               if (var3 >= this.j) {
                  this.M -= var3;
                  this.F = 2;
                  this.j = 0;
                  return true;
               }
            }
         } else if (var3 + this.g[3] == var4[1]) {
            this.o = 2;
            return true;
         }

         if (var2 + this.g[0] < var4[0]) {
            if (var2 > var4[2]) {
               this.m = 2;
            } else {
               this.A = 2;
            }
         } else if (var2 + this.g[2] > var4[2]) {
            if (var2 < var4[0]) {
               this.A = 2;
            } else {
               this.m = 2;
            }
         } else if (this.H[0]) {
            this.m = 2;
         } else {
            this.A = 2;
         }

         if (this.m == 2) {
            if (this.A <= 2) {
               this.e = this.e - (var2 + this.g[0] - var4[2] << 8);
            }
         } else if (this.A == 2 && this.m <= 2) {
            this.e = this.e - (var2 + this.g[2] - var4[0] << 8);
         }

         return true;
      } else {
         return false;
      }
   }

   boolean a(int var1, boolean var2) {
      int var3 = this.e >> 8;
      if (var1 == -1) {
         var3 += this.g[0];
         if (var2) {
            var3--;
         }
      } else if (var1 == 1) {
         var3 += this.g[2];
         if (var2) {
            var3++;
         }
      }

      if (var3 < 0) {
         var3 = -1;
      } else {
         var3 /= Game.W;
      }

      int var4 = ((this.M >> 8) + this.g[1] + 1) / Game.aC;
      int var5 = ((this.M >> 8) + this.g[3]) / Game.aC;
      byte var6 = 0;

      for (int var7 = var5; var7 >= var4; var7--) {
         var6 = Game.e(var3, var7);
         if (var6 == 3) {
            if (var7 != var4 && Game.e(var3, var7 - 1) == 3) {
               var6 = 4;
            }

            if (var1 != -1 && (var1 != 0 || !this.H[0])) {
               this.e = var3 * Game.W - 1 - this.g[2] << 8;
            } else {
               this.e = (var3 + 1) * Game.W - this.g[0] << 8;
            }

            var2 = true;
            break;
         }
      }

      if (var2) {
         if (var1 == -1) {
            this.m = var6;
         } else if (var1 == 1) {
            this.A = var6;
         }
      }

      return var6 >= 3;
   }

   boolean b(int var1, boolean var2) {
      int var3 = ((this.e >> 8) + this.g[0]) / Game.W;
      int var4 = ((this.e >> 8) + this.g[2]) / Game.W;
      int var5 = this.M >> 8;
      if (var1 == -1) {
         var5 += this.g[1];
         if (var2) {
            var5--;
         }
      } else {
         var5 += this.g[3];
         if (var2) {
            var5++;
         }
      }

      var5 /= Game.aC;
      byte var6 = 0;

      for (int var7 = var3; var7 <= var4; var7++) {
         var6 = Game.e(var7, var5);
         if (var6 == 3) {
            if (var1 == -1) {
               this.M = (var5 + 1) * Game.aC - this.g[1] << 8;
            } else {
               this.M = var5 * Game.aC - 1 - this.g[3] << 8;
            }

            var2 = true;
            break;
         }
      }

      if (var2) {
         if (var1 == -1) {
            this.F = var6;
         } else {
            this.o = var6;
         }
      }

      return var6 == 3;
   }

   int a(boolean var1) {
      byte var4 = this.g[3];
      byte var2;
      byte var3;
      byte var5;
      if (var1) {
         var2 = this.g[0];
         var3 = this.g[2];
         var5 = 1;
      } else {
         var2 = this.g[2];
         var3 = this.g[0];
         var5 = -1;
      }

      int var6 = ((this.e >> 8) + var2) / Game.W;
      int var7 = ((this.e >> 8) + var3) / Game.W + var5;
      int var8 = ((this.M >> 8) + var4 + 1) / Game.aC;
      byte var9 = 3;

      for (int var11 = var6; var11 != var7; var11 += var5) {
         byte var10 = Game.e(var11, var8);
         if (var10 == 3) {
            if (var9 != 3) {
               var11 -= var5;

               for (int var12 = var8 + 1; var12 > var8; var12--) {
                  var10 = Game.e(var11, var12);
                  if (var10 == 3) {
                     return 1;
                  }
               }

               var11 += var5;
               if (var1) {
                  this.s = var11 * Game.W - 1 << 8;
               } else {
                  this.s = (var11 + 1) * Game.W << 8;
               }

               this.l = var8 * Game.aC - 1 << 8;
               return 2;
            }
            break;
         }

         var9 = var10;
      }

      return 0;
   }

   void u() {
      switch (this.b) {
         case 3:
            this.n = this.y = this.M;
            break;
         case 4:
            switch (this.z) {
               case 0:
               case 1:
                  this.n = this.e - ((this.w[0] & 255) << 8);
                  this.y = this.e + ((this.w[1] & 255) << 8);
                  break;
               default:
                  this.n = this.M - ((this.w[0] & 255) << 8);
                  this.y = this.M + ((this.w[1] & 255) << 8);
            }
         case 5:
         case 7:
         case 8:
         default:
            break;
         case 6:
            this.E = this.L = Game.au < 3 ? 2 : 3;
            this.C = 0;
            if (this.z == 2) {
               this.n = this.y = this.e;
            } else if (this.z == 3) {
               this.n = this.e;
            }
            break;
         case 9:
            this.E = this.L = 2;
            break;
         case 10:
            switch (this.z) {
               case 1:
                  if (Prince.B[this.w[0]] > 0) {
                     this.H[3] = false;
                  }
               case 2:
               case 24:
               default:
                  break;
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
                  this.a(this.z, 0);
                  this.c(true);
                  break;
               case 27:
               case 28:
                  this.a(this.z - 27 + 25, 1);
               case 25:
               case 26:
                  if (Level.i.J > 50 && Level.i.a(this)) {
                     this.a(this.z - 25 + 27, 1);
                     Level.i.J = 50;
                  }
            }
      }
   }

   boolean j() {
      Actor var1 = Level.a(this);
      if (var1 == null) {
         return false;
      }

      switch (var1.b) {
         case 4:
            var1.a(4, 1);
         case 3:
         case 5:
            return true;
         case 6:
         case 7:
            return this.w[1] > 0;
         default:
            return false;
      }
   }

   void r() {
      int var1 = Game.au;
      switch (this.z) {
         case 0:
         case 4:
            this.i = 0;
            if (this.j()) {
               if (this.w[2] >= 0) {
                  byte var4 = this.w[this.w[3] >= 0 && Game.i % 2L == 0L ? 3 : 2];
                  if (Level.p[var1][var4] == null) {
                     Level.p[var1][var4] = Level.c(var4);
                  }

                  Actor var3 = Level.p[var1][var4];
                  var3.H[3] = true;
                  var3.H[2] = true;
                  var3.e = this.e + (this.d()[0] + this.d()[2] << 7);
                  var3.M = this.M;
               }

               Level.i.a(2, this);
               this.a(this.z + 2, 0);
            } else if (this.o < 2) {
               Level.i.a(2, this);
               this.a(this.z + 1, 1);
            } else if (Level.i.a(this.e, this.M, this.d(), 16)) {
               Level.i.a(1, this);
            } else {
               Level.i.a(2, this);
            }
            break;
         case 1:
         case 5:
            if (this.j()) {
               Level.i.a(2, this);
               this.a(this.z + 1, 0);
            } else if (this.o >= 2) {
               this.a(this.z - 1, 1);
            }
            break;
         case 2:
         case 6:
            if (this.k) {
               this.H[3] = false;
            }
            break;
         case 3:
            Actor var2 = Level.i.d;
            if (var2 != null && Math.abs(var2.M - this.M) < 1024 && (var2.d == null || Math.abs(var2.d.e - var2.e) > Math.abs(this.e - var2.e))) {
               var2.d = this;
               this.d = var2;
            }
      }
   }

   void a(byte var1) {
      if (var1 >= 0 && Game.w > 0) {
         if (var1 >= 13 && var1 <= 27 && Game.h < 3) {
            var1 = (byte)(var1 + (Game.h - 0));
         }

         Game.av = var1;
         Game.t = 0;
         Game.Q = Game.i + 200L;
         Level.i.a(0, 1);
         Game.a();
      }
   }

   void h() {
      if (this.z != 27 && this.z != 28) {
         if (this.z >= 3 && this.z <= 23 && this.k) {
            this.a(this.z, 0);
            this.c(true);
         } else if (Level.i.a(this)) {
            int var1 = 0;
            boolean var2 = false;
            switch (this.z) {
               case 2:
                  if (Game.w == 0) {
                     return;
                  }
               case 1:
                  if (Level.i.z > 1 && Level.i.z != 5 && Level.i.o < 2) {
                     Level.i.a(16, 1);
                     Game.a();
                     Level.i.i = 0;
                     break;
                  }
               case 0:
                  if (this.z == 1) {
                     if (Level.i.b(this.w[0])) {
                        this.a(0, 0);
                     } else {
                        this.H[3] = false;
                     }
                  } else if (this.k) {
                     this.H[3] = false;
                     this.a(this.w[1]);
                  }
                  break;
               case 3:
               case 4:
               case 5:
               case 10:
               case 11:
               case 12:
               case 17:
               case 18:
               case 19:
                  if (this.z >= 3 && this.z <= 5) {
                     var1 = this.z - 3 + 2;
                  } else if (this.z >= 10 && this.z <= 12) {
                     var1 = this.z - 10 + 2;
                  } else {
                     var1 = this.z - 17 + 2;
                  }

                  if (Prince.B[var1] <= 0 || Level.i.H[0] != this.H[0]) {
                     break;
                  }
               case 6:
               case 7:
               case 8:
               case 9:
               case 13:
               case 14:
               case 15:
               case 16:
               case 20:
               case 21:
               case 22:
               case 23:
                  if (Game.w != 0) {
                     if (this.z >= 3 && this.z <= 9) {
                        if (Game.h == 1) {
                           this.c(true);
                           this.a(this.z + 7, 0);
                           this.d(true);
                        } else if (Game.h == 2) {
                           this.c(true);
                           this.a(this.z + 14, 0);
                           this.d(true);
                        }
                     } else if (this.z >= 10 && this.z <= 16) {
                        if (Game.h == 0 || Game.h == 3 || Game.h == 4) {
                           this.c(true);
                           this.a(this.z - 7, 0);
                           this.d(true);
                        }
                     } else if (Game.h == 0 || Game.h == 3 || Game.h == 4) {
                        this.c(true);
                        this.a(this.z - 14, 0);
                        this.d(true);
                     }

                     if (this.t < 0) {
                        this.t = 0;
                     }
                  }
                  break;
               case 24:
                  PrinceSOT.c.a(true);
                  break;
               case 25:
               case 26:
                  if (Level.i.J == 0) {
                     Level.i.a(4, this);
                     this.a(this.z - 25 + 27, 1);
                  }

                  Level.i.J = 50;
               case 27:
               case 28:
               default:
                  break;
               case 29:
                  if (Level.i.g()) {
                     this.H[3] = false;
                  }
                  break;
               case 30:
                  var1 = this.w[0];
                  var2 = Prince.B[var1] <= 0;
                  if (Level.i.b(var1)) {
                     this.H[3] = false;
                     if (var2) {
                        this.a(this.w[1]);
                     }
                  }
            }
         }
      } else {
         if (this.k) {
            this.a(this.z - 27 + 25, 1);
         }
      }
   }

   void o() {
      if (Level.i.b(this)) {
         Level.i.a(3, this);
      }
   }

   void F() {
      int var1 = Game.au;
      switch (this.z) {
         case 0:
            if (Level.b(this)) {
               this.a(1, 0);
               if (Level.p[var1][this.w[0]] != null && Level.p[var1][this.w[0]].z == 2) {
                  Level.p[var1][this.w[0]].a(0, 1);
               }
            }
            break;
         case 1:
            if (!Level.b(this)) {
               this.a(0, 0);
            }
      }
   }

   void i() {
      switch (this.z) {
         case 0:
            if (this.k) {
               this.a(3, 1);
               Game.a(1, 1);
            }
            break;
         case 1:
            if (this.k) {
               if (this.w[0] > 0) {
                  this.M = this.y;
                  this.a(2, 1);
                  this.o = 0;
               } else {
                  this.H[3] = false;
               }
            }
            break;
         case 2:
            this.M = this.y;
            break;
         case 3:
            if (Level.i.b(this)) {
               Level.i.a(3, this);
            }

            if (this.o >= 3) {
               this.a(1, 1);
            }
      }
   }

   void l() {
      switch (this.z) {
         case 0:
         case 1:
         case 6:
            if (Level.n == null) {
               Level.n = this;
            }

            short var4;
            if (this.z == 0) {
               var4 = 768;
            } else {
               var4 = 1536;
            }

            if (this.z == 6) {
               if (this.H[0]) {
                  this.e -= var4;
               } else {
                  this.e += var4;
               }

               if (this.k) {
                  this.a(0, 1);
                  break;
               }
            } else {
               this.b(this.e > Level.i.e);
               if (this.e > Level.i.e) {
                  this.e -= var4;
               } else {
                  this.e += var4;
               }

               int var3 = Level.i.M + (Level.i.d()[1] + Level.i.d()[3] << 7);
               if (this.M > var3) {
                  this.M -= var4;
                  if (this.M < var3) {
                     this.M = var3;
                  }
               } else {
                  this.M += var4;
                  if (this.M > var3) {
                     this.M = var3;
                  }
               }
            }

            if (Level.i.b(this)) {
               Level.i.a(3, this);
               this.a(6, 1);
            } else if (this.b(Level.i)) {
               if (--this.E > 0) {
                  this.a(2, 1);
               } else {
                  this.a(5, 1);
                  if (Level.n.equals(this)) {
                     Level.n = null;
                  }
               }
            }
            break;
         case 2:
            short var2 = 1024;
            if (this.e > Level.i.e) {
               this.e += var2;
            } else {
               this.e -= var2;
            }

            int var1 = Level.i.M + (Level.i.d()[1] + Level.i.d()[3] << 7);
            if (this.M > var1) {
               this.M += var2 / 2;
            } else {
               this.M -= var2 / 2;
            }

            if (this.k) {
               this.a(1, 1);
            }
            break;
         case 3:
            if (Math.abs(this.e - Level.i.e) < 40960 && Math.abs(this.M - Level.i.M) < 23040) {
               this.H[2] = true;
               this.a(4, 1);
               this.b(this.e > Level.i.e);
            }
            break;
         case 4:
            if (this.k) {
               this.a(0, 1);
               this.C = 0;
            }
            break;
         case 5:
            if (this.k) {
               this.H[2] = false;
               this.H[3] = false;
            }
      }
   }

   boolean t() {
      Actor var1 = Level.a(this);
      if (var1 == null) {
         return false;
      } else if (var1.b == 7) {
         this.b(var1.e > this.e);
         return false;
      } else {
         return true;
      }
   }

   boolean E() {
      Actor var1 = Level.a(this);
      if (var1 == null) {
         return false;
      }

      switch (var1.b) {
         case 6:
            if (var1.z != 13 || var1.e > this.e ^ var1.i < 0) {
               return false;
            }

            var1.a(14, 1);
         case 7:
            this.E--;
            break;
         default:
            this.E = 0;
      }

      if (this.z < 8) {
         if (this.E <= 0) {
            this.C = 45;
            this.a(5, 1);
         } else {
            this.a(17, 1);
         }
      } else if (this.E <= 0) {
         this.C = 45;
         this.a(10, 1);
      } else {
         this.a(16, 1);
      }

      return true;
   }

   boolean s() {
      return this.e > Level.i.e ^ this.H[0] ? false : Math.abs(this.e - Level.i.e) < 37376 && this.v();
   }

   boolean v() {
      byte[] var1 = Level.i.d();
      return Level.i.Q != 19 && (Level.i.M >> 8) + var1[3] >= (this.M >> 8) + this.d()[1] && (Level.i.M >> 8) + var1[1] < (this.M >> 8) + this.d()[3];
   }

   boolean m() {
      if (this.n != 0) {
         if (this.y != 0) {
            this.a(2, 1);
            this.b(Level.i.e < this.e);
         } else if (this.n != this.e) {
            if (this.z == 4) {
               return false;
            }

            this.a(4, 1);
            this.b(this.n < this.e);
         } else {
            this.a(3, 1);
            this.b(Level.i.e < this.e);
         }

         return true;
      } else {
         return false;
      }
   }

   void x() {
      if (Level.i.z != 19) {
         if (Level.i.i == 0 && Level.i.z != 9 && (this.z == 4 || this.z == 0 || this.z == 1)) {
            Level.i.c(this);
         }

         int var2 = Level.i.e;
         if (Math.abs(this.M - Level.i.M) <= this.g[3] - this.g[1] << 8) {
            if (this.e > Level.i.e) {
               int var1 = this.e + (this.g[0] - Level.i.d()[2] << 8);
               if (Level.i.e > var1) {
                  if (Level.i.z == 9) {
                     if (this.H[0] && Level.i.H[0]) {
                        this.e = Level.i.e + (Level.i.d()[2] - this.g[0] << 8);
                     }
                  } else if (Level.i.m >= 2) {
                     this.c(Level.i);
                  } else if (Level.i.e - var1 > 2048) {
                     Level.i.e = Level.i.e - (2048 + Level.i.i);
                  } else {
                     Level.i.e = var1;
                  }
               }
            } else {
               int var3 = this.e + (this.g[2] - Level.i.d()[0] << 8);
               if (Level.i.e < var3) {
                  if (Level.i.z == 9) {
                     if (!this.H[0] && !Level.i.H[0]) {
                        this.e = Level.i.e + (Level.i.d()[0] - this.g[2] << 8);
                     }
                  } else if (Level.i.A >= 2) {
                     this.c(Level.i);
                  } else if (var3 - Level.i.e > 2048) {
                     Level.i.e = Level.i.e + (2048 - Level.i.i);
                  } else {
                     Level.i.e = var3;
                  }
               }
            }

            if (var2 != Level.i.e) {
               Prince.x = true;
            }
         }
      }
   }

   boolean w() {
      if (this.w[0] >= 0) {
         Prince.v = false;
         Game.a();
         Game.ay[4] = 1;
      }

      if (Game.ay[4] != 1) {
         return false;
      }

      if (Level.i.M <= this.M + 1024 && Level.i.M >= this.M - 1024) {
         if (Level.i.z <= 1 || Level.i.z == 6 && Level.i.D == 0) {
            int var1 = this.g[2] - this.g[0];
            int var2 = (this.e >> 8) + (this.g[2] + this.g[0] >> 1) - (Level.i.e >> 8);
            if (Level.i.H[0]) {
               var2 = -var2;
            }

            if (this.w[0] >= 0 || var2 >= -var1 / 2 && var2 <= var1 * 2) {
               Level.i.a(14, 1);
               Level.i.c(false);
               Level.i.i = (var2 - var1 << 8) / 3;
               if (Level.i.H[0]) {
                  Level.i.i = -Level.i.i;
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   void k() {
      boolean var1 = Level.i.b(this);
      if (var1) {
         Level.i.a(3, this);
      }

      switch (this.z) {
         case 0:
         case 1:
         case 2:
            this.x();
            if (!this.E()) {
               if (this.z == 1 && this.D == this.h.b[this.z] - 1) {
                  Level.e += 2;
               }

               if (this.k) {
                  if (this.s()) {
                     if (Math.abs(this.e - Level.i.e) <= 12800) {
                        if (this.n != 0) {
                           this.C = 1 - this.C;
                        }

                        this.a(this.C == 1 ? 0 : 1, 1);
                        Game.a(5, 1);
                     } else if (this.n != 0 && this.y != 0) {
                        this.a(2, 1);
                     } else {
                        this.a(4, 1);
                     }
                  } else if (!this.m()) {
                     this.a(4, 1);
                  }
               }
            }
            break;
         case 3:
            this.x();
            if (!this.E() && this.s()) {
               this.a(2, 1);
            }
            break;
         case 4:
            this.x();
            label250:
            if (!this.E()) {
               if (this.s()) {
                  if (Math.abs(this.e - Level.i.e) <= 12800) {
                     if (Level.i.z == 14) {
                        this.a(2, 1);
                     } else {
                        this.b(this.e > Level.i.e);
                        this.a(this.C == 1 ? 0 : 1, 1);
                        Game.a(5, 1);
                     }
                     break label250;
                  }
               } else if (this.m()) {
                  break label250;
               }

               if (this.a(this.H[0]) > 0 || this.H[0] && this.m >= 3 || !this.H[0] && this.A >= 3) {
                  this.b(!this.H[0]);
               }
            }
            break;
         case 5:
            if (this.k) {
               this.a(this.w[0]);
               if (Game.w == 0) {
                  this.w[0] = -1;
               }

               this.a(6, 1);
            }
            break;
         case 6:
            if (Level.i.z == 15) {
               if (this.b(Level.i)) {
                  this.a(7, 1);
               }
            } else if (!this.w()) {
               if (this.y == 0) {
                  if (Level.i.z != 14) {
                     this.C--;
                     if (this.C <= 0) {
                        this.E = this.L;
                        this.b(this.e > Level.i.e);
                        this.a(17, 1);
                        this.x();
                     }
                  }
               } else {
                  this.a(7, 1);
               }
            }
            break;
         case 7:
         case 12:
            if (Level.i.z != 14 && Level.i.z != 15 && this.y == 0) {
               this.C = 45;
               this.a(this.z - 1, 1);
            }

            if (this.k) {
               Level.i.a(0, 1);
               this.a(15, 1);
            }
            break;
         case 8:
            this.x();
            this.c(false);
            if (!this.E() && Math.abs(this.M - Level.i.M) <= 12800) {
               this.b(this.e > Level.i.e);
               if (this.s() && this.d != null && !this.d.H[3]) {
                  this.a(9, 1);
               }
            }
            break;
         case 9:
            this.x();
            if (!this.E() && this.D >= this.h.b[this.z] - 1) {
               if (this.k) {
                  this.a(this.z - 1, 1);
               } else {
                  Game.a(13, 1);
                  this.d.H[3] = true;
                  this.d.b(this.H[0]);
                  this.d.e = this.e + this.d.i + (this.H[0] ? -1536 : 1536);
                  this.d.n = this.e;
                  this.d.M = this.M + (this.d()[1] + 9 << 8);
                  this.d.a(13, 1);
               }
            }
            break;
         case 10:
            if (this.k) {
               this.a(this.w[0]);
               if (Game.w == 0) {
                  this.w[0] = -1;
               }

               this.a(11, 1);
            }
            break;
         case 11:
            if (Level.i.z == 15) {
               if (this.b(Level.i)) {
                  this.a(12, 1);
               }
            } else if (!this.w()) {
               if (this.y == 0) {
                  if (Level.i.z != 14) {
                     this.C--;
                     if (this.C <= 0) {
                        this.E = this.L;
                        this.b(this.e > Level.i.e);
                        this.a(16, 1);
                        this.x();
                     }
                  }
               } else {
                  this.a(12, 1);
               }
            }
            break;
         case 13:
            if (this.m >= 3 || this.A >= 3 || var1 || this.t() || this.e - this.n >> 8 > 176) {
               this.a(14, 1);
            }
            break;
         case 14:
            if (this.k) {
               this.H[3] = false;
            }
            break;
         case 15:
            if (this.k) {
               this.H[3] = false;
            }
            break;
         case 16:
            this.x();
            if (this.k) {
               if (this.H[0] != this.e > Level.i.e) {
                  this.b(!this.H[0]);
                  this.k = false;
               } else {
                  this.a(9, 1);
               }
            }
            break;
         case 17:
            this.x();
            if (this.k) {
               if (this.H[0] != this.e > Level.i.e) {
                  this.b(!this.H[0]);
                  this.k = false;
               } else {
                  this.a(1, 1);
               }
            }
      }

      if (this.z == 17 && this.a(!this.H[0]) > 0) {
         this.i = 0;
      }
   }

   void z() {
      if (Level.i.b(this)) {
         Level.i.a(3, this);
      }

      switch (this.z) {
         case 0:
            if (this.n >= this.e) {
               this.a(1, 1);
            }
            break;
         case 1:
            if (this.y <= this.e) {
               this.a(0, 1);
            }
            break;
         case 2:
            if (this.n >= this.M) {
               this.a(3, 1);
            }
            break;
         case 3:
            if (this.y <= this.M) {
               this.a(2, 1);
            }
            break;
         case 4:
            if (this.k) {
               this.H[3] = false;
            }
      }
   }
}
