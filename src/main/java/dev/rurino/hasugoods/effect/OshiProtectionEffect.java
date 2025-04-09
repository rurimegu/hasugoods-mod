package dev.rurino.hasugoods.effect;

import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.IToutoshiComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.damage.ModDamageTypes;
import dev.rurino.hasugoods.damage.ToutoshiDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class OshiProtectionEffect extends StatusEffect {

  private static final DoubleValue TOUTOSHISHI_DAMAGE = Hasugoods.CONFIG.oshiProtection.toutoshiDamage;

  public OshiProtectionEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0xffd700);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return duration == 1;
  }

  @Override
  public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
    Optional<IToutoshiComponent> toutoshiComponentOptional = ModComponents.TOUTOSHI.maybeGet(entity);
    if (toutoshiComponentOptional.isEmpty()) {
      Hasugoods.LOGGER.warn("Cannot find ToutoshiComponent for {} when removing oshi protection", entity);
      return false;
    }
    IToutoshiComponent toutoshiComponent = toutoshiComponentOptional.get();
    Item item = toutoshiComponent.getToutoshiSourceItem();
    if (item == null) {
      Hasugoods.LOGGER.warn("Cannot find Toutoshi source item for {} when removing oshi protection", entity);
      return false;
    }
    toutoshiComponent.setToutoshiSourceItem(null);
    DamageSource damageSource = new ToutoshiDamageSource(
        world.getRegistryManager()
            .getOrThrow(RegistryKeys.DAMAGE_TYPE)
            .getEntry(ModDamageTypes.TOUTOSHI_DAMAGE.getValue()).get(),
        item);
    entity.damage(world, damageSource, TOUTOSHISHI_DAMAGE.get().floatValue());
    return true;
  }
}
