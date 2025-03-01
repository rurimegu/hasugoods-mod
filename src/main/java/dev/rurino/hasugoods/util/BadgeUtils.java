package dev.rurino.hasugoods.util;

import java.util.List;
import java.util.stream.Stream;

import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.minecraft.util.math.random.Random;

public class BadgeUtils {
  public static BadgeItem getRandomBadge(Random random, boolean isSecret, String... excludes) {
    List<BadgeItem> badgeList = BadgeItem.getAllBadges(isSecret)
        .stream()
        .filter(badge -> !Stream.of(excludes).anyMatch(e -> badge.getOshiKey().equals(e)))
        .toList();
    return CollectionUtils.getRandomElement(badgeList, random);
  }
}
