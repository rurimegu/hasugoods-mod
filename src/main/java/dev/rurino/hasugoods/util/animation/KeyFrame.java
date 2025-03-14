package dev.rurino.hasugoods.util.animation;

import org.joml.Quaternionf;

import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public interface KeyFrame {
  public double tick();

  public Vec3d translation();

  public Quaternionf rotation();

  public Vec3d scale();

  public static class Regular implements KeyFrame {
    private final double tick;
    private final Vec3d translation;
    private final Quaternionf rotation;
    private final Vec3d scale;

    public Regular(double tick, Vec3d translation, Quaternionf rotation, Vec3d scale) {
      this.tick = tick;
      this.translation = translation;
      this.rotation = rotation;
      this.scale = scale;
    }

    public static Regular rotateBy(
        RotationAxis axis,
        double tick,
        Vec3d translation,
        float rotationDegrees,
        Vec3d scale) {
      return new Regular(tick, translation, axis.rotationDegrees(rotationDegrees), scale);
    }

    @Override
    public double tick() {
      return tick;
    }

    @Override
    public Vec3d translation() {
      return translation;
    }

    @Override
    public Quaternionf rotation() {
      return rotation;
    }

    @Override
    public Vec3d scale() {
      return scale;
    }
  }
}
