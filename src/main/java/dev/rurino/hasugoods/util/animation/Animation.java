package dev.rurino.hasugoods.util.animation;

import java.util.ArrayList;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.util.math.Vec3d;

public class Animation {
  public enum LoopType {
    NONE,
    LOOP,
    PING_PONG
  }

  protected final ArrayList<KeyFrame> keyFrames = new ArrayList<>();
  protected final ArrayList<Interpolator> interpolators = new ArrayList<>();
  protected final LoopType loop;

  public Animation(KeyFrame initState, LoopType loop) {
    keyFrames.add(initState);
    this.loop = loop;
  }

  public void addKeyFrame(float tick, Vec3d translation, float rotationDegrees, Vec3d scale,
      Interpolator interpolator) {
    keyFrames.add(new KeyFrame(tick, translation, rotationDegrees, scale));
    interpolators.add(interpolator);
  }

  public void addKeyFrame(float tick, Vec3d translation, float rotationDegrees, Vec3d scale) {
    addKeyFrame(tick, translation, rotationDegrees, scale, Interpolator.LINEAR);
  }

  public float duration() {
    return keyFrames.get(keyFrames.size() - 1).tick();
  }

  public int size() {
    return keyFrames.size();
  }

  protected int getPrevKeyFrameIndex(float tick) {
    if (keyFrames.size() == 0)
      throw new IllegalStateException("No keyframes added");

    if (keyFrames.size() == 1)
      return 0;

    if (loop == LoopType.LOOP) {
      tick %= duration();
    } else if (loop == LoopType.PING_PONG) {
      float duration = duration();
      tick %= duration * 2;
      if (tick > duration) {
        tick = duration * 2 - tick;
      }
    } else if (tick > duration()) {
      return keyFrames.size() - 1;
    }

    for (int i = 1; i < keyFrames.size(); i++) {
      if (keyFrames.get(i).tick() >= tick) {
        return i - 1;
      }
    }

    return keyFrames.size() - 1;
  }

  public KeyFrame getKeyFrame(float tick, KeyFrame currentState) {
    int prevIdx = getPrevKeyFrameIndex(tick);
    if (prevIdx == keyFrames.size() - 1) {
      return keyFrames.get(prevIdx);
    }
    KeyFrame a = keyFrames.get(prevIdx);
    KeyFrame b = keyFrames.get(prevIdx + 1);
    Interpolator interpolator = interpolators.get(prevIdx);
    throw new NotImplementedException();
  }

  public KeyFrame getKeyFrame(float tick) {
    int prevIdx = getPrevKeyFrameIndex(tick);
    if (prevIdx == keyFrames.size() - 1) {
      return keyFrames.get(prevIdx);
    }
    KeyFrame a = keyFrames.get(prevIdx);
    KeyFrame b = keyFrames.get(prevIdx + 1);
    Interpolator interpolator = interpolators.get(prevIdx);
    return interpolator.interpolate(a, b, tick);
  }
}
