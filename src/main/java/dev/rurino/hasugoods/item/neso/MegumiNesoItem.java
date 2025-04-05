package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.item.IModifyDamageInHand;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class MegumiNesoItem extends NesoItem implements IModifyDamageInHand {

  private final NesoConfig.Megumi config;

  public MegumiNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.MEGUMI_KEY, size);
    config = (NesoConfig.Megumi) super.config;
  }

  @Override
  public NesoConfig.Megumi getConfig() {
    return config;
  }

  @Override
  public float modifyDamage(PlayerEntity player, ItemStack stack, DamageSource source, float amount) {
    if (source == null || !(source.getAttacker() instanceof LivingEntity attacker)) {
      return amount;
    }
    return ModComponents.OSHI.maybeGet(attacker).map(c -> {
      float newAmount = amount;
      if (c.getOshiKey().equals(getCharaKey()) && tryUseEnergy(stack, config.damageEnergy())) {
        newAmount *= config.damageMultiplier();
        if (newAmount < 0) {
          player.heal(-newAmount);
          newAmount = 0;
        }
      }
      return newAmount;
    }).orElse(amount);
  }

}
