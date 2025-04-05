package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.INesoComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils.IWithChara;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;

import java.util.Optional;

public interface INesoEntity extends IWithChara, Nameable {
  NesoSize getNesoSize();

  NesoConfig.Base getConfig();

  default Optional<INesoComponent> getNesoComponent() {
    var nesoComponentOptional = ModComponents.NESO.maybeGet(this);
    if (nesoComponentOptional.isEmpty()) {
      Hasugoods.LOGGER.warn("NesoComponent not found for INesoEntity: {}", this);
    }
    return nesoComponentOptional;
  }

  default long getStoredEnergy() {
    return getNesoComponent().map(INesoComponent::getStoredEnergy).orElse(0L);
  }

  default boolean useEnergy(long amount) {
    return getNesoComponent().map(component -> {
      long storedEnergy = component.getStoredEnergy();
      if (storedEnergy >= amount) {
        component.setStoredEnergy(storedEnergy - amount);
        return true;
      }
      return false;
    }).orElse(false);
  }

  default NesoItem getNesoItem() {
    Optional<NesoItem> item = NesoItem.getNesoItem(getCharaKey(), getNesoSize());
    if (item.isEmpty()) {
      Hasugoods.LOGGER.error("NesoItem not found: {} {}", getCharaKey(), getNesoSize());
      return null;
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
