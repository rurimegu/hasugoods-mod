package dev.rurino.hasugoods.util;

public class Timer {
  private double tick;

  private final Runnable action;

  public Timer(double tick, Runnable action) {
    this.tick = tick;
    this.action = action;
  }

  public boolean update(double delta) {
    if (Double.isInfinite(tick)) {
      return true;
    }
    tick -= delta;
    if (tick <= 0) {
      tick = Double.POSITIVE_INFINITY;
      action.run();
      return true;
    }
    return false;
  }
}
