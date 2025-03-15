package dev.rurino.hasugoods.util.animation;

import dev.rurino.hasugoods.util.Easing;
import dev.rurino.hasugoods.util.MathUtils;
import net.minecraft.util.math.MathHelper;

public interface Interpolator {
  public KeyFrame interpolate(KeyFrame a, KeyFrame b, double ratio);

  default public KeyFrame interpolateTick(KeyFrame a, KeyFrame b, double tick) {
    if (MathUtils.approx(a.tick(), b.tick())) {
      return interpolate(a, b, tick < b.tick() ? 0 : 1);
    }
    double ratio = MathHelper.getLerpProgress(tick, a.tick(), b.tick());
    return interpolate(a, b, ratio);
  }

  public static Interpolator of(Easing easing) {
    return (a, b, ratio) -> {
      ratio = MathHelper.clamp(ratio, 0, 1);
      double progress = easing.ease(ratio);
      return new KeyFrame.Regular(
          MathHelper.lerp(progress, a.tick(), b.tick()),
          MathUtils.lerp(progress, a.translation(), b.translation()),
          a.rotation().nlerp(b.rotation(), (float) progress),
          MathUtils.lerp(progress, a.scale(), b.scale()));
    };
  }

  public static Interpolator LINEAR = of(Easing::linear);
  public static Interpolator CONSTANT = of(Easing::constant);
  public static Interpolator FIRST = (a, b, tick) -> a;
  public static Interpolator LAST = (a, b, tick) -> b;
}
