package dev.rurino.hasugoods.util.animation;

import dev.rurino.hasugoods.util.Easing;
import dev.rurino.hasugoods.util.MathUtils;
import net.minecraft.util.math.MathHelper;

public interface Interpolator {
  public KeyFrame interpolate(KeyFrame a, KeyFrame b, double tick);

  public static Interpolator of(Easing easing) {
    return (a, b, tick) -> {
      if (MathUtils.approx(a.tick(), b.tick())) {
        return tick < b.tick() ? a : b;
      }
      double progress = easing.ease(
          MathHelper.clamp(
              MathHelper.getLerpProgress(tick, a.tick(), b.tick()), 0, 1));
      return new KeyFrame.Regular(tick,
          MathUtils.lerp(a.translation(), b.translation(), progress),
          a.rotation().nlerp(b.rotation(), (float) progress),
          MathUtils.lerp(a.scale(), b.scale(), progress));
    };
  }

  public static Interpolator LINEAR = of(Easing::linear);
  public static Interpolator CONSTANT = of(Easing::constant);
  public static Interpolator FIRST = (a, b, tick) -> a;
  public static Interpolator LAST = (a, b, tick) -> b;
}
