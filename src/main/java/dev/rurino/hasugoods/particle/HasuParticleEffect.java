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

  public static class Builder {
    private final String name;
    private int color;
    private float initialScale;
    private float finalScale;

    public Builder(String name) {
      this.name = name;
      this.color = 0xFFFFFF;
      this.initialScale = 1.0F;
      this.finalScale = 1.0F;
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
      this.finalScale = finalScale;
      return this;
    }

    public HasuParticleEffect build() {
      return new HasuParticleEffect(name, color, initialScale, finalScale);
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
                .forGetter(HasuParticleEffect::getFinalScale))
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
      HasuParticleEffect::new);

  private final ParticleType<?> type;
  private final int color;
  private final String name;
  private final float initialScale;
  private final float finalScale;

  public HasuParticleEffect(String name, int color, float initialScale, float finalScale) {
    this.name = name;
    this.color = color;
    this.type = ModParticles.get(name);
    this.initialScale = initialScale;
    this.finalScale = finalScale;
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

  @Override
  public ParticleType<?> getType() {
    return type;
  }

}
