package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.entity.LivingEntity;

public class ModComponents implements EntityComponentInitializer {

  public static final ComponentKey<IToutoshiComponent> TOUTOSHI = ComponentRegistry.getOrCreate(
      Hasugoods.id("toutoshi"),
      IToutoshiComponent.class);

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    Hasugoods.LOGGER.info("Registering mod components");
    registry.registerFor(LivingEntity.class, TOUTOSHI, ToutoshiComponent::new);
  }
}
