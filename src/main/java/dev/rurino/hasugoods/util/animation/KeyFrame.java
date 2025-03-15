package dev.rurino.hasugoods.util.animation;

import org.joml.Quaternionf;
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

    public static class Builder {
      private double tick = 0;
      private Vec3d translation = Vec3d.ZERO;
      private Quaternionf rotation = new Quaternionf();
      private Vec3d scale = new Vec3d(1, 1, 1);

      public Builder tick(double tick) {
        this.tick = tick;
        return this;
      }

      public Builder translation(Vec3d translation) {
        this.translation = translation;
        return this;
      }

      public Builder rotation(Quaternionf rotation) {
        this.rotation = rotation;
        return this;
      }

      public Builder scale(Vec3d scale) {
        this.scale = scale;
        return this;
      }

      public Builder scale(double scale) {
        return scale(new Vec3d(scale, scale, scale));
      }

      public Regular build() {
        return new Regular(tick, translation, rotation, scale);
      }
    }

    public Regular(double tick, Vec3d translation, Quaternionf rotation, Vec3d scale) {
      this.tick = tick;
      this.translation = translation;
      this.rotation = rotation;
      this.scale = scale;
    }

    public Builder toBuilder() {
      return new Builder().tick(tick).translation(translation).rotation(rotation).scale(scale);
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

    @Override
    public String toString() {
      return String.format("KeyFrame(tick=%f, translation=%s, rotation=%s, scale=%s)", tick, translation, rotation,
          scale);
    }
  }
}
