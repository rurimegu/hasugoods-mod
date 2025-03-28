package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import net.minecraft.item.ItemStack;

public interface INesoComponent extends AutoSyncedComponent {
  long getStoredEnergy();

  void setStoredEnergy(long energyStored);

  void apply(ItemStack stack);

  void readFrom(ItemStack stack);
}
