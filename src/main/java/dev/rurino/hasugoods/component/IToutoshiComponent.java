package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import net.minecraft.item.Item;

public interface IToutoshiComponent extends AutoSyncedComponent {
  Item getToutoshiSourceItem();

  void setToutoshiSourceItem(Item item);
}
