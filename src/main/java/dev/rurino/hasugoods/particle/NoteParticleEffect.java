package dev.rurino.hasugoods.particle;

import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public class NoteParticleEffect implements ParticleEffect {
  public static final MapCodec<NoteParticleEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
    return instance
        .group(Codecs.RGB.fieldOf("color")
            .forGetter(NoteParticleEffect::getColor))
        .apply(instance, NoteParticleEffect::new);
  });
  public static final PacketCodec<RegistryByteBuf, NoteParticleEffect> PACKET_CODEC;

  static {
    PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, NoteParticleEffect::getColor, NoteParticleEffect::new);
  }

  protected final int color;

  public NoteParticleEffect(int color) {
    this.color = color;
  }

  public int getColor() {
    return this.color;
  }

  public Vector3f getColorVec() {
    return ColorHelper.toVector(color);
  }

  @Override
  public ParticleType<?> getType() {
    return ModParticles.NOTE_PARTICLE_EFFECT;
  }

}
