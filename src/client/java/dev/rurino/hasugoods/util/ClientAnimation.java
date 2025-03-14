package dev.rurino.hasugoods.util;

import dev.rurino.hasugoods.util.animation.KeyFrame;
import net.minecraft.client.util.math.MatrixStack;

public class ClientAnimation {
  public static void apply(KeyFrame frame, MatrixStack matrices) {
    matrices.translate(frame.translation().x, frame.translation().y, frame.translation().z);
    matrices.multiply(frame.rotation());
    matrices.scale(
        (float) frame.scale().x,
        (float) frame.scale().y,
        (float) frame.scale().z);
  }
}
