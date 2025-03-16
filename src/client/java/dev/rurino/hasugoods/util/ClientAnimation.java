package dev.rurino.hasugoods.util;

import dev.rurino.hasugoods.util.animation.Frame;
import net.minecraft.client.util.math.MatrixStack;

public class ClientAnimation {
  public static void apply(Frame frame, MatrixStack matrices) {
    matrices.translate(frame.translate().x, frame.translate().y, frame.translate().z);
    matrices.multiply(frame.rotate());
    matrices.scale(
        (float) frame.scale().x,
        (float) frame.scale().y,
        (float) frame.scale().z);
  }
}
