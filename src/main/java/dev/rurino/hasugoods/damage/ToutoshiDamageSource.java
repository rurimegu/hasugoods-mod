package dev.rurino.hasugoods.damage;

import dev.rurino.hasugoods.item.CharaItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ToutoshiDamageSource extends DamageSource {

  private final Item item;

  public ToutoshiDamageSource(RegistryEntry<DamageType> type, Item oshiItem, Vec3d position) {
    super(type, position);
    this.item = oshiItem;
  }

  public ToutoshiDamageSource(RegistryEntry<DamageType> type, Item oshiItem) {
    super(type);
    this.item = oshiItem;
  }

  @Override
  public Text getDeathMessage(LivingEntity killed) {
    String string = "death.attack." + this.getType().msgId();
    LivingEntity livingEntity2 = killed.getPrimeAdversary();
    String string2 = string + ".player";
    Text toutoshiSource = item instanceof CharaItem ? ((CharaItem) item).getCharaDisplayName() : item.getName();
    return livingEntity2 != null
        ? Text.translatable(string2, killed.getDisplayName(), toutoshiSource,
            livingEntity2.getDisplayName())
        : Text.translatable(string, killed.getDisplayName(), toutoshiSource);
  }
}
