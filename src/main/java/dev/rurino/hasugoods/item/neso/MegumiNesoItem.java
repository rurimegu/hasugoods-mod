package dev.rurino.hasugoods.item.neso;

import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.IOshiComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.item.IDisplayIconInHand;
import dev.rurino.hasugoods.item.IModifyDamageInHand;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.IWithChara;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class MegumiNesoItem extends NesoItem implements IModifyDamageInHand, IDisplayIconInHand {

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

  @Override
  public Optional<Identifier> getEntityDisplayIconInHand(LivingEntity entity) {
    if (entity instanceof IWithChara || entity instanceof PlayerEntity) {
      return Optional.empty();
    }
    Optional<IOshiComponent> oshiComponent = ModComponents.OSHI.maybeGet(entity);
    return oshiComponent.map(c -> Hasugoods.id("textures/particle/" + c.getOshiKey() + "_icon.png"));
  }
}
