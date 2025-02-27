package dev.rurino.hasugoods.util;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.minecraft.util.math.random.Random;

public class BadgeUtils {
  public static BadgeItem getRandomBadge(Random random, boolean isSecret, String... excludes) {
    List<BadgeItem> badgeList = (isSecret ? BadgeItem.ALL_SECRET_BADGES : BadgeItem.ALL_REGULAR_BADGES)
        .stream()
        .filter(badge -> !Stream.of(excludes).anyMatch(e -> badge.getOshiKey().equals(e)))
        .toList();
    return CollectionUtils.getRandomElement(badgeList, random);
  }

  public static BadgeItem getBadgeByOshi(String oshiKey, boolean isSecret) {
    if (StringUtils.isEmpty(oshiKey)) {
      return null;
    }
    List<BadgeItem> badgeList = isSecret ? BadgeItem.ALL_SECRET_BADGES : BadgeItem.ALL_REGULAR_BADGES;
    for (var badge : badgeList) {
      if (badge.getOshiKey().equals(oshiKey)) {
        return badge;
      }
    }
    return null;
  }
}
