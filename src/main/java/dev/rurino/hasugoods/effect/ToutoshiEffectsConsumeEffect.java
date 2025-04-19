package dev.rurino.hasugoods.effect;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.IToutoshiComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.minecraft.network.RegistryByteBuf;

public record ToutoshiEffectsConsumeEffect(String charaKey) implements ConsumeEffect {
  public static final MapCodec<ToutoshiEffectsConsumeEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
    return instance
        .group(
            Codecs.NON_EMPTY_STRING.fieldOf("charaKey")
                .forGetter(ToutoshiEffectsConsumeEffect::charaKey))
        .apply(instance, ToutoshiEffectsConsumeEffect::new);
  });
  public static final PacketCodec<RegistryByteBuf, ToutoshiEffectsConsumeEffect> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.STRING,
      ToutoshiEffectsConsumeEffect::charaKey,
      ToutoshiEffectsConsumeEffect::new);

  private static final IntValue OSHI_PROTECTION_DURATION = Hasugoods.CONFIG.oshiProtection.duration;
  private static final IntValue OSHI_SECRET_PROTECTION_DURATION = Hasugoods.CONFIG.oshiProtection.secretDuration;

  @Override
  public ConsumeEffect.Type<ToutoshiEffectsConsumeEffect> getType() {
    return ModEffects.TOUTOSHI;
  }

  @Override
  public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
    Optional<IToutoshiComponent> toutoshiComponent = ModComponents.TOUTOSHI.maybeGet(user);
    if (toutoshiComponent.isEmpty()) {
      Hasugoods.LOGGER.error("Cannot find ToutoshiComponent for {}", user);
      return false;
    }
    Item item = stack.getItem();
    toutoshiComponent.get().setToutoshiSourceItem(item);
    int duration = OSHI_PROTECTION_DURATION.getAsInt();
    if (item instanceof BadgeItem badgeItem && badgeItem.isSecret()) {
      duration = OSHI_SECRET_PROTECTION_DURATION.getAsInt();
    }
    if (duration <= 0)
      return false;

    var effect = ModEffects.getStatusEffect(ModEffects.oshiProtectionKey(charaKey));
    if (effect.isEmpty()) {
      Hasugoods.LOGGER.error("Cannot find status effect for {}", charaKey);
      return false;
    }
    return user.addStatusEffect(new StatusEffectInstance(effect.get(), duration));
  }
}
