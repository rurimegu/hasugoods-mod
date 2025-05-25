package dev.rurino.hasugoods;

import dev.rurino.hasugoods.block.ClientBlocks;
import dev.rurino.hasugoods.entity.ClientEntities;
import dev.rurino.hasugoods.item.NesoItemRenderer;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.network.ClientNetwork;
import dev.rurino.hasugoods.particle.ClientParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.util.Identifier;

public class HasugoodsClient implements ClientModInitializer {

  public static final Identifier PREDICATE_NESO_3D = Hasugoods.id("neso_3d");
  public static final int CUSTOM_DATA_NESO_3D = 1;

  private static void registerItemRenderer() {
    ModelLoadingPlugin.register(
        (context) -> {
          for (NesoItem neso : NesoItem.getAllNesos()) {
            context.addModels(NesoItemRenderer.guiModelId(neso),
                NesoItemRenderer.inHandModelId(neso));
          }
        });
    for (NesoItem neso : NesoItem.getAllNesos()) {
      BuiltinItemRendererRegistry.INSTANCE.register(neso, new NesoItemRenderer(neso));
    }
  }

  @Override
  public void onInitializeClient() {
    registerItemRenderer();
    ClientEntities.initialize();
    ClientParticles.initialize();
    ClientBlocks.initialize();
    ClientNetwork.initialize();
  }
}
