package dev.rurino.hasugoods.particle;

import dev.rurino.hasugoods.Hasugoods;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
  public static final ParticleType<NoteParticleEffect> NOTE_PARTICLE_EFFECT = FabricParticleTypes.complex(
      NoteParticleEffect.CODEC,
      NoteParticleEffect.PACKET_CODEC);

  public static final ParticleType<QuestionMarkParticleEffect> QUESTION_MARK_PARTICLE_EFFECT = FabricParticleTypes
      .complex(
          QuestionMarkParticleEffect.CODEC,
          QuestionMarkParticleEffect.PACKET_CODEC);

  public static void register(String name, ParticleType<?> particleType) {
    Identifier id = Hasugoods.id(name);
    Hasugoods.LOGGER.info("Register particle: {}", id);
    Registry.register(Registries.PARTICLE_TYPE, id, particleType);
  }

  public static void initialize() {
    register("note_particle", NOTE_PARTICLE_EFFECT);
    register("question_mark_particle", QUESTION_MARK_PARTICLE_EFFECT);
  }
}
