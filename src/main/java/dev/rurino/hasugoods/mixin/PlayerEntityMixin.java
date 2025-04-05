package dev.rurino.hasugoods.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.rurino.hasugoods.item.IModifyDamageInHand;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

  public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @ModifyVariable(method = "applyDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At("HEAD"), ordinal = 0)
  private float modifyDamageAmount(float amount, @Local(argsOnly = true) DamageSource source) {
    for (ItemStack stack : this.getHandItems()) {
      if (stack.isEmpty() || !(stack.getItem() instanceof IModifyDamageInHand item)) {
        continue;
      }
      float newAmount = item.modifyDamage((PlayerEntity) (Object) this, stack, source, amount);
      if (newAmount != amount) {
        return newAmount;
      }
    }
    return amount;
  }
}
