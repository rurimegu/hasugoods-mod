package dev.rurino.hasugoods.util.animation;

import org.joml.Quaternionf;
import net.minecraft.util.math.Vec3d;

public interface KeyFrame<T> {
  public double tick();

  public T value();

  public static abstract class Vector3d implements KeyFrame<Vec3d> {

    private final double tick;
    private final Vec3d value;

    public Vector3d(double tick, Vec3d value) {
      this.tick = tick;
      this.value = value;
    }

    public Vector3d() {
      this(0, Vec3d.ZERO);
    }

    abstract protected String name();

    @Override
    public double tick() {
      return tick;
    }

    @Override
    public Vec3d value() {
      return value;
    }

    @Override
    public String toString() {
      return String.format("KeyFrame(tick=%f, %s=%s)", tick, name(), value);
    }
  }

  public static class Translate extends Vector3d {
    public Translate(double tick, Vec3d value) {
      super(tick, value);
    }

    public Translate() {
      super();
    }

    @Override
    protected String name() {
      return "translate";
    }
  }

  public static class Scale extends Vector3d {
    private static final Vec3d DEFAULT = new Vec3d(1, 1, 1);

    public Scale(double tick, Vec3d value) {
      super(tick, value);
    }

    public Scale() {
      super(0, DEFAULT);
    }

    @Override
    protected String name() {
      return "scale";
    }
  }

  public static class Rotate implements KeyFrame<Quaternionf> {
    private static final Quaternionf DEFAULT = new Quaternionf();

    private final double tick;
    private final Quaternionf value;

    public Rotate(double tick, Quaternionf value) {
      this.tick = tick;
      this.value = value;
    }

    public Rotate() {
      this(0, DEFAULT);
    }

    @Override
    public double tick() {
      return tick;
    }

    @Override
    public Quaternionf value() {
      return value;
    }

    @Override
    public String toString() {
      return String.format("KeyFrame(tick=%f, rotate=%s)", tick, value);
    }
  }
}
