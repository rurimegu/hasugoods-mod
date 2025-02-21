package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.Component;

import net.minecraft.item.Item;

public interface IToutoshiComponent extends Component {
  Item getToutoshiSourceItem();

  void setToutoshiSourceItem(Item item);
}
