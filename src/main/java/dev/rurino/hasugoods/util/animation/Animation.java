package dev.rurino.hasugoods.util.animation;

import java.util.ArrayList;

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

  public Animation(KeyFrame initState) {
    this(initState, LoopType.NONE);
  }

  public Animation addKeyFrame(KeyFrame frame, Interpolator interpolator) {
    keyFrames.add(frame);
    interpolators.add(interpolator);
    return this;
  }

  public Animation addKeyFrame(KeyFrame frame) {
    return addKeyFrame(frame, Interpolator.LINEAR);
  }

  public double duration() {
    double lastTick = last().tick();
    return loop == LoopType.PING_PONG ? lastTick * 2 : lastTick;
  }

  public int size() {
    return keyFrames.size();
  }

  public KeyFrame at(int index) {
    return keyFrames.get(index);
  }

  public KeyFrame first() {
    return keyFrames.get(0);
  }

  public KeyFrame last() {
    return keyFrames.get(keyFrames.size() - 1);
  }

  protected KeyFrame interpolate(double tick) {
    if (keyFrames.size() == 0)
      throw new IllegalStateException("No keyframes added");

    if (keyFrames.size() == 1) {
      return first();
    }

    if (loop == LoopType.LOOP) {
      tick %= duration();
    } else if (loop == LoopType.PING_PONG) {
      double duration = duration();
      tick %= duration;
      if (tick > duration / 2) {
        tick = duration - tick;
      }
    } else if (tick > duration()) {
      return last();
    }

    for (int i = 1; i < keyFrames.size(); i++) {
      KeyFrame b = keyFrames.get(i);
      if (b.tick() >= tick) {
        KeyFrame a = keyFrames.get(i - 1);
        Interpolator interpolator = interpolators.get(i - 1);
        KeyFrame interpolated = interpolator.interpolateTick(a, b, tick);
        return interpolated;
      }
    }

    return keyFrames.get(keyFrames.size() - 1);
  }

  public KeyFrame getKeyFrame(double tick) {
    return interpolate(tick);
  }
}
