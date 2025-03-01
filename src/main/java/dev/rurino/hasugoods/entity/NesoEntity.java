package dev.rurino.hasugoods.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class NesoEntity extends Entity {

  public NesoEntity(EntityType<? extends Entity> type, World world) {
    super((EntityType<?>) type, world);
  }

  @Override
  public boolean damage(ServerWorld world, DamageSource source, float amount) {
    return false;
  }

  @Override
  protected void initDataTracker(Builder builder) {
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
  }

}
