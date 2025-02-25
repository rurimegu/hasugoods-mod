package dev.rurino.hasugoods.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rurino.hasugoods.Hasugoods;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	// @Inject(at = @At("HEAD"), method =
	// "hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry)Z")
	// private void hasStatusEffect(CallbackInfo info) {
	// Hasugoods.LOGGER.info("hasStatusEffect mixin");
	// }
}
