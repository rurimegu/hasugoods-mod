package dev.rurino.hasugoods.item;

import com.google.common.collect.ImmutableList;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

public class OshiItem extends Item {
  public static final String RURINO_KEY = "rurino";
  public static final String MEGUMI_KEY = "megumi";
  public static final String HIME_KEY = "hime";
  public static final String SAYAKA_KEY = "sayaka";
  public static final String TSUZURI_KEY = "tsuzuri";
  public static final String KOSUZU_KEY = "kosuzu";
  public static final String KAHO_KEY = "kaho";
  public static final String KOZUE_KEY = "kozue";
  public static final String GINKO_KEY = "ginko";
  // TODO: Add more oshi keys
  public static final ImmutableList<String> ALL_OSHI_KEYS = ImmutableList.of(
      RURINO_KEY,
      MEGUMI_KEY);

  private final String oshiKey;

  public OshiItem(Settings settings, String oshiKey) {
    super(settings);
    this.oshiKey = oshiKey;
  }

  public String getOshiKey() {
    return oshiKey;
  }

  public Text getOshiDisplayName() {
    return Text.translatable(String.format("text.%s.%s", Hasugoods.MOD_ID, oshiKey));
  }
}
