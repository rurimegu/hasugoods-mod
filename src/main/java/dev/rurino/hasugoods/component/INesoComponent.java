package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.Component;

import net.minecraft.item.ItemStack;

public interface INesoComponent extends Component {
  long getStoredEnergy();

  void setStoredEnergy(long energyStored);

  void apply(ItemStack stack);

  void readFrom(ItemStack stack);
}
