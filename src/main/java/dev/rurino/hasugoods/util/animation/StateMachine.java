package dev.rurino.hasugoods.util.animation;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
  protected int currentState;
  protected float currentTick = 0;
  protected KeyFrame currentFrame;
  protected final Map<Integer, Animation> states = new HashMap<>();

  public StateMachine(int initialState) {
    this.currentState = initialState;
  }

  public KeyFrame getFrame() {
    return currentFrame;
  }

  public KeyFrame update(float tick) {
    Animation currentAnimation = states.get(currentState);
    if (currentAnimation == null) {
      throw new IllegalStateException("No animation for state " + currentState);
    }

    currentTick += tick;
    currentFrame = currentAnimation.getKeyFrame(currentTick, currentFrame);
    return currentFrame;
  }

  public void addState(int state, Animation animation) {
    states.put(state, animation);
  }

  public void transit(int state) {
    currentState = state;
    currentTick = 0;
  }
}
