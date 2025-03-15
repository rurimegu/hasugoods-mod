package dev.rurino.hasugoods.network;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.block.PositionZeroBlockEntity;
import dev.rurino.hasugoods.network.BlockPosPayload.PlayNesoMergeAnim;
import dev.rurino.hasugoods.network.BlockPosPayload.StopNesoMergeAnim;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class ClientNetwork {
  public static void initialize() {
    ClientPlayNetworking.registerGlobalReceiver(PlayNesoMergeAnim.ID, (payload, context) -> {
      context.client().execute(() -> {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (!(world.getBlockEntity(payload.blockPos()) instanceof PositionZeroBlockEntity blockEntity)) {
          Hasugoods.LOGGER.warn("Cannot play merge animation: BlockEntity is not PositionZeroBlockEntity");
          return;
        }
        blockEntity.playMergeAnimation();
      });
    });
    ClientPlayNetworking.registerGlobalReceiver(StopNesoMergeAnim.ID, (payload, context) -> {
      context.client().execute(() -> {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (!(world.getBlockEntity(payload.blockPos()) instanceof PositionZeroBlockEntity blockEntity)) {
          Hasugoods.LOGGER.warn("Cannot stop merge animation: BlockEntity is not PositionZeroBlockEntity");
          return;
        }
        blockEntity.stopMergeAnimation();
      });
    });
  }
}
