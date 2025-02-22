package dev.rurino.hasugoods.effect;

import com.mojang.serialization.MapCodec;

import dev.rurino.hasugoods.Hasugoods;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;

public class ModEffects {

  public static final RegistryEntry<StatusEffect> OSHI_PROTECTION = Registry.registerReference(
      Registries.STATUS_EFFECT, Hasugoods.id("oshi_protection"), new OshiProtectionEffect());

  // #region ConsumeEffects

  private static <T extends ConsumeEffect> ConsumeEffect.Type<T> registerConsumeEffect(String id, MapCodec<T> codec,
      PacketCodec<RegistryByteBuf, T> packetCodec) {
    Hasugoods.LOGGER.info("Registering ConsumeEffect: " + id);
    return Registry.register(Registries.CONSUME_EFFECT_TYPE, id, new ConsumeEffect.Type<T>(codec, packetCodec));
  }

  public static final ConsumeEffect.Type<ToutoshiEffectsConsumeEffect> TOUTOSHI = registerConsumeEffect("toutoshi",
      ToutoshiEffectsConsumeEffect.CODEC,
      ToutoshiEffectsConsumeEffect.PACKET_CODEC);

  // #endregion ConsumeEffects

  public static void initialize() {

    ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
      if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE))
        return true;
      if (entity.hasStatusEffect(OSHI_PROTECTION))
        return false;
      return true;
    });
  }

}
