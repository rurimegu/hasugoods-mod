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

public class HasuParticleEffect implements ParticleEffect {
  public static final MapCodec<HasuParticleEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
    return instance
        .group(
            Codecs.NON_EMPTY_STRING.fieldOf("name")
                .forGetter(HasuParticleEffect::getName),
            Codecs.RGB.fieldOf("color")
                .forGetter(HasuParticleEffect::getColor))
        .apply(instance, HasuParticleEffect::new);
  });
  public static final PacketCodec<RegistryByteBuf, HasuParticleEffect> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.STRING,
      HasuParticleEffect::getName,
      PacketCodecs.INTEGER,
      HasuParticleEffect::getColor,
      HasuParticleEffect::new);

  public static HasuParticleEffect note(int color) {
    return new HasuParticleEffect(ModParticles.NAME_NOTE_PARTICLE, color);
  }

  public static HasuParticleEffect questionMark(int color) {
    return new HasuParticleEffect(ModParticles.NAME_QUESTION_MARK_PARTICLE, color);
  }

  public static HasuParticleEffect charaIcon(String charaKey) {
    return new HasuParticleEffect(
        ModParticles.getCharaIconName(charaKey), 0xFFFFFF);
  }

  private final int color;
  private final String name;
  private final ParticleType<?> type;

  public HasuParticleEffect(String name, int color) {
    this.name = name;
    this.color = color;
    this.type = ModParticles.get(name);
  }

  public int getColor() {
    return this.color;
  }

  public String getName() {
    return this.name;
  }

  public Vector3f getColorVec() {
    return ColorHelper.toVector(color);
  }

  @Override
  public ParticleType<?> getType() {
    return type;
  }

}
