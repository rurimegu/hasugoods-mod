package dev.rurino.hasugoods.util.animation;

import dev.rurino.hasugoods.util.Easing;
import net.minecraft.util.math.MathHelper;

public interface Interpolator {
  public KeyFrame interpolate(KeyFrame a, KeyFrame b, double tick);

  public static Interpolator of(Easing easing) {
    return (a, b, tick) -> {
      double progress = easing.ease(MathHelper.clamp(easing.ease(tick / b.tick()), 0, 1));
      return new KeyFrame.Regular(tick,
          a.translation().add(b.translation().subtract(a.translation()).multiply(progress)),
          a.rotation().nlerp(b.rotation(), (float) progress),
          a.scale().add(b.scale().subtract(a.scale()).multiply(progress)));
    };
  }

  public static Interpolator LINEAR = of(Easing::linear);
}
