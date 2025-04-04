package dev.rurino.hasugoods.item.neso;

import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.entity.NesoEntity;
import dev.rurino.hasugoods.util.CharaUtils.IWithChara;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import team.reborn.energy.api.base.SimpleEnergyItem;

public interface INesoItem extends SimpleEnergyItem, IWithChara {
  NesoConfig.Base getConfig();

  NesoSize getNesoSize();

  default EntityType<NesoEntity> getEntityType() {
    String charaKey = getCharaKey();
    NesoSize nesoSize = getNesoSize();
    Optional<EntityType<NesoEntity>> type = NesoEntity.getNesoEntityType(charaKey, nesoSize);
    if (type.isEmpty()) {
      Hasugoods.LOGGER.error("Neso entity type not found: {} {}", charaKey, nesoSize);
    }
    return type.get();
  }

  default long chargeEnergy(ItemStack stack, long amount) {
    if (amount > 0) {
      long stored = getStoredEnergy(stack);
      long capacity = getEnergyCapacity(stack);
      amount = Math.min(amount, capacity - stored);
      if (amount > 0) {
        setStoredEnergy(stack, stored + amount);
        return amount;
      }
    }
    return 0;
  }

  default long extractEnergy(ItemStack stack, long amount) {
    if (amount > 0) {
      long stored = getStoredEnergy(stack);
      amount = Math.min(amount, stored);
      if (amount > 0) {
        setStoredEnergy(stack, stored - amount);
        return amount;
      }
    }
    return 0;
  }

  default void setFullEnergy(ItemStack stack) {
    long capacity = getEnergyCapacity(stack);
    if (capacity > 0) {
      setStoredEnergy(stack, capacity);
    }
  }
}
