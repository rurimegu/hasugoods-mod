package dev.rurino.hasugoods.util;

import java.util.List;

import net.minecraft.util.math.random.Random;

public class CollectionUtils {
  public static <T> T getRandomElement(List<T> array, Random random) {
    return array.get(random.nextInt(array.size()));
  }
}
