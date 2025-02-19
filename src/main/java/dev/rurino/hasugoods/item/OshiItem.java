package dev.rurino.hasugoods.item;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

public class OshiItem extends Item {
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
