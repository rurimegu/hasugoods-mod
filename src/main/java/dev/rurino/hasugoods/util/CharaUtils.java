package dev.rurino.hasugoods.util;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class CharaUtils {
  public static final String RURINO_KEY = "rurino";
  public static final String MEGUMI_KEY = "megumi";
  public static final String HIME_KEY = "hime";
  public static final String SAYAKA_KEY = "sayaka";
  public static final String TSUZURI_KEY = "tsuzuri";
  public static final String KOSUZU_KEY = "kosuzu";
  public static final String KAHO_KEY = "kaho";
  public static final String KOZUE_KEY = "kozue";
  public static final String GINKO_KEY = "ginko";
  public static final ImmutableList<String> ALL_CHARA_KEYS = ImmutableList.of(
      RURINO_KEY,
      MEGUMI_KEY,
      HIME_KEY,
      SAYAKA_KEY,
      TSUZURI_KEY,
      KOSUZU_KEY,
      KAHO_KEY,
      KOZUE_KEY,
      GINKO_KEY);

  public static final ImmutableMap<String, Integer> CHARA_COLOR_MAP = ImmutableMap.of(
      RURINO_KEY, 0xE7609E,
      MEGUMI_KEY, 0xC8C2C6,
      HIME_KEY, 0x9D8DE2,
      SAYAKA_KEY, 0x5383C3,
      TSUZURI_KEY, 0xC22D3B,
      KOSUZU_KEY, 0xFAD764,
      KAHO_KEY, 0xF8B500,
      KOZUE_KEY, 0x68BE8D,
      GINKO_KEY, 0xA2D7DD);

  public static Text getCharaDisplayName(String charaKey) {
    return Text.translatable(String.format("text.%s.%s", Hasugoods.MOD_ID, charaKey));
  }

  public static final int DEFAULT_CHARA_COLOR = 0xFFFFFF;

  public static int getCharaColor(String charaKey) {
    return CHARA_COLOR_MAP.getOrDefault(charaKey, DEFAULT_CHARA_COLOR);
  }

  // #region Groups

  public static enum HasuUnit {
    NONE,
    CERISE_BOUQUET,
    DOLLCHESTRA,
    MIRACRA_PARK
  }

  public static HasuUnit getUnit(String charaKey) {
    if (charaKey == null)
      return HasuUnit.NONE;
    return switch (charaKey) {
      case KOZUE_KEY, KAHO_KEY, GINKO_KEY -> HasuUnit.CERISE_BOUQUET;
      case TSUZURI_KEY, SAYAKA_KEY, KOSUZU_KEY -> HasuUnit.DOLLCHESTRA;
      case MEGUMI_KEY, RURINO_KEY, HIME_KEY -> HasuUnit.MIRACRA_PARK;
      default -> HasuUnit.NONE;
    };
  }

  public static int getGrade(String charaKey) {
    if (charaKey == null)
      return -1;
    return switch (charaKey) {
      case KOZUE_KEY, TSUZURI_KEY, MEGUMI_KEY -> 102;
      case KAHO_KEY, SAYAKA_KEY, RURINO_KEY -> 103;
      case GINKO_KEY, KOSUZU_KEY, HIME_KEY -> 104;
      default -> -1;
    };
  }

  // #endregion Group

  // #region Badge
  public static BadgeItem getRandomBadge(Random random, boolean isSecret, String... excludes) {
    List<BadgeItem> badgeList = BadgeItem.getAllBadges(isSecret)
        .stream()
        .filter(badge -> !Stream.of(excludes).anyMatch(e -> badge.getCharaKey().equals(e)))
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

  public static String nesoKey(String charaKey, NesoSize size) {
    return charaKey + "_neso_" + size.name().toLowerCase();
  }
  // #endregion Neso
}
