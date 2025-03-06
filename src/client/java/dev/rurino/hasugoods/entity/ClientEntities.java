package dev.rurino.hasugoods.entity;

import java.util.Optional;

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.OshiUtils;
import dev.rurino.hasugoods.util.OshiUtils.NesoSize;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;

public class ClientEntities {

  public static void initialize() {
    SpecialModelLoaderEvents.LOAD_SCOPE.register(() -> {
      return (resourceManager, location) -> Hasugoods.MOD_ID.equals(location.getNamespace());
    });

    for (String oshiKey : OshiUtils.ALL_OSHI_KEYS) {
      for (NesoSize size : NesoSize.values()) {
        Optional<EntityType<NesoEntity>> entityType = NesoEntity.getNesoEntityType(oshiKey, size);
        if (entityType.isEmpty()) {
          Hasugoods.LOGGER.warn("Failed to get NesoEntity for {} {}", oshiKey, size);
          continue;
        }
        Optional<NesoItem> item = NesoItem.getNesoItem(oshiKey, size);
        if (item.isEmpty()) {
          Hasugoods.LOGGER.warn("Failed to get NesoItem for {} {}", oshiKey, size);
          continue;
        }
        Hasugoods.LOGGER.info("Register entity renderer for {} {}", oshiKey, size);
        EntityRendererRegistry.register(entityType.get(), (context) -> {
          return new NesoEntityRenderer(context, new NesoEntityModel(item.get()));
        });
      }
    }
  }
}
