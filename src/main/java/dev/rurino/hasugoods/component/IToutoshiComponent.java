package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.Component;

import dev.rurino.hasugoods.item.OshiItem;

public interface IToutoshiComponent extends Component {
  OshiItem getToutoshiSourceItem();

  void setToutoshiSourceItem(OshiItem item);
}
