package dev.rurino.hasugoods.mixin;

import dev.rurino.hasugoods.effect.ToutoshiEffectsConsumeEffect;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

  public LivingEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
  private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
    LivingEntity self = (LivingEntity) (Object) this;
    if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      return;
    }
    ItemStack badgeStack = null;
    BadgeItem badgeItem = null;
    for (Hand hand : Hand.values()) {
      ItemStack stack = self.getStackInHand(hand);
      if (stack.getItem() instanceof BadgeItem item) {
        badgeStack = stack;
        badgeItem = item;
        break;
      }
    }
    if (badgeStack == null || badgeItem == null || badgeStack.isEmpty())
      return;
    self.setHealth(1.0F);
    self.clearStatusEffects();
    new ToutoshiEffectsConsumeEffect(badgeItem.getCharaKey())
        .onConsume(self.getWorld(), badgeStack, self);
    badgeStack.decrement(1);
    this.getWorld().sendEntityStatus(this, EntityStatuses.USE_TOTEM_OF_UNDYING);
    cir.setReturnValue(true);
  }
}
