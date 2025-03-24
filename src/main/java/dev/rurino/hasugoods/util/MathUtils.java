package dev.rurino.hasugoods.util;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MathUtils {
  public static final double EPS = 1e-6;
  public static final int VERY_LARGE = 0x3f3f3f3f;
  public static final ImmutableList<BlockPos> HORIZONTAL_NEIGHBORS = ImmutableList.of(
      new BlockPos(1, 0, 0),
      new BlockPos(-1, 0, 0),
      new BlockPos(0, 0, 1),
      new BlockPos(0, 0, -1));
  public static final ImmutableList<BlockPos> NEIGHBORS = ImmutableList.of(
      new BlockPos(1, 0, 0),
      new BlockPos(-1, 0, 0),
      new BlockPos(0, 0, 1),
      new BlockPos(0, 0, -1),
      new BlockPos(0, 1, 0),
      new BlockPos(0, -1, 0));

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

  public static Vec3d lerp(double progress, Vec3d a, Vec3d b) {
    return a.add(b.subtract(a).multiply(progress));
  }
}
