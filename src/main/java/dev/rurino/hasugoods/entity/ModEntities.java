package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModEntities {

  public static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> registryKey, EntityType<T> type) {
    Hasugoods.LOGGER.info("Register entity type: " + registryKey.getValue());
    return Registry.register(Registries.ENTITY_TYPE, registryKey.getValue(), type);
  }

  public static void initialize() {
    NesoEntity.Initialize();
  }
}
