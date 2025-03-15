package dev.rurino.hasugoods.item;

import dev.rurino.hasugoods.util.CharaUtils;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

public class CharaItem extends Item {
  private final String charaKey;

  public CharaItem(Settings settings, String charaKey) {
    super(settings);
    this.charaKey = charaKey;
  }

  public String getCharaKey() {
    return charaKey;
  }

  public Text getCharaDisplayName() {
    return CharaUtils.getCharaDisplayName(charaKey);
  }
}
