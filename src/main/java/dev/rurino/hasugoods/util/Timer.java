package dev.rurino.hasugoods.util;

import java.util.LinkedList;

import dev.rurino.hasugoods.Hasugoods;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

public class Timer {
  private static final int CANCELED = -1;

  private static LinkedList<Pair<World, Timer>> worldTickTimers = new LinkedList<>();

  static {
    ServerTickEvents.START_WORLD_TICK.register((w) -> {
      worldTickTimers.removeIf(pair -> {
        if (pair.getLeft() == w) {
          return pair.getRight().tick();
        }
        return false;
      });
    });
  }

  private int tick = 0;
  private int repeat;
  private int totTick;
  private Runnable action;

  /**
   * Creates a timer.
   * 
   * @param tick   the amount of ticks before running.
   * @param action the action to run.
   * @param repeat the amount of times to repeat.
   */
  public Timer(int tick, Runnable action, int repeat) {
    this.totTick = tick;
    this.action = action;
    this.repeat = repeat;
  }

  /**
   * Creates a one-off timer.
   * 
   * @param tick   the amount of ticks before running.
   * @param action the action to run.
   */
  public Timer(int tick, Runnable action) {
    this(tick, action, 1);
  }

  /**
   * Creates a timer that runs once every a certain amount of ticks.
   * 
   * @param tick   the amount of ticks between each run.
   * @param action the action to run.
   * @return the timer.
   */
  public static Timer loop(int tick, Runnable action) {
    return new Timer(tick, action, Integer.MAX_VALUE);
  }

  /**
   * Attach the timer to the server tick.
   * 
   * @param world the world tick to attach to.
   * @return <code>this</code> for chaining.
   */
  public Timer attachToServerTick(World world) {
    if (world.isClient) {
      Hasugoods.LOGGER.warn("Timer cannot attach to server tick in client world, ignored.");
      return this;
    }
    worldTickTimers.add(new Pair<>(world, this));
    return this;
  }

  /**
   * Reset the timer with a new tick and repeated action.
   * 
   * @param tick   the amount of ticks before running.
   * @param action the action to run.
   * @param repeat the amount of times to repeat.
   * @return <code>this</code> for chaining.
   */
  public Timer reset(int tick, Runnable action, int repeat) {
    this.totTick = tick;
    this.action = action;
    this.repeat = repeat;
    this.tick = 0;
    return this;
  }

  /**
   * Reset the timer with a new tick and one-off action.
   * 
   * @param tick   the amount of ticks before running.
   * @param action the action to run.
   * @return <code>this</code> for chaining.
   */
  public Timer reset(int tick, Runnable action) {
    return reset(tick, action, 1);
  }

  public int remaining() {
    return totTick - tick;
  }

  public int remainingRepeats() {
    return repeat;
  }

  public int elapsed() {
    return tick;
  }

  public boolean finished() {
    return tick == CANCELED;
  }

  /**
   * Cancel the timer. If attached to server tick, it will be removed at the next
   * tick.
   */
  public void cancel() {
    tick = CANCELED;
  }

  /**
   * Run the next action immediately.
   * 
   * @return true if the timer is finished.
   */
  public boolean run() {
    if (finished())
      return true;
    action.run();
    repeat--;
    if (repeat <= 0) {
      tick = CANCELED;
      return true;
    }
    tick = 0;
    return false;
  }

  /**
   * Tick the timer with delta time.
   * 
   * @param delta the delta time in ticks.
   * @return true if the timer is finished.
   */
  public boolean tick(int delta) {
    if (finished())
      return true;
    tick += delta;
    if (tick >= totTick)
      return run();
    return false;
  }

  /**
   * Tick the timer.
   * 
   * @return true if the timer is finished.
   */
  public boolean tick() {
    return tick(1);
  }

  @Override
  public String toString() {
    return String.format("Timer{tick=%d, repeat=%d, totTick=%d}", tick, repeat, totTick);
  }
}
