package dev.rurino.hasugoods.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

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

  public static <T> boolean equalsIgnoreOrder(Iterable<T> a, Iterable<T> b) {
    List<T> listA = new ArrayList<>();
    List<T> listB = new ArrayList<>();
    a.forEach(listA::add);
    b.forEach(listB::add);
    if (listA.size() != listB.size()) {
      return false;
    }
    listA.removeAll(listB);
    return listA.isEmpty();
  }

  public static <T> boolean equalsIgnoreOrder(Stream<T> a, Stream<T> b) {
    List<T> listA = new ArrayList<>(a.toList()); // Convert to mutable list
    List<T> listB = b.toList();
    listA.removeAll(listB);
    return listA.isEmpty();
  }

  public static String toDebugString(Iterable<?> collection) {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    boolean first = true;
    for (Object o : collection) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(o);
    }
    sb.append(']');
    return sb.toString();
  }
}
