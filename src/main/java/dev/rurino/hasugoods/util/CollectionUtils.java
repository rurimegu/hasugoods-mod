package dev.rurino.hasugoods.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.util.math.random.Random;

public class CollectionUtils {
  public static <T> T getRandomElement(List<T> array, Random random) {
    return array.get(random.nextInt(array.size()));
  }

  public static <T> T getRandomElement(T[] array, Random random) {
    return array[random.nextInt(array.length)];
  }

  public static <T> ArrayList<T> pickRandom(Collection<T> collection, int count, Random random) {
    int n = collection.size();
    ArrayList<T> result = new ArrayList<>();
    count = Math.min(count, n);
    for (T element : collection) {
      if (random.nextInt(n) < count) {
        result.add(element);
        if (--count == 0) {
          break;
        }
      }
      n--;
    }
    assert count == 0 : count + " elements left";
    return result;
  }
}
