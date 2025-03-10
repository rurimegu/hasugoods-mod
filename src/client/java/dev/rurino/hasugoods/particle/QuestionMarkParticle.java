package dev.rurino.hasugoods.particle;

import org.joml.Vector3f;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class QuestionMarkParticle extends NoteParticle {

  public QuestionMarkParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY,
      double velocityZ, Vector3f color, SpriteProvider spriteProvider) {
    super(world, x, y, z, velocityX, velocityY, velocityZ, color, spriteProvider);
  }

}
