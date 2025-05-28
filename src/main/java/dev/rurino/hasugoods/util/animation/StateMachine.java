package dev.rurino.hasugoods.util.animation;

import java.util.HashMap;
import java.util.Map;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.ICopyable;

public class StateMachine implements ICopyable<StateMachine> {

  protected static class InnerState {
    public Frame frame;
    public int state;
    public int tick;

    public InnerState(Frame frame, int state, int tick) {
      this.frame = frame;
      this.state = state;
      this.tick = tick;
    }

    public InnerState(InnerState other) {
      this(other.frame, other.state, other.tick);
    }

    public InnerState clone() {
      return new InnerState(this);
    }
  }

  private InnerState curState;
  private int transitTick;
  private Frame prevFrame;
  private final Map<Integer, Animation> states = new HashMap<>();
  private final int initialState;

  public StateMachine(int initialState) {
    this.initialState = initialState;
    this.curState = new InnerState(null, initialState, 0);
  }

  public Frame get() {
    if (curState.frame == null)
      return getFrame(0);

    return curState.frame;
  }

  public Frame getFrame(double tickDelta) {
    double tick = curState.tick + tickDelta;
    Animation currentAnimation = getCurrentAnimation();
    if (currentAnimation == null) {
      throw new IllegalStateException("No animation for state " + curState.state);
    }
    curState.frame = currentAnimation.getFrame(tick);

    if (prevFrame != null && transitTick > 0) {
      double progress = tick / transitTick;
      if (progress < 1) {
        curState.frame = prevFrame.interpolate(curState.frame, progress);
      }
    }
    return curState.frame;
  }

  public void set(int state, Animation animation) {
    states.put(state, animation);
  }

  public int getState() {
    return curState.state;
  }

  public void transit(int state, int tick) {
    if (!states.containsKey(state)) {
      Hasugoods.LOGGER.warn("No animation found for state {}, skipped" + state);
      return;
    }
    if (state == curState.state) {
      return;
    }
    curState.tick = 0;
    curState.state = state;
    transitTick = tick;
    prevFrame = curState.frame;
  }

  public Animation getAnimation(int state) {
    return states.get(state);
  }

  public Animation getCurrentAnimation() {
    return states.get(curState.state);
  }

  public int getTick() {
    return curState.tick;
  }

  public void tick() {
    curState.tick++;
    Animation animation = getCurrentAnimation();
    if (animation == null)
      return;
    if (animation.isFinished(curState.tick) && animation.exitState() >= 0) {
      transit(animation.exitState(), 0);
    }
  }

  @Override
  public StateMachine copy() {
    StateMachine copy = new StateMachine(initialState);
    for (Map.Entry<Integer, Animation> entry : states.entrySet()) {
      copy.set(entry.getKey(), entry.getValue());
    }
    return copy;
  }
}
