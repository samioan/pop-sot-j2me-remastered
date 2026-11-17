package pop2;

public class d extends a {
   int Q;
   int q;
   int K;
   int O;
   static byte[] B = new byte[10];
   static int u;
   static int I;
   static boolean v = false;
   static boolean x = false;
   boolean N = false;
   boolean f;
   int r;
   int J;

   void q() {
      this.Q = 0;
      this.q = 0;
      this.K = 0;
      this.O = 0;
      this.N = false;
      this.f = false;
      this.r = 0;
      this.J = 0;
   }

   void u() {
      pop2.b.k = true;
      this.r++;
      super.n = super.e;
      super.y = super.M;
      this.f = super.H[0];
      super.L = 3 + B[0] * 1;
      super.E = super.L;
      this.O = 60 + B[5] * 40;
      this.K = this.O;
      u = 44;
      I = -34;
      super.d = null;
   }

   boolean g() {
      if (super.E >= super.L) {
         return false;
      }

      this.a(23, 1);
      pop2.e.a(3, 1);
      pop2.e.a();
      super.E++;
      return true;
   }

   boolean b(int var1) {
      if (var1 == 0) {
         super.L = 3 + B[0] * 1;
         super.E = super.L;
         return true;
      }

      if (B[var1] > 0) {
         if (var1 < 6 || super.E >= super.L) {
            return false;
         }

         super.E = super.L;
      } else {
         if (var1 == 5) {
            this.O = 100;
            this.K = this.O;
         } else if (var1 >= 6) {
            super.L++;
            if (B[0] < 4) {
               B[0]++;
            }
         }

         super.E = super.L;
         B[var1] = 1;
      }

      this.a(24, 1);
      pop2.e.a(4, 1);
      pop2.e.a();
      return true;
   }

   int a(int var1) {
      this.K -= var1;
      if (this.K < 0) {
         this.K = 0;
      }

      return this.K;
   }

   void e() {
      super.d.i = super.i;
      if (super.H[0]) {
         if (super.m < 3) {
            super.e = super.d.e + (super.d.d()[2] - this.d()[0] << 8);
            return;
         }
      } else if (super.A < 3) {
         super.e = super.d.e + (super.d.d()[0] - this.d()[2] << 8);
         return;
      }

      this.a(2, super.d);
   }

   void a(int var1, a var2) {
      switch (var1) {
         case 1:
            if (super.d == null || !super.d.equals(var2)) {
               super.d = var2;
               this.c(super.d);
            }

            if (super.H[0] ^ super.e < var2.e
               && super.z == 1
               && var2.w[0] > 0
               && Math.abs(super.M - var2.M) < 1024
               && (!super.H[0] || super.m < 3)
               && (super.H[0] || super.A < 3)) {
               this.a(4, 1);
               this.e();
            }
            break;
         case 2:
            if (super.d != null && super.d.equals(var2)) {
               super.d.i = 0;
               super.d = null;
               if (super.z != 12 && super.z != 13 && super.z != 4) {
                  this.b(1, true);
               } else {
                  this.a(0, 1);
               }
            }
            break;
         case 3:
            if (super.C == 0 && super.z != 17 && super.z != 19) {
               if (super.z == 4 || super.z == 13 || super.z == 12) {
                  super.d.i = 0;
                  super.d = null;
               }

               pop2.e.U++;
               if (super.z == 9) {
                  this.y();
               }

               if (--super.E > 0) {
                  if (super.z != 16) {
                     this.a(17, 1);
                  }

                  if (var2.b != 4 && var2.b != 5) {
                     if (var2.b != 6 || !var2.equals(pop2.b.a)) {
                        this.b(super.e > var2.e);
                     } else if (super.z != 16 && super.z != 22 && super.z != 3) {
                        this.b(super.e > var2.e);
                     }
                  }

                  super.C = 30;
                  pop2.e.a(6, 1);
               } else {
                  this.a(19, 1);
                  this.c(false);
                  pop2.e.a(2, 1);
               }
            }
            break;
         case 4:
            super.n = var2.e;
            super.y = var2.M;
            this.f = super.H[0];
            pop2.b.b(true);
      }
   }

   private boolean f() {
      if (super.H[0] && super.m == 4 || !super.H[0] && super.A == 4) {
         if (B[3] > 0 && this.K >= 26) {
            this.b(!super.H[0]);
            this.q = 21;
            this.a(26);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean B() {
      int var1 = ((super.e >> 8) + (super.H[0] ? super.g[0] - 15 : super.g[2] + 15)) / pop2.e.W;
      int var2 = var1 + (super.H[0] ? 1 : -1);
      int var3 = ((super.M >> 8) + super.g[3]) / pop2.e.aC;
      int var4 = ((super.M >> 8) + super.g[1]) / pop2.e.aC;
      int var5 = var4 - (var3 - var4);

      for (int var6 = var5; var6 <= var3; var6++) {
         if (pop2.e.e(var2, var6) == 3) {
            return false;
         }

         if (pop2.e.e(var1, var6) == 3) {
            if (var6 < var3 && var6 >= var4) {
               if (super.H[0]) {
                  super.s = (var1 + 1) * pop2.e.W << 8;
               } else {
                  super.s = var1 * pop2.e.W - 1 << 8;
               }

               super.l = var6 * pop2.e.aC - 1 << 8;
               return true;
            }

            return false;
         }
      }

      if (super.d != null) {
         byte[] var7 = super.d.d();
         if (super.M == super.d.M) {
            if (super.H[0] && super.e > super.d.e) {
               super.s = super.d.e + (var7[2] << 8);
               super.l = super.d.M + (var7[1] << 8);
               return true;
            }

            if (!super.H[0] && super.e < super.d.e) {
               super.s = super.d.e + (var7[0] << 8);
               super.l = super.d.M + (var7[1] << 8);
               return true;
            }
         }
      }

      return false;
   }

   private boolean p() {
      int var6 = super.H[0] ? -1 : 1;
      byte[] var7 = this.d();
      int var3 = ((super.e >> 8) + (super.H[0] ? var7[0] : var7[2])) / pop2.e.W;
      int var5 = ((super.M >> 8) + var7[1]) / pop2.e.aC - 1;

      for (int var1 = 0; var1 < 2; var5--) {
         int var4 = var3;

         for (int var2 = 0; var2 < 3; var4 += var6) {
            byte var8 = pop2.e.e(var4, var5);
            if (var8 == 3) {
               if (pop2.e.e(var4, var5 - 1) != 3 && pop2.e.e(var4 - var6, var5) != 3) {
                  if (super.H[0]) {
                     super.s = (var4 + 1) * pop2.e.W << 8;
                  } else {
                     super.s = var4 * pop2.e.W - 1 << 8;
                  }

                  super.l = var5 * pop2.e.aC - 1 << 8;
                  return true;
               }
               break;
            }

            var2++;
         }

         var1++;
      }

      return false;
   }

   private boolean n() {
      int var1 = ((super.e >> 8) + (super.H[0] ? super.g[0] - 7 : super.g[2] + 7)) / pop2.e.W;
      int var2 = ((super.M >> 8) + super.g[1] + 3) / pop2.e.aC;
      if (pop2.e.e(var1, var2) == 3
         && pop2.e.e(var1, var2 - 1) != 3
         && pop2.e.e(var1 - (super.H[0] ? -1 : 1), var2) != 3
         && pop2.e.e(var1 - (super.H[0] ? -1 : 1), var2 - 1) != 3) {
         if (super.H[0]) {
            super.s = (var1 + 1) * pop2.e.W << 8;
         } else {
            super.s = var1 * pop2.e.W + 1 << 8;
         }

         super.l = var2 * pop2.e.aC - 1 << 8;
         return true;
      } else {
         return false;
      }
   }

   private boolean A() {
      int var1 = super.e;
      int var2 = super.M;
      super.e = super.s;
      super.M = super.l;
      this.a(9, 0);
      int var3 = ((super.e >> 8) + (super.g[0] + super.g[2]) / 2) / pop2.e.W;
      int var4 = ((super.M >> 8) + super.g[3]) / pop2.e.aC;
      if (pop2.e.e(var3, var4) != 3 && pop2.e.e(var3, var4 - 1) != 3) {
         if (!super.H[0]) {
            super.e += 512;
         }

         this.q = 9;
         return true;
      } else {
         super.e = var1;
         super.M = var2;
         this.a(this.Q, 0);
         return false;
      }
   }

   private void y() {
      byte[] var1 = this.d();
      super.M = super.M + (var1[3] - var1[1] << 8);
      this.a(16, 1);
      this.Q = this.q = 16;
      var1 = this.d();
      int var2 = (var1[2] - var1[0]) / 2 + 4 << 8;
      super.e = super.e + (super.H[0] ? var2 : -var2);
      if (this.b(1, true)) {
         this.a(0, 1);
         this.Q = this.q = 0;
      } else {
         this.c(false);
      }
   }

   private final boolean C() {
      return (super.H[0] || super.m < 3 && this.a(true) <= 0) && (!super.H[0] || super.A < 3 && this.a(false) <= 0);
   }

   private boolean a() {
      if (B[4] > 0 && (pop2.e.ay[2] == 3 || pop2.e.ay[3] == 3)) {
         if (this.K > 12) {
            this.q = 8;
            return true;
         }

         if (pop2.e.ay[2] == 3) {
            pop2.e.ay[2] = 2;
         } else {
            pop2.e.ay[3] = 2;
         }
      }

      this.q = 1;
      return false;
   }

   public boolean b(int var1, int var2) {
      x = false;
      if (!super.H[3]) {
         return false;
      }

      if (super.a == -1) {
         return false;
      }

      super.b(var1, var2);
      if (this.K < this.O) {
         this.K++;
      }

      if (super.C > 0) {
         super.C--;
      }

      if (this.J > 0) {
         this.J--;
      }

      this.Q = this.q = super.z;
      switch (this.Q) {
         case 1:
            if (super.o < 2) {
               this.q = 16;
            } else if (!this.a()) {
               if (pop2.e.ay[0] == 1) {
                  this.q = 3;
               } else if (pop2.e.ay[2] < 1 && pop2.e.ay[3] < 1 || super.H[0] != pop2.e.ay[2] >= 1) {
                  this.q = 0;
               }
            }
            break;
         case 2:
            super.i = (super.s - super.e) / 16;
            if ((this.n() || super.F >= 2 || super.j >= 0) && !this.A()) {
               this.q = 16;
            }
            break;
         case 3:
         case 22:
            if (super.j == 0 || super.j > 4608) {
               this.a(16, 1);
               this.Q = this.q = 16;
               this.c(false);
            }
         case 16:
            label353:
            if (!this.n() || this.N || !this.A()) {
               if (pop2.e.ay[0] == 1) {
                  if (this.f()) {
                     this.N = false;
                     break label353;
                  }

                  if (super.z != 22 && super.o == 1 && B[2] > 0) {
                     this.N = false;
                     this.q = 11;
                     break label353;
                  }
               }

               if (super.j >= 0 && super.o >= 2) {
                  this.N = false;
                  if (super.F >= 3) {
                     if (!super.H[0] && super.m >= 3) {
                        super.e = super.e + (pop2.e.W << 8);
                     } else if (super.H[0] && super.A >= 3) {
                        super.e = super.e - (pop2.e.W << 8);
                     }
                  } else {
                     this.q = 5;
                  }
               }
            }
            break;
         case 4:
            if (pop2.e.ay[2] >= 1 || pop2.e.ay[3] >= 1) {
               boolean var4 = pop2.e.ay[2] >= 1;
               if ((!var4 || super.e <= super.d.e) && (var4 || super.e >= super.d.e)) {
                  if (!this.C()) {
                     break;
                  }

                  this.q = 13;
               } else {
                  this.q = 12;
               }

               this.a(this.q, 1);
               this.Q = this.q;
               this.e();
            } else if (pop2.e.ay[4] >= 1 || pop2.e.ay[1] >= 1) {
               this.q = 0;
               super.d.i = 0;
               super.e = super.e + (super.H[0] ? -(pop2.e.W >> 2) << 8 : pop2.e.W >> 2 << 8);
            } else if (pop2.e.ay[0] >= 1 && this.B()) {
               super.e = super.s;
               super.M = super.l;
               this.Q = this.q = 10;
               this.a(10, 1);
               this.d(true);
               this.d(true);
            }
            break;
         case 5:
            if (super.k) {
               this.q = 0;
            }
            break;
         case 6:
            if (super.k) {
               this.q = 0;
            }
            break;
         case 7:
            if (super.o < 2) {
               this.q = 16;
               byte[] var3 = this.d();
               super.M = super.M + ((var3[3] - var3[1]) / 2 << 8);
            } else if (this.a(6) <= 0 || pop2.e.ay[2] != 3 && pop2.e.ay[3] != 3) {
               if (pop2.e.ay[2] < 1 && pop2.e.ay[3] < 1) {
                  this.q = 0;
               } else {
                  this.b(pop2.e.ay[2] >= 1);
                  this.q = 0;
               }
            }
            break;
         case 8:
            if (super.k) {
               this.q = 7;
            }
            break;
         case 9:
            if (pop2.e.ay[0] == 1) {
               this.q = 10;
            } else if (pop2.e.ay[1] == 1) {
               this.N = true;
               this.y();
            } else if (pop2.e.ay[4] == 1) {
               I = -I;
            }
            break;
         case 10:
            if (super.D >= super.h.b[super.z] - 1) {
               super.e = super.e + ((super.H[0] ? -2 : 2) << 8);
               this.q = 0;
            }
            break;
         case 11:
            if (this.a(6) == 0 || pop2.e.ay[0] < 2 || super.o != 1 || super.m >= 2 || super.A >= 2 || x) {
               this.q = 16;
            }
            break;
         case 12:
         case 13:
            this.e();
            if (super.d != null
               && (
                  super.i == 0
                     || super.z == 12 && (super.H[0] && pop2.e.ay[2] <= 0 || !super.H[0] && pop2.e.ay[3] <= 0)
                     || super.z == 13 && (!super.H[0] && pop2.e.ay[2] <= 0 || super.H[0] && pop2.e.ay[3] <= 0 || !this.C())
               )) {
               this.q = 4;
               super.d.i = 0;
            }
            break;
         case 14:
            if (super.k) {
               super.i = 0;
               this.q = 15;
               pop2.e.a(0, 1);
            }
            break;
         case 15:
            if (super.k) {
            }
            break;
         case 17:
         case 24:
            if (super.k) {
               this.q = 0;
            }
         case 18:
         default:
            break;
         case 19:
            if (super.k && super.j == 0) {
               if (this.r > 3) {
                  PrinceSOT.c.a(false);
               } else {
                  pop2.b.b(false);
                  super.e = super.n;
                  super.M = super.y;
                  this.b(this.f);
                  this.u();
                  this.J = 100;
                  this.b(1, true);
                  pop2.b.a(true);
                  this.a(23, 1);
               }
            }
            break;
         case 20:
            if (super.D >= super.h.b[super.z] - 1 && !this.A()) {
               this.y();
            }
            break;
         case 21:
            if (super.k) {
               this.q = this.K == 0 ? 16 : 22;
            }
            break;
         case 23:
            if (super.k) {
               this.q = 0;
            }
         case 0:
            if (!v) {
               if (super.o < 2) {
                  this.q = 16;
               } else {
                  label393:
                  if (pop2.e.ay[1] == 1 && super.o == 3) {
                     if (this.a(false) == 2) {
                        if (pop2.b.a(super.l >> 8, super.s >> 8, true)) {
                           break label393;
                        }

                        this.b(true);
                     } else {
                        if (this.a(true) != 2 || pop2.b.a(super.l >> 8, super.s >> 8, false)) {
                           break label393;
                        }

                        this.b(false);
                     }

                     super.e = super.s;
                     super.M = super.l;
                     this.q = 20;
                  } else if (pop2.e.ay[2] >= 1 || pop2.e.ay[3] >= 1) {
                     this.b(pop2.e.ay[2] >= 1);
                     this.a();
                  } else if (pop2.e.ay[0] == 1) {
                     if (this.B()) {
                        super.e = super.s;
                        super.M = super.l;
                        this.Q = this.q = 10;
                        this.a(10, 1);
                        this.d(true);
                        this.d(true);
                     } else if (this.p()) {
                        this.q = 2;
                     } else {
                        this.q = 3;
                     }
                  } else if (B[1] > 0 && pop2.e.ay[4] == 1) {
                     this.q = 6;
                  }
               }
            }
      }

      if (this.Q != this.q) {
         this.a(this.q, 1);
         if (this.q == 6) {
            pop2.e.a(10, 1);
         } else if (this.q == 16) {
            super.M += 256;
            super.i /= 2;
            this.c(false);
         } else if (this.q == 0) {
            this.e(true);
         } else if (this.q == 9 || this.q == 2 || this.q == 3) {
            this.c(false);
         }
      }

      if (this.q == 20 || this.q == 16) {
         I = 34;
      } else if (this.q == 9 && this.Q == 16) {
         I = -34;
      } else if (this.q != 9) {
         if (super.H[0]) {
            u = -44;
         } else {
            u = 44;
         }

         I = -34;
      }

      if (pop2.e.ay[1] >= 1) {
         I = 34;
      } else if (pop2.e.ay[2] >= 1) {
         u = -44;
      } else if (pop2.e.ay[3] >= 1) {
         u = 44;
      }

      return true;
   }

   public void c(int var1, int var2) {
      if (super.C % 2 == 1 && pop2.e.av < 0) {
         this.d(false);
      } else {
         super.c(var1, var2);
      }
   }
}
