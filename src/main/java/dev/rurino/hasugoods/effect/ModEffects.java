package dev.rurino.hasugoods.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.MapCodec;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.CharaUtils;
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

  public static final Map<String, RegistryEntry<StatusEffect>> STATUS_EFFECTS = new HashMap<>();

  public static String oshiProtectionKey(String charaKey) {
    return "oshi_protection_" + charaKey;
  }

  public static Optional<RegistryEntry<StatusEffect>> getStatusEffect(String charaKey) {
    return Optional.ofNullable(STATUS_EFFECTS.get(charaKey));
  }

  private static RegistryEntry<StatusEffect> register(String id, StatusEffect entry) {
    Hasugoods.LOGGER.debug("Register StatusEffect: " + id);
    return STATUS_EFFECTS.put(
        id,
        Registry.registerReference(Registries.STATUS_EFFECT, Hasugoods.id(id), entry));
  }

  // #region ConsumeEffects

  private static <T extends ConsumeEffect> ConsumeEffect.Type<T> registerConsumeEffect(String id, MapCodec<T> codec,
      PacketCodec<RegistryByteBuf, T> packetCodec) {
    Hasugoods.LOGGER.debug("Register ConsumeEffect: " + id);
    return Registry.register(Registries.CONSUME_EFFECT_TYPE, id, new ConsumeEffect.Type<T>(codec, packetCodec));
  }

  public static final ConsumeEffect.Type<ToutoshiEffectsConsumeEffect> TOUTOSHI = registerConsumeEffect("toutoshi",
      ToutoshiEffectsConsumeEffect.CODEC,
      ToutoshiEffectsConsumeEffect.PACKET_CODEC);

  // #endregion ConsumeEffects

  public static void initialize() {

    for (String charaKey : CharaUtils.ALL_CHARA_KEYS) {
      StatusEffect effect = new OshiProtectionEffect(charaKey);
      register(oshiProtectionKey(charaKey), effect);
    }

    ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
      if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE))
        return true;
      if (entity.getStatusEffects().stream()
          .anyMatch(instance -> instance.getEffectType().value() instanceof OshiProtectionEffect)) {
        return false;
      }
      return true;
    });
  }

}
