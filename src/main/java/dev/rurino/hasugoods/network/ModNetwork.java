package dev.rurino.hasugoods.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ModNetwork {

  public static PacketCodec<ByteBuf, Vec3d> CODEC_VECTOR3D = PacketCodec.tuple(
      PacketCodecs.DOUBLE,
      Vec3d::getX,
      PacketCodecs.DOUBLE,
      Vec3d::getY,
      PacketCodecs.DOUBLE,
      Vec3d::getZ,
      Vec3d::new);

  public static void sendPacketToPlayersTracking(ServerWorld world, BlockPos pos, CustomPayload payload) {
    PlayerLookup.tracking(world, pos).forEach(player -> ServerPlayNetworking.send(player, payload));
  }

  public static void sendPacketToPlayersTracking(Entity entity, CustomPayload payload) {
    PlayerLookup.tracking(entity).forEach(player -> ServerPlayNetworking.send(player, payload));
  }

  public static void initialize() {
    PayloadTypeRegistry.playS2C().register(PlayAnimPayload.ID, PlayAnimPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(FinishNesoMergePayload.ID, FinishNesoMergePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(EmitParticlesPayload.ID, EmitParticlesPayload.CODEC);
  }
}
