package dev.rurino.hasugoods.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class ClientNetwork {
  private static void playAnimHandler(PlayAnimPayload payload, ClientPlayNetworking.Context context) {
    ClientWorld world = MinecraftClient.getInstance().world;
    payload.apply(world);
  }

  public static void initialize() {
    ClientPlayNetworking.registerGlobalReceiver(PlayAnimPayload.ID, ClientNetwork::playAnimHandler);
  }
}
