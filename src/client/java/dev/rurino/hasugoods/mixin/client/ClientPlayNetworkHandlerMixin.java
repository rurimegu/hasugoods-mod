package dev.rurino.hasugoods.mixin.client;

import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {

  protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection,
      ClientConnectionState connectionState) {
    super(client, connection, connectionState);
  }

  @Inject(method = "getActiveTotemOfUndying", at = @At("HEAD"), cancellable = true)
  private static void getActiveTotemOfUndying(PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
    for (Hand hand : Hand.values()) {
      ItemStack stack = player.getStackInHand(hand);
      Item item = stack.getItem();
      if (item instanceof BadgeItem) {
        cir.setReturnValue(stack);
        return;
      }
    }
  }
}
