package dev.rurino.hasugoods.item;

import dev.rurino.hasugoods.util.CharaUtils.IWithChara;
import net.minecraft.item.Item;

public class CharaItem extends Item implements IWithChara {
  private final String charaKey;

  public CharaItem(Settings settings, String charaKey) {
    super(settings);
    this.charaKey = charaKey;
  }

  @Override
  public String getCharaKey() {
    return charaKey;
  }
}
