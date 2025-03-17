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

  public static Timer loop(int tick, Runnable action) {
    return new Timer(tick, action, Integer.MAX_VALUE);
  }

  public Timer attachToServerTick(World world) {
    if (world.isClient) {
      Hasugoods.LOGGER.warn("Timer cannot attach to server tick in client world, ignored.");
      return this;
    }
    worldTickTimers.add(new Pair<>(world, this));
    return this;
  }

  public int remaining() {
    return tick;
  }

  public int elapsed() {
    return initTick - tick;
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
