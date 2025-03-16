package dev.rurino.hasugoods.util.animation;

import java.util.HashMap;
import java.util.Map;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.ICopyable;

public class StateMachine implements ICopyable<StateMachine> {

  protected static class InnerState {
    public KeyFrame frame;
    public int state;
    public double tick;

    public InnerState(KeyFrame frame, int state, double tick) {
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
  private double transitTick;
  private KeyFrame prevFrame;
  private final Map<Integer, Animation> states = new HashMap<>();
  private final int initialState;

  public StateMachine(int initialState) {
    this.initialState = initialState;
    this.curState = new InnerState(null, initialState, 0);
  }

  public KeyFrame get() {
    if (curState.frame == null)
      return update(0);

    return curState.frame;
  }

  public KeyFrame update(double tick) {
    curState.tick += tick;
    Animation currentAnimation = states.get(curState.state);
    if (currentAnimation == null) {
      throw new IllegalStateException("No animation for state " + curState.state);
    }
    curState.frame = currentAnimation.getKeyFrame(curState.tick);

    if (prevFrame != null) {
      double progress = curState.tick / transitTick;
      if (progress < 1) {
        curState.frame = Interpolator.LINEAR.interpolate(prevFrame, curState.frame, progress);
      }
    }
    return curState.frame;
  }

  public void set(int state, Animation animation) {
    states.put(state, animation);
  }

  public void transit(int state, double tick) {
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

  @Override
  public StateMachine copy() {
    StateMachine copy = new StateMachine(initialState);
    for (Map.Entry<Integer, Animation> entry : states.entrySet()) {
      copy.set(entry.getKey(), entry.getValue());
    }
    return copy;
  }
}
