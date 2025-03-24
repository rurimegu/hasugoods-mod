package dev.rurino.hasugoods.network;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.particle.HasuParticleEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public class EmitParticlesPayload implements CustomPayload {
  public static final int TYPE_NONE = 0;
  public static final int TYPE_RANDOM_UP = 1;

  public static final CustomPayload.Id<EmitParticlesPayload> ID = new CustomPayload.Id<>(
      Hasugoods.id("emit_particles"));
  public static final PacketCodec<RegistryByteBuf, EmitParticlesPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.INTEGER,
      EmitParticlesPayload::getType,
      HasuParticleEffect.PACKET_CODEC,
      EmitParticlesPayload::getEffect,
      Vec3d.PACKET_CODEC,
      EmitParticlesPayload::getPos,
      PacketCodecs.INTEGER,
      EmitParticlesPayload::getCount,
      EmitParticlesPayload::new);

  private final int type;
  private final HasuParticleEffect effect;
  private final Vec3d pos;
  private final int count;

  public EmitParticlesPayload(int type, HasuParticleEffect effect, Vec3d pos, int count) {
    this.type = type;
    this.effect = effect;
    this.pos = pos;
    this.count = count;
  }

  public int getType() {
    return type;
  }

  public HasuParticleEffect getEffect() {
    return effect;
  }

  public Vec3d getPos() {
    return pos;
  }

  public int getCount() {
    return count;
  }

  @Override
  public Id<EmitParticlesPayload> getId() {
    return ID;
  }
}
