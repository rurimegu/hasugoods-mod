package dev.rurino.hasugoods.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class NesoBaseBlockEntity extends AbstractNesoBaseBlockEntity {

  public NesoBaseBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.NESO_BASE_BLOCK_ENTITY_TYPE, pos, state);
  }

  @Override
  protected ParticleState getParticleState() {
    if (getItemStack().isEmpty())
      return ParticleState.RANDOM;
    return ParticleState.SPIRAL;
  }
}
