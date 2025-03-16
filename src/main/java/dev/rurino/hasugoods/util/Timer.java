package dev.rurino.hasugoods.util;

public class Timer {
  private static int CANCELED = -1;

  private int tick;
  private int repeat;
  private final int initTick;
  private final Runnable action;

  public Timer(int tick, Runnable action, int repeat) {
    this.initTick = tick;
    this.tick = tick;
    this.action = action;
    this.repeat = repeat;
  }

  public Timer(int tick, Runnable action) {
    this(tick, action, 1);
  }

  public static Timer Loop(int tick, Runnable action) {
    return new Timer(tick, action, Integer.MAX_VALUE);
  }

  public int remaining() {
    return tick;
  }

  public boolean finished() {
    return tick == CANCELED;
  }

  public void cancel() {
    tick = CANCELED;
  }

  public boolean run() {
    if (finished())
      return true;
    action.run();
    repeat--;
    if (repeat <= 0) {
      tick = CANCELED;
      return true;
    }
    tick = initTick;
    return false;
  }

  public boolean tick(int delta) {
    if (finished())
      return true;
    tick -= delta;
    if (tick <= 0)
      return run();
    return false;
  }

  public boolean tick() {
    return tick(1);
  }
}
