package dev.rurino.hasugoods.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ModNetwork {

  public static void sendPacketToNearbyPlayers(ServerWorld world, BlockPos pos, CustomPayload payload) {
    PlayerLookup.tracking(world, pos).forEach(player -> ServerPlayNetworking.send(player, payload));
  }

  public static void initialize() {
    PayloadTypeRegistry.playS2C().register(PlayAnimPayload.ID, PlayAnimPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(FinishNesoMergePayload.ID, FinishNesoMergePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(EmitParticlesPayload.ID, EmitParticlesPayload.CODEC);
  }
}
