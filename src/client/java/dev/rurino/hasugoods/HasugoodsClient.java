package dev.rurino.hasugoods;

import dev.rurino.hasugoods.entity.ClientEntities;
import net.fabricmc.api.ClientModInitializer;

public class HasugoodsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientEntities.initialize();
  }
}
