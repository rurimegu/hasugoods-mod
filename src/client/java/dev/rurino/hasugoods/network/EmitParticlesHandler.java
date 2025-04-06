package dev.rurino.hasugoods.network;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.ParticleUtils.Emitter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class EmitParticlesHandler {
  private static void handleRandomUp(EmitParticlesPayload payload, ClientPlayNetworking.Context context) {
    var effect = List.of(payload.getEffect());
    var emitter = new Emitter.RandomUp(effect, 1f);
    ClientWorld world = context.client().world;
    Vec3d pos = payload.getPos();
    int count = payload.getCount();
    for (int i = 0; i < count; i++) {
      emitter.emit(world, pos);
    }
  }

  private static void handleRurino(EmitParticlesPayload payload, ClientPlayNetworking.Context context) {
    var effect = payload.getEffect();
    ClientWorld world = context.client().world;
    Vec3d pos = payload.getPos();
    world.addParticle(effect, pos.x, pos.y, pos.z, 0, 1, 0);
  }

  public static void handle(EmitParticlesPayload payload, ClientPlayNetworking.Context context) {
    switch (payload.getType()) {
      case EmitParticlesPayload.TYPE_NONE:
        Hasugoods.LOGGER.warn("Unexpected particle type: TYPE_NONE, skipped");
        break;
      case EmitParticlesPayload.TYPE_RANDOM_UP:
        handleRandomUp(payload, context);
        break;
      case EmitParticlesPayload.TYPE_RURINO_CHARGE:
        handleRurino(payload, context);
        break;
      default:
        break;
    }
  }
}
