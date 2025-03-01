package dev.rurino.hasugoods.item;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

public class OshiItem extends Item {
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
