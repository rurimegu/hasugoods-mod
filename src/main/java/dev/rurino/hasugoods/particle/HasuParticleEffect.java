package dev.rurino.hasugoods.particle;

import java.util.Optional;

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

  public static class Builder {
    private final String name;
    private int color = 0xFFFFFF;
    private float initialScale = 1.0F;
    private Optional<Float> finalScale = Optional.empty();
    private int maxAge = 40; // Default maxAge

    public Builder(String name) {
      this.name = name;
    }

    public static Builder note() {
      return new Builder(ModParticles.NAME_NOTE_PARTICLE);
    }

    public static Builder questionMark() {
      return new Builder(ModParticles.NAME_QUESTION_MARK_PARTICLE);
    }

    public static Builder charaIcon(String charaKey) {
      return new Builder(ModParticles.getCharaIconName(charaKey));
    }

    public Builder color(int color) {
      this.color = color;
      return this;
    }

    public Builder initialScale(float initialScale) {
      this.initialScale = initialScale;
      return this;
    }

    public Builder finalScale(float finalScale) {
      this.finalScale = Optional.of(finalScale);
      return this;
    }

    public Builder maxAge(int maxAge) {
      this.maxAge = maxAge;
      return this;
    }

    public HasuParticleEffect build() {
      float finalScale = this.finalScale.orElse(this.initialScale);
      return new HasuParticleEffect(name, color, initialScale, finalScale, maxAge);
    }
  }

  public static final MapCodec<HasuParticleEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
    return instance
        .group(
            Codecs.NON_EMPTY_STRING.fieldOf("name")
                .forGetter(HasuParticleEffect::getName),
            Codecs.RGB.fieldOf("color")
                .forGetter(HasuParticleEffect::getColor),
            Codecs.NON_NEGATIVE_FLOAT.fieldOf("initialScale")
                .forGetter(HasuParticleEffect::getInitialScale),
            Codecs.NON_NEGATIVE_FLOAT.fieldOf("finalScale")
                .forGetter(HasuParticleEffect::getFinalScale),
            Codecs.NON_NEGATIVE_INT.fieldOf("maxAge")
                .forGetter(HasuParticleEffect::getMaxAge))
        .apply(instance, HasuParticleEffect::new);
  });
  public static final PacketCodec<RegistryByteBuf, HasuParticleEffect> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.STRING,
      HasuParticleEffect::getName,
      PacketCodecs.INTEGER,
      HasuParticleEffect::getColor,
      PacketCodecs.FLOAT,
      HasuParticleEffect::getInitialScale,
      PacketCodecs.FLOAT,
      HasuParticleEffect::getFinalScale,
      PacketCodecs.INTEGER,
      HasuParticleEffect::getMaxAge,
      HasuParticleEffect::new);

  private final ParticleType<?> type;
  private final int color;
  private final String name;
  private final float initialScale;
  private final float finalScale;
  private final int maxAge;

  public HasuParticleEffect(String name, int color, float initialScale, float finalScale, int maxAge) {
    this.name = name;
    this.color = color;
    this.type = ModParticles.get(name);
    this.initialScale = initialScale;
    this.finalScale = finalScale;
    this.maxAge = maxAge;
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

  public float getInitialScale() {
    return this.initialScale;
  }

  public float getFinalScale() {
    return this.finalScale;
  }

  public int getMaxAge() {
    return this.maxAge;
  }

  @Override
  public ParticleType<?> getType() {
    return type;
  }

}
