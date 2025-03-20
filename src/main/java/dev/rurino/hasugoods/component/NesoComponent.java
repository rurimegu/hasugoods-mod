package dev.rurino.hasugoods.component;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.entity.NesoEntity;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class NesoComponent implements INesoComponent {

  private static final String NBT_ENERGY = "energyStored";

  private long storedEnergy;

  public NesoComponent(NesoEntity provider) {
  }

  @Override
  public long getStoredEnergy() {
    return this.storedEnergy;
  }

  @Override
  public void setStoredEnergy(long energyStored) {
    this.storedEnergy = energyStored;
  }

  @Override
  public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
    if (tag.contains(NBT_ENERGY)) {
      this.storedEnergy = tag.getLong(NBT_ENERGY);
    } else {
      this.storedEnergy = 0;
    }
  }

  @Override
  public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
    tag.putLong(NBT_ENERGY, this.storedEnergy);
  }

  @Override
  public void apply(ItemStack stack) {
    if (stack == null || stack.isEmpty())
      return;

    if (!(stack.getItem() instanceof NesoItem item)) {
      Hasugoods.LOGGER.warn("NesoComponent apply called on non-neso ItemStack {}", stack);
      return;
    }
    item.setStoredEnergy(stack, getStoredEnergy());
  }

  @Override
  public void readFrom(ItemStack stack) {
    if (stack == null || stack.isEmpty())
      return;

    if (!(stack.getItem() instanceof NesoItem item)) {
      Hasugoods.LOGGER.warn("NesoComponent readFrom called on non-neso ItemStack {}", stack);
      return;
    }
    setStoredEnergy(item.getStoredEnergy(stack));
  }

}
