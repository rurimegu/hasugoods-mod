package dev.rurino.hasugoods.jade;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.block.AbstractNesoBaseBlock;
import net.minecraft.util.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class HasuJadePlugin implements IWailaPlugin {

  public static final Identifier NESO_BASE_BLOCK = Hasugoods.id("neso_base_block");

  @Override
  public void register(IWailaCommonRegistration registration) {
  }

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(new AbstractNesoBaseBlockProvider(), AbstractNesoBaseBlock.class);
  }
}
