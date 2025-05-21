package dev.rurino.hasugoods;

import dev.rurino.hasugoods.block.ClientBlocks;
import dev.rurino.hasugoods.entity.ClientEntities;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.network.ClientNetwork;
import dev.rurino.hasugoods.particle.ClientParticles;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class HasugoodsClient implements ClientModInitializer {

  public static final Identifier PREDICATE_NESO_3D = Hasugoods.id("neso_3d");
  public static final int CUSTOM_DATA_NESO_3D = 1;

  private static void registerItemPredicates() {
    for (NesoItem neso : NesoItem.getAllNesos()) {
      ModelPredicateProviderRegistry.register(neso, PREDICATE_NESO_3D,
          (itemStack, clientWorld, livingEntity, seed) -> {
            return livingEntity == null ? 0.0F : 1.0F;
          });
    }
  }

  @Override
  public void onInitializeClient() {
    registerItemPredicates();
    ClientEntities.initialize();
    ClientParticles.initialize();
    ClientBlocks.initialize();
    ClientNetwork.initialize();
  }
}
