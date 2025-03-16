package dev.rurino.hasugoods.util.animation;

import java.util.ArrayList;

public class Animation {

  public enum LoopType {
    NONE,
    LOOP,
    PING_PONG
  }

  private record FrameData<T extends KeyFrame<?>>(T frame, Interpolator<T> interpolator) {
  }

  private final ArrayList<FrameData<KeyFrame.Translate>> translations = new ArrayList<>();
  private final ArrayList<FrameData<KeyFrame.Scale>> scales = new ArrayList<>();
  private final ArrayList<FrameData<KeyFrame.Rotate>> rotations = new ArrayList<>();

  private final LoopType loop;
  private double lastTick = 0;

  public Animation(LoopType loop) {
    this.loop = loop;
    translations.add(new FrameData<>(new KeyFrame.Translate(), null));
    scales.add(new FrameData<>(new KeyFrame.Scale(), null));
    rotations.add(new FrameData<>(new KeyFrame.Rotate(), null));
  }

  public Animation() {
    this(LoopType.NONE);
  }

  private static <T extends KeyFrame<?>> void maybePop(ArrayList<FrameData<T>> frames, double tick) {
    while (!frames.isEmpty() && last(frames).tick() >= tick)
      frames.remove(frames.size() - 1);
  }

  public Animation addTranslation(KeyFrame.Translate frame, Interpolator.Translate interpolator) {
    maybePop(translations, frame.tick());
    translations.add(new FrameData<KeyFrame.Translate>(frame, interpolator));
    lastTick = Math.max(lastTick, frame.tick());
    return this;
  }

  public Animation addTranslation(KeyFrame.Translate frame) {
    return addTranslation(frame, Interpolator.Translate.LINEAR);
  }

  public Animation addScale(KeyFrame.Scale frame, Interpolator.Scale interpolator) {
    maybePop(scales, frame.tick());
    scales.add(new FrameData<KeyFrame.Scale>(frame, interpolator));
    lastTick = Math.max(lastTick, frame.tick());
    return this;
  }

  public Animation addScale(KeyFrame.Scale frame) {
    return addScale(frame, Interpolator.Scale.LINEAR);
  }

  public Animation addRotation(KeyFrame.Rotate frame, Interpolator.Rotate interpolator) {
    maybePop(rotations, frame.tick());
    rotations.add(new FrameData<KeyFrame.Rotate>(frame, interpolator));
    lastTick = Math.max(lastTick, frame.tick());
    return this;
  }

  public Animation addRotation(KeyFrame.Rotate frame) {
    return addRotation(frame, Interpolator.Rotate.LINEAR);
  }

  public Animation finish(double finalTick) {
    this.lastTick = finalTick;
    return this;
  }

  public double duration() {
    return loop == LoopType.PING_PONG ? this.lastTick * 2 : lastTick;
  }

  private static <T extends KeyFrame<?>> T first(ArrayList<FrameData<T>> frames) {
    return frames.get(0).frame;
  }

  private static <T extends KeyFrame<?>> T last(ArrayList<FrameData<T>> frames) {
    return frames.get(frames.size() - 1).frame;
  }

  private static <T extends KeyFrame<?>> T interpolate(ArrayList<FrameData<T>> frames, double tick) {
    if (frames.size() == 0)
      throw new IllegalStateException("No keyframes added");

    if (frames.size() == 1)
      return first(frames);

    for (int i = 1; i < frames.size(); i++) {
      FrameData<T> b = frames.get(i);
      if (b.frame.tick() >= tick) {
        FrameData<T> a = frames.get(i - 1);
        Interpolator<T> interpolator = b.interpolator;
        T interpolated = interpolator.interpolateTick(a.frame, b.frame, tick);
        return interpolated;
      }
    }

    return last(frames);
  }

  public Frame getFrame(double tick) {
    if (loop == LoopType.LOOP) {
      tick %= duration();
    } else if (loop == LoopType.PING_PONG) {
      tick %= duration();
      if (tick >= lastTick)
        tick = lastTick * 2 - tick;
    }
    return new Frame(
        interpolate(translations, tick).value(),
        interpolate(scales, tick).value(),
        interpolate(rotations, tick).value());
  }
}
