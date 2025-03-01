package dev.rurino.hasugoods.util;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Nest;

@Config(name = "hasu-config", wrapperName = "HasuConfig")
public class HasuConfigModel {
  // #region Toutoshi
  public float toutoshiDamage = 1000f;
  public int oshiProtectionDuration = 2 * 20;
  public int oshiSecretProtectionDuration = 10 * 20;
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

  // #region Trade
  public static class TradeConfig {
    public int exp = 1;
    public float prob = 0.5f;
    public float secretProb = 0.1f;
    public int maxUses = 12;
    public int secretMaxUses = 3;
  }

  public static class BuyConfig {
    public int exp = 3;
    public float prob = 0.5f;
    public float secretProb = 0.1f;
    public int maxUses = 12;
    public int secretMaxUses = 3;
    public int regularPrice = 1;
    public int secretPrice = 5;
  }

  public static class SellConfig {
    public int exp = 3;
    public float prob = 0.5f;
    public float secretProb = 0.1f;
    public int maxUses = 12;
    public int secretMaxUses = 3;
    public int regularPrice = 4;
    public int secretPrice = 30;
  }

  @Nest
  public TradeConfig trade = new TradeConfig();

  @Nest
  public BuyConfig buy = new BuyConfig();

  @Nest
  public SellConfig sell = new SellConfig();

  public int unopenedPacketPrice = 3;
  public int unopenedPacketMaxUses = 9;
  public float unopenedBoxDiscount = 0.1f;
  public int unopenedBoxMaxUses = 2;
  // b#endregion Trade
}
