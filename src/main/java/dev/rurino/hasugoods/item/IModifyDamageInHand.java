package dev.rurino.hasugoods.item;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IModifyDamageInHand {
  float modifyDamage(PlayerEntity player, ItemStack stack, DamageSource source, float amount);
}
