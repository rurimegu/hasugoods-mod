package dev.rurino.hasugoods.item;

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

  private final String oshiName;

  public OshiItem(Settings settings, String oshiName) {
    super(settings);
    this.oshiName = oshiName;
  }

  public String getOshiName() {
    return oshiName;
  }

  public Text getOshiDisplayName() {
    return Text.translatable(String.format("text.%s.%s", Hasugoods.MOD_ID, oshiName));
  }
}
