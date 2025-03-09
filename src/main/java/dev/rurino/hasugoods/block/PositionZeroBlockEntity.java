package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.particle.NoteParticleEffect;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PositionZeroBlockEntity extends NesoBaseBlockEntity {
  public PositionZeroBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.POSITION_ZERO_BLOCK_ENTITY_TYPE, pos, state);
  }

  public static void tick(World world, BlockPos blockPos, BlockState blockState, PositionZeroBlockEntity entity) {
    if (!world.isClient)
      return;
    entity.displayTick++;
    entity.displayTick %= TICK_PER_SIDE << 2;
    if (entity.displayTick != 0) {
      return;
    }

    double y = blockPos.getY() + 0.9;
    NoteParticleEffect noteParticleEffect = new NoteParticleEffect(0x00FFFF);

    for (int i = 0; i < TICK_PER_SIDE << 2; i++) {
      int side = i / TICK_PER_SIDE;
      double progress = (i % TICK_PER_SIDE) / (double) TICK_PER_SIDE;
      double x;
      double z;
      switch (side) {
        case 0:
          x = progress;
          z = 0;
          break;
        case 1:
          x = 1;
          z = progress;
          break;
        case 2:
          x = 1 - progress;
          z = 1;
          break;
        case 3:
          x = 0;
          z = 1 - progress;
          break;
        default:
          throw new IllegalStateException("Unexpected side value: " + side);
      }
      x += blockPos.getX();
      z += blockPos.getZ();
      world.addParticle(noteParticleEffect, x, y, z, 0, 0.04, 0);
    }
  }

}
