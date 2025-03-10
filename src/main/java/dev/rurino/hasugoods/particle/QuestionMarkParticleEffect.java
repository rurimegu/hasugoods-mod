package dev.rurino.hasugoods.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;

public class QuestionMarkParticleEffect extends NoteParticleEffect {
  public static final MapCodec<QuestionMarkParticleEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
    return instance
        .group(Codecs.RGB.fieldOf("color")
            .forGetter(QuestionMarkParticleEffect::getColor))
        .apply(instance, QuestionMarkParticleEffect::new);
  });
  public static final PacketCodec<RegistryByteBuf, QuestionMarkParticleEffect> PACKET_CODEC;

  static {
    PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER,
        QuestionMarkParticleEffect::getColor,
        QuestionMarkParticleEffect::new);
  }

  public QuestionMarkParticleEffect(int color) {
    super(color);
  }

  @Override
  public ParticleType<?> getType() {
    return ModParticles.NOTE_PARTICLE_EFFECT;
  }

}
