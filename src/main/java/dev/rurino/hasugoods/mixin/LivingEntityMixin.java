package dev.rurino.hasugoods.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	// @Inject(at = @At("HEAD"), method =
	// "hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry)Z", cancellable
	// = true)
	// private void hasStatusEffect(CallbackInfo info) {
	// LivingEntity entity = (LivingEntity) (Object) this;
	// }
}
