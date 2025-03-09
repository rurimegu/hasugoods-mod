package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PositionZeroBlockEntity extends AbstractNesoBaseBlockEntity {

  public PositionZeroBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.POSITION_ZERO_BLOCK_ENTITY_TYPE, pos, state);
  }

  @Override
  protected ParticleState getParticleState() {
    Hasugoods.LOGGER.info("PositionZeroBlockEntity#getParticleState {}", getItemStack());
    if (getItemStack().isEmpty())
      return ParticleState.RANDOM;
    return ParticleState.WAVE;
  }

}
