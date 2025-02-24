package dev.rurino.hasugoods.util;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "hasu-config", wrapperName = "HasuConfig")
public class HasuConfigModel {
  // #region Toutoshi
  public float toutoshiDamage = 1000f;
  public int oshiProtectionDuration = 2 * 20;
  // #endregion Toutoshi

  // #region Common
  public int numBadgeInBox = 16;
  // #endregion Common

  // #region Drop probability
  public int chestBadgeDropMinCount = 1;
  public int chestBadgeDropMaxCount = 3;
  public int chestEmptyDropWeight = 20;
  public int chestBadgeDropWeight = 70;
  public int chestBoxDropWeight = 10;
  public int regularBadgeDropWeight = 95;
  public int secretBadgeDropWeight = 5;
  // #endregion Drop probability
}
