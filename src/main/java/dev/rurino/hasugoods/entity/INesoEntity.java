package dev.rurino.hasugoods.entity;

import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils.IWithChara;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;

public interface INesoEntity extends IWithChara, Nameable {
  NesoSize getNesoSize();

  long getStoredEnergy();

  NesoConfig.Base getConfig();

  default NesoItem getNesoItem() {
    Optional<NesoItem> item = NesoItem.getNesoItem(getCharaKey(), getNesoSize());
    if (item.isEmpty()) {
      Hasugoods.LOGGER.error("NesoItem not found: {} {}", getCharaKey(), getNesoSize());
    }
    return item.get();
  }

  default ItemStack convertToNesoItemStack() {
    ItemStack stack = new ItemStack(getNesoItem());
    // Set custom name
    Text text = this.getCustomName();
    if (text != null) {
      stack.set(DataComponentTypes.CUSTOM_NAME, text);
    }
    var nesoComponentOptional = ModComponents.NESO.maybeGet(this);
    if (nesoComponentOptional.isPresent()) {
      nesoComponentOptional.get().apply(stack);
    } else {
      Hasugoods.LOGGER.warn("NesoComponent not found for NesoEntity: {}", this);
    }
    return stack;
  }
}
