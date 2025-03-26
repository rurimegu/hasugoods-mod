package dev.rurino.hasugoods.jade;

import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.entity.NesoEntity;
import dev.rurino.hasugoods.util.HasuString;
import net.minecraft.util.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

class NesoEntityProvider implements IEntityComponentProvider {

  @Override
  public void appendTooltip(ITooltip tooltip, EntityAccessor entityAccessor, IPluginConfig pluginConfig) {
    if (entityAccessor.getEntity() instanceof NesoEntity nesoEntity) {
      long capacity = NesoConfig.getConfig(nesoEntity.getCharaKey(), nesoEntity.getNesoSize()).maxEnergy();
      long energy = nesoEntity.getStoredEnergy();
      tooltip.add(HasuString.formatEnergyTooltip(energy, capacity));
    }
  }

  @Override
  public Identifier getUid() {
    return HasuJadePlugin.NESO_ENTITY;
  }

}
