package dev.rurino.hasugoods.util.animation;

import org.joml.Quaternionf;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record Frame(Vec3d translate, Vec3d scale, Quaternionf rotate) {

  public Frame interpolate(Frame other, double ratio) {
    ratio = MathHelper.clamp(ratio, 0, 1);
    return new Frame(
        translate.lerp(other.translate, ratio),
        scale.lerp(other.scale, ratio),
        rotate.slerp(other.rotate, (float) ratio));
  }
}
