package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.entity.NesoEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ModComponents implements EntityComponentInitializer {

  public static final ComponentKey<IToutoshiComponent> TOUTOSHI = ComponentRegistry.getOrCreate(
      Hasugoods.id("toutoshi"),
      IToutoshiComponent.class);

  public static final ComponentKey<IOshiComponent> OSHI = ComponentRegistry.getOrCreate(
      Hasugoods.id("oshi"),
      IOshiComponent.class);

  public static final ComponentKey<INesoComponent> NESO = ComponentRegistry.getOrCreate(
      Hasugoods.id("neso"),
      INesoComponent.class);

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    Hasugoods.LOGGER.info("Register mod components");
    registry.registerFor(PlayerEntity.class, TOUTOSHI, ToutoshiComponent::new);
    registry.registerFor(LivingEntity.class, OSHI, OshiComponent::new);
    registry.registerFor(NesoEntity.class, NESO, NesoComponent::new);
  }
}
