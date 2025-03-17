package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;

public class KahoNesoItem extends NesoItem {

  // #region Config
  private static record Config(
      long energyPerAction,
      long energyPerReplace,
      int intervalTicks,
      int radius,
      float flowerRatio,
      int cooldownTicks) {
  }

  private static final Config SMALL_CONFIG;
  private static final Config MEDIUM_CONFIG;
  private static final Config LARGE_CONFIG;

  static {
    var kahoConfig = Hasugoods.CONFIG.neso.kaho;
    SMALL_CONFIG = new Config(
        kahoConfig.small.energyPerAction(),
        kahoConfig.small.energyPerReplace(),
        kahoConfig.small.intervalTicks(),
        kahoConfig.small.radius(),
        kahoConfig.small.flowerRatio(),
        kahoConfig.small.cooldownTicks());
    MEDIUM_CONFIG = new Config(
        kahoConfig.medium.energyPerAction(),
        kahoConfig.medium.energyPerReplace(),
        kahoConfig.medium.intervalTicks(),
        kahoConfig.medium.radius(),
        kahoConfig.medium.flowerRatio(),
        kahoConfig.medium.cooldownTicks());
    LARGE_CONFIG = new Config(
        kahoConfig.large.energyPerAction(),
        kahoConfig.large.energyPerReplace(),
        kahoConfig.large.intervalTicks(),
        kahoConfig.large.radius(),
        kahoConfig.large.flowerRatio(),
        kahoConfig.large.cooldownTicks());
  }
  // #endregion

  private final Config config;

  public KahoNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.KAHO_KEY, size);
    switch (size) {
      case SMALL -> config = SMALL_CONFIG;
      case MEDIUM -> config = MEDIUM_CONFIG;
      case LARGE -> config = LARGE_CONFIG;
      default -> throw new IllegalArgumentException("Invalid size: " + size);
    }
  }

}
