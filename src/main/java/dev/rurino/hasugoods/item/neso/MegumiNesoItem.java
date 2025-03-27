package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;

public class MegumiNesoItem extends NesoItem {

  private final NesoConfig.Megumi config;

  public MegumiNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.MEGUMI_KEY, size);
    config = (NesoConfig.Megumi) super.config;
  }

  @Override
  public NesoConfig.Megumi getConfig() {
    return config;
  }

}
