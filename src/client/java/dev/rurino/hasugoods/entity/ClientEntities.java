package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.OshiUtils;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class ClientEntities {

  public static final EntityModelLayer MODEL_KAHO_NESO_LAYER = new EntityModelLayer(
      Hasugoods.id(OshiUtils.KAHO_KEY + "_neso"),
      "main");

  public static void initialize() {
    EntityRendererRegistry.register(ModEntities.KAHO_NESO, (context) -> {
      return new NesoEntityRenderer(context);
    });

    EntityModelLayerRegistry.registerModelLayer(MODEL_KAHO_NESO_LAYER, NesoEntityModel::getTexturedModelData);
  }
}
