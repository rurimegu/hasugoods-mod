package dev.rurino.hasugoods.jade;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.rurino.hasugoods.entity.INesoEntity;
import dev.rurino.hasugoods.util.HasuString;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.Accessor;
import snownee.jade.api.ui.MessageType;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

class NesoEntityEnergyProvider
    implements IServerExtensionProvider<NbtCompound>, IClientExtensionProvider<NbtCompound, EnergyView> {

  public static final NesoEntityEnergyProvider INSTANCE = new NesoEntityEnergyProvider();

  private NesoEntityEnergyProvider() {
  }

  @Override
  public Identifier getUid() {
    return HasuJadePlugin.NESO_ENTITY;
  }

  @Override
  public @Nullable List<ViewGroup<NbtCompound>> getGroups(Accessor<?> accessor) {
    if (!(accessor.getTarget() instanceof INesoEntity nesoEntity)) {
      return null;
    }
    long capacity = nesoEntity.getConfig().maxEnergy();
    long energy = nesoEntity.getStoredEnergy();
    return List.of(new ViewGroup<>(List.of(EnergyView.of(energy, capacity))));
  }

  @Override
  public List<ClientViewGroup<EnergyView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<NbtCompound>> groups) {
    return ClientViewGroup.map(groups, data -> EnergyView.read(data, HasuString.ENERGY_UNIT), (group, clientGroup) -> {
      if (group.id != null) {
        clientGroup.title = Text.literal(group.id);
        clientGroup.messageType = MessageType.DANGER;
      } else {
        clientGroup.messageType = MessageType.INFO;
      }
    });
  }

}
