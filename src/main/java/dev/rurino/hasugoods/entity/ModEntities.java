package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.OshiUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModEntities {
  public static final RegistryKey<EntityType<?>> KAHO_NESO_KEY = RegistryKey.of(
      Registries.ENTITY_TYPE.getKey(),
      Hasugoods.id(OshiUtils.KAHO_KEY + "_neso"));
  public static final EntityType<NesoEntity> KAHO_NESO = Registry.register(
      Registries.ENTITY_TYPE,
      KAHO_NESO_KEY.getValue(),
      EntityType.Builder.create(NesoEntity::new, SpawnGroup.MISC).dimensions(0.75f, 0.75f).dropsNothing()
          .build(KAHO_NESO_KEY));

  public static void initialize() {
  }
}
