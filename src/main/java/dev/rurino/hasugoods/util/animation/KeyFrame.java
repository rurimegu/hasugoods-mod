package dev.rurino.hasugoods.util.animation;

import net.minecraft.util.math.Vec3d;

public record KeyFrame(float tick, Vec3d translation, float rotationDegrees, Vec3d scale) {
}
