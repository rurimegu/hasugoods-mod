package dev.rurino.hasugoods.damage;

import dev.rurino.hasugoods.item.OshiItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ToutoshiDamageSource extends DamageSource {

  private final OshiItem oshiItem;

  public ToutoshiDamageSource(RegistryEntry<DamageType> type, OshiItem oshiItem, Vec3d position) {
    super(type, position);
    this.oshiItem = oshiItem;
  }

  public ToutoshiDamageSource(RegistryEntry<DamageType> type, OshiItem oshiItem) {
    super(type);
    this.oshiItem = oshiItem;
  }

  @Override
  public Text getDeathMessage(LivingEntity killed) {
    String string = "death.attack." + this.getType().msgId();
    LivingEntity livingEntity2 = killed.getPrimeAdversary();
    String string2 = string + ".player";
    return livingEntity2 != null
        ? Text.translatable(string2, killed.getDisplayName(), oshiItem.getOshiDisplayName(),
            livingEntity2.getDisplayName())
        : Text.translatable(string, killed.getDisplayName(), oshiItem.getOshiDisplayName());
  }
}
