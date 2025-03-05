package dev.rurino.hasugoods.item;

import dev.rurino.hasugoods.util.OshiUtils;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

public class OshiItem extends Item {
  protected final String oshiKey;

  public OshiItem(Settings settings, String oshiKey) {
    super(settings);
    this.oshiKey = oshiKey;
  }

  public String getOshiKey() {
    return oshiKey;
  }

  public Text getOshiDisplayName() {
    return OshiUtils.getOshiDisplayName(oshiKey);
  }
}
