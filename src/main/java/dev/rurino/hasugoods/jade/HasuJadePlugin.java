package dev.rurino.hasugoods.jade;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.block.AbstractNesoBaseBlock;
import dev.rurino.hasugoods.entity.NesoEntity;
import net.minecraft.util.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class HasuJadePlugin implements IWailaPlugin {

  public static final Identifier NESO_BASE_BLOCK = Hasugoods.id("neso_base_block");
  public static final Identifier NESO_ENTITY = Hasugoods.id("neso_entity");

  @Override
  public void register(IWailaCommonRegistration registration) {
  }

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(new AbstractNesoBaseBlockProvider(), AbstractNesoBaseBlock.class);
    registration.registerEntityComponent(new NesoEntityProvider(), NesoEntity.class);
  }
}
