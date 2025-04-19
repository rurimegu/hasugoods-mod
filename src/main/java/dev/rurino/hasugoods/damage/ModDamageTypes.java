package dev.rurino.hasugoods.damage;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModDamageTypes {
  public static final RegistryKey<DamageType> TOUTOSHI_DAMAGE = register("toutoshi");

  private static RegistryKey<DamageType> register(String id) {
    Hasugoods.LOGGER.debug("Register DamageType: " + id);
    return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Hasugoods.id(id));
  }

  public static void initialize() {
  }
}
