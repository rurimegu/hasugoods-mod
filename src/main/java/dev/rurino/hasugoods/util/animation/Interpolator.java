package dev.rurino.hasugoods.util.animation;

import net.minecraft.util.math.MathHelper;

public interface Interpolator {
  public KeyFrame interpolate(KeyFrame a, KeyFrame b, float tick);

  public static Interpolator LINEAR = (a, b, tick) -> {
    float progress = MathHelper.clamp(MathHelper.getLerpProgress(tick, a.tick(), b.tick()), 0, 1);
    return new KeyFrame(tick, a.translation().add(b.translation().subtract(a.translation()).multiply(progress)),
        MathHelper.lerp(progress, a.rotationDegrees(), b.rotationDegrees()),
        a.scale().add(b.scale().subtract(a.scale()).multiply(progress)));
  };
}
