package dev.rurino.hasugoods.network;

import dev.rurino.hasugoods.block.AbstractNesoBaseBlockEntity;
import dev.rurino.hasugoods.block.PositionZeroBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;

public class ClientNetwork {
  private static void playAnimHandler(AbstractPlayAnimPayload payload, ClientPlayNetworking.Context context) {
    ClientWorld world = context.client().world;
    payload.apply(world);
  }

  private static void finishNesoMergeHandler(FinishNesoMergePayload payload, ClientPlayNetworking.Context context) {
    ClientWorld world = context.client().world;
    playAnimHandler(payload, context);
    for (var anim : payload.anims()) {
      if (!(world.getBlockEntity(anim.blockPos()) instanceof AbstractNesoBaseBlockEntity nesoBaseBlockEntity)) {
        continue;
      }
      nesoBaseBlockEntity.unlockItemStack();
      if (payload.isSuccess()) {
        if (nesoBaseBlockEntity instanceof PositionZeroBlockEntity positionZeroBlockEntity) {
          positionZeroBlockEntity.setItemStack(positionZeroBlockEntity.getUpgradedNeso());
        } else {
          nesoBaseBlockEntity.setItemStack(ItemStack.EMPTY);
        }
      }
    }
  }

  public static void initialize() {
    ClientPlayNetworking.registerGlobalReceiver(PlayAnimPayload.ID, ClientNetwork::playAnimHandler);
    ClientPlayNetworking.registerGlobalReceiver(FinishNesoMergePayload.ID, ClientNetwork::finishNesoMergeHandler);
    ClientPlayNetworking.registerGlobalReceiver(EmitParticlesPayload.ID, EmitParticlesHandler::handle);
  }
}
