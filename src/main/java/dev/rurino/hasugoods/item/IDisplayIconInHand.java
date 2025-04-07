package dev.rurino.hasugoods.item;

import java.util.Optional;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface IDisplayIconInHand {
  Optional<Identifier> getEntityDisplayIconInHand(LivingEntity entity);
}
