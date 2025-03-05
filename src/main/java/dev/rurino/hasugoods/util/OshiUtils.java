package dev.rurino.hasugoods.util;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class OshiUtils {
  public static final String RURINO_KEY = "rurino";
  public static final String MEGUMI_KEY = "megumi";
  public static final String HIME_KEY = "hime";
  public static final String SAYAKA_KEY = "sayaka";
  public static final String TSUZURI_KEY = "tsuzuri";
  public static final String KOSUZU_KEY = "kosuzu";
  public static final String KAHO_KEY = "kaho";
  public static final String KOZUE_KEY = "kozue";
  public static final String GINKO_KEY = "ginko";
  public static final ImmutableList<String> ALL_OSHI_KEYS = ImmutableList.of(
      RURINO_KEY,
      MEGUMI_KEY,
      HIME_KEY,
      SAYAKA_KEY,
      TSUZURI_KEY,
      KOSUZU_KEY,
      KAHO_KEY,
      KOZUE_KEY,
      GINKO_KEY);

  public static Text getOshiDisplayName(String oshiKey) {
    return Text.translatable(String.format("text.%s.%s", Hasugoods.MOD_ID, oshiKey));
  }

  // #region Badge
  public static BadgeItem getRandomBadge(Random random, boolean isSecret, String... excludes) {
    List<BadgeItem> badgeList = BadgeItem.getAllBadges(isSecret)
        .stream()
        .filter(badge -> !Stream.of(excludes).anyMatch(e -> badge.getOshiKey().equals(e)))
        .toList();
    return CollectionUtils.getRandomElement(badgeList, random);
  }
  // #endregion Badge

  // #region Neso
  public static enum NesoSize {
    SMALL,
    MEDIUM,
    LARGE
  }

  public static String nesoKey(String oshiKey, NesoSize size) {
    return oshiKey + "_neso_" + size.name().toLowerCase();
  }
  // #endregion Neso
}
