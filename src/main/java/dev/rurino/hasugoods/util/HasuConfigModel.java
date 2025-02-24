package dev.rurino.hasugoods.util;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "hasu-config", wrapperName = "HasuConfig")
public class HasuConfigModel {
  public float toutoshiDamage = 1000f;
  public int oshiProtectionDuration = 2 * 20;
  public int numBadgeInBox = 16;
}
