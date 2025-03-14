package dev.rurino.hasugoods.util;

import net.minecraft.util.math.Vec3d;

public class MathUtils {
  public static double EPS = 1e-6;

  public static boolean approx(double a, double b, double epsilon) {
    return approx0(b - a, epsilon);
  }

  public static boolean approx(double a, double b) {
    return approx(a, b, EPS);
  }

  public static boolean approx0(double a, double epsilon) {
    return Math.abs(a) < epsilon;
  }

  public static boolean approx0(double a) {
    return approx0(a, EPS);
  }

  public static Vec3d lerp(Vec3d a, Vec3d b, double progress) {
    return a.add(b.subtract(a).multiply(progress));
  }
}
