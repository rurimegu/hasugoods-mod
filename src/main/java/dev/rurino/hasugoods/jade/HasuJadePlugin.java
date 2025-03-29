package dev.rurino.hasugoods.jade;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.block.AbstractNesoBaseBlockEntity;
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
    registration.registerEnergyStorage(AbstractNesoBaseBlockEnergyProvider.INSTANCE, AbstractNesoBaseBlockEntity.class);
    registration.registerEnergyStorage(NesoEntityEnergyProvider.INSTANCE, NesoEntity.class);
  }

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerEnergyStorageClient(AbstractNesoBaseBlockEnergyProvider.INSTANCE);
    registration.registerEnergyStorageClient(NesoEntityEnergyProvider.INSTANCE);
  }
}
