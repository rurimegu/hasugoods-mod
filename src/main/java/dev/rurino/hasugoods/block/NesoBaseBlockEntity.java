package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.particle.NoteParticleEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NesoBaseBlockEntity extends BlockEntity {
  protected static final String NESO_ITEM_STACK_KEY = "nesoItemStack";
  protected static final int TICK_PER_SIDE = 4;

  protected ItemStack nesoItemStack = ItemStack.EMPTY;
  protected int displayTick = 0;

  public NesoBaseBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.NESO_BASE_BLOCK_ENTITY_TYPE, pos, state);
  }

  public NesoBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
    super.writeNbt(nbt, registries);
    nbt.put(NESO_ITEM_STACK_KEY, nesoItemStack.toNbtAllowEmpty(registries));
  }

  @Override
  protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
    super.readNbt(nbt, registries);
    nesoItemStack = ItemStack.fromNbtOrEmpty(registries, nbt.getCompound(NESO_ITEM_STACK_KEY));
  }

  public static void tick(World world, BlockPos blockPos, BlockState blockState, NesoBaseBlockEntity entity) {
    if (!world.isClient)
      return;

    entity.displayTick++;
    entity.displayTick %= TICK_PER_SIDE << 2;
    int side = entity.displayTick / TICK_PER_SIDE;
    double sideProgress = (entity.displayTick % TICK_PER_SIDE) / (double) TICK_PER_SIDE;

    double x;
    double z;
    switch (side) {
      case 0:
        x = sideProgress;
        z = 0;
        break;
      case 1:
        x = 1;
        z = sideProgress;
        break;
      case 2:
        x = 1 - sideProgress;
        z = 1;
        break;
      case 3:
        x = 0;
        z = 1 - sideProgress;
        break;
      default:
        throw new IllegalStateException("Unexpected side value: " + side);
    }

    x += blockPos.getX();
    double y = blockPos.getY() + 0.9;
    z += blockPos.getZ();

    NoteParticleEffect noteParticleEffect = new NoteParticleEffect(0x00FFFF);
    world.addParticle(noteParticleEffect, x, y, z, 0, 0.02, 0);
  }

}
