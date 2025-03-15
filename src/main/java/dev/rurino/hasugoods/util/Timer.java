package dev.rurino.hasugoods.util;

public class Timer {
  private int tick;

  private final Runnable action;

  public Timer(int tick, Runnable action) {
    this.tick = tick;
    this.action = action;
  }

  public int remaining() {
    return tick;
  }

  public void run() {
    if (tick == -1)
      return;
    tick = -1;
    action.run();
  }

  public boolean tick(int delta) {
    tick -= delta;
    if (tick <= 0) {
      run();
      return true;
    }
    return false;
  }

  public boolean tick() {
    return tick(1);
  }
}
