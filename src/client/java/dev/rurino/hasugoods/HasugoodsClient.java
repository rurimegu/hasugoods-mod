package dev.rurino.hasugoods;

import dev.rurino.hasugoods.entity.ClientEntities;
import dev.rurino.hasugoods.particle.ClientParticles;
import net.fabricmc.api.ClientModInitializer;

public class HasugoodsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientEntities.initialize();
    ClientParticles.initialize();
  }
}
