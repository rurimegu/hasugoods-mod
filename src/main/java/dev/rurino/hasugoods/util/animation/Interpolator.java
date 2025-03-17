package dev.rurino.hasugoods.util.animation;

import dev.rurino.hasugoods.util.Easing;
import dev.rurino.hasugoods.util.MathUtils;
import net.minecraft.util.math.MathHelper;

public abstract class Interpolator<T extends KeyFrame<?>> {
  abstract protected T interpolate(T a, T b, double ratio);

  private final Easing easing;

  public Interpolator(Easing easing) {
    this.easing = easing;
  }

  public Interpolator() {
    this(Easing::linear);
  }

  public T interpolateRatio(T a, T b, double ratio) {
    return interpolate(a, b, this.easing.ease(MathHelper.clamp(ratio, 0, 1)));
  }

  public T interpolateTick(T a, T b, double tick) {
    if (MathUtils.approx(a.tick(), b.tick())) {
      return interpolate(a, b, tick < b.tick() ? 0 : 1);
    }
    double ratio = MathHelper.getLerpProgress(tick, a.tick(), b.tick());
    return interpolateRatio(a, b, ratio);
  }

  public static class Translate extends Interpolator<KeyFrame.Translate> {
    public Translate(Easing easing) {
      super(easing);
    }

    public Translate() {
      super();
    }

    @Override
    protected KeyFrame.Translate interpolate(KeyFrame.Translate a, KeyFrame.Translate b, double ratio) {
      return new KeyFrame.Translate(
          MathHelper.lerp(ratio, a.tick(), b.tick()),
          MathUtils.lerp(ratio, a.value(), b.value()));
    }

    public static Translate LINEAR = new Translate(Easing::linear);
    public static Translate CONSTANT = new Translate(Easing::constant);
    public static Translate EASE_OUT_CUBIC = new Translate(Easing::easeOutCubic);
  }

  public static class Scale extends Interpolator<KeyFrame.Scale> {

    public Scale(Easing easing) {
      super(easing);
    }

    public Scale() {
      super();
    }

    @Override
    protected KeyFrame.Scale interpolate(KeyFrame.Scale a, KeyFrame.Scale b, double ratio) {
      return new KeyFrame.Scale(
          MathHelper.lerp(ratio, a.tick(), b.tick()),
          MathUtils.lerp(ratio, a.value(), b.value()));
    }

    public static Scale LINEAR = new Scale(Easing::linear);
    public static Scale CONSTANT = new Scale(Easing::constant);
    public static Scale OUT_BOUNCE = new Scale(Easing::easeOutBounce);
  }

  public static class Rotate extends Interpolator<KeyFrame.Rotate> {

    public Rotate(Easing easing) {
      super(easing);
    }

    public Rotate() {
      super();
    }

    @Override
    protected KeyFrame.Rotate interpolate(KeyFrame.Rotate a, KeyFrame.Rotate b, double ratio) {
      return new KeyFrame.Rotate(
          MathHelper.lerp(ratio, a.tick(), b.tick()),
          a.value().nlerp(b.value(), (float) ratio));
    }

    public static Rotate LINEAR = new Rotate(Easing::linear);
    public static Rotate CONSTANT = new Rotate(Easing::constant);
  }

}
