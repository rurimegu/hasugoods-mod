package dev.rurino.hasugoods.util.animation;

import java.util.ArrayList;

public class Animation {
  protected static float MAX_TRANSITION_TICK = 20;

  public enum LoopType {
    NONE,
    LOOP,
    PING_PONG
  }

  protected final ArrayList<KeyFrame> keyFrames = new ArrayList<>();
  protected final ArrayList<Interpolator> interpolators = new ArrayList<>();
  protected final LoopType loop;
  protected final float maxTransition;

  public Animation(KeyFrame initState, LoopType loop, float maxTransition) {
    keyFrames.add(initState);
    this.loop = loop;
    this.maxTransition = maxTransition;
  }

  public Animation(KeyFrame initState, LoopType loop) {
    this(initState, loop, MAX_TRANSITION_TICK);
  }

  public Animation(KeyFrame initState) {
    this(initState, LoopType.NONE);
  }

  public void addKeyFrame(KeyFrame frame, Interpolator interpolator) {
    keyFrames.add(frame);
    interpolators.add(interpolator);
  }

  public void addKeyFrame(KeyFrame frame) {
    addKeyFrame(frame, Interpolator.LINEAR);
  }

  public double duration() {
    double lastTick = keyFrames.get(keyFrames.size() - 1).tick();
    return loop == LoopType.PING_PONG ? lastTick * 2 : lastTick;
  }

  public int size() {
    return keyFrames.size();
  }

  protected int getPrevKeyFrameIndex(double tick) {
    if (keyFrames.size() == 0)
      throw new IllegalStateException("No keyframes added");

    if (keyFrames.size() == 1)
      return 0;

    if (loop == LoopType.LOOP) {
      tick %= duration();
    } else if (loop == LoopType.PING_PONG) {
      double duration = duration();
      tick %= duration;
      if (tick > duration / 2) {
        tick = duration - tick;
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

  public KeyFrame getKeyFrame(double tick, KeyFrame prev) {
    int prevIdx = getPrevKeyFrameIndex(tick);
    if (prevIdx == keyFrames.size() - 1) {
      return keyFrames.get(prevIdx);
    }
    KeyFrame a = keyFrames.get(prevIdx);
    KeyFrame b = keyFrames.get(prevIdx + 1);
    Interpolator interpolator = interpolators.get(prevIdx);
    KeyFrame result = interpolator.interpolate(a, b, tick);
    double ratio = Math.min(1, tick / MAX_TRANSITION_TICK);
    return Interpolator.LINEAR.interpolate(prev, result, ratio);
  }

  public KeyFrame getKeyFrame(double tick) {
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
