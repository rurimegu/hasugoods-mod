package dev.rurino.hasugoods.util.animation;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {

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

  public StateMachine(int initialState) {
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

    if (prevFrame == null) {
      curState.frame = currentAnimation.getKeyFrame(curState.tick);
    } else {
      curState.frame = currentAnimation.getKeyFrame(tick, transitTick, prevFrame);
    }
    return curState.frame;
  }

  public void set(int state, Animation animation) {
    states.put(state, animation);
  }

  public void transit(int state, double tick) {
    if (!states.containsKey(state)) {
      throw new IllegalStateException("No animation for state " + state);
    }
    if (state == curState.state) {
      return;
    }
    curState.tick = 0;
    curState.state = state;
    transitTick = tick;
    prevFrame = curState.frame;
  }
}
