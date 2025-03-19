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
  // #endregion Trade

  // #region Nesos
  public static class NesoSmall {
    public long maxEnergy = 128 * 1024;
  }

  public static class NesoMedium {
    public long maxEnergy = 1024 * 1024;
  }

  public static class NesoLarge {
    public long maxEnergy = 16 * 1024 * 1024;
  }

  public static class KahoNesoSmall {
    public long energyPerAction = 500;
    public long energyPerReplace = -1;
    public int intervalTicks = 10;
    public int radius = 5;
    public float flowerRatio = 0.2f;
    public float useCooldown = 5;
  }

  public static class KahoNesoMedium {
    public long energyPerAction = 800;
    public long energyPerReplace = -1;
    public int intervalTicks = 5;
    public int radius = 8;
    public float flowerRatio = 0.3f;
    public float useCooldown = 5;
  }

  public static class KahoNesoLarge {
    public long energyPerAction = 1000;
    public long energyPerReplace = 5000;
    public int intervalTicks = 3;
    public int radius = 12;
    public float flowerRatio = 0.5f;
    public float useCooldown = 10;
  }

  public static class KahoNeso {
    @Nest
    public KahoNesoSmall small = new KahoNesoSmall();
    @Nest
    public KahoNesoMedium medium = new KahoNesoMedium();
    @Nest
    public KahoNesoLarge large = new KahoNesoLarge();
  }

  public static class Neso {
    @Nest
    public NesoSmall small = new NesoSmall();
    @Nest
    public NesoMedium medium = new NesoMedium();
    @Nest
    public NesoLarge large = new NesoLarge();
    @Nest
    public KahoNeso kaho = new KahoNeso();

    public long pos0ChargeAmountPerTick = 16;
  }

  @Nest
  public Neso neso = new Neso();
  // #endregion Nesos
}
