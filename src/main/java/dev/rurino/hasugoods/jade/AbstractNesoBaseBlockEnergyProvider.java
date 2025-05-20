package dev.rurino.hasugoods.jade;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.rurino.hasugoods.block.AbstractNesoBaseBlockEntity;
import dev.rurino.hasugoods.util.HasuString;
import net.minecraft.item.ItemStack;
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
import team.reborn.energy.api.base.SimpleEnergyItem;

public class AbstractNesoBaseBlockEnergyProvider
    implements IServerExtensionProvider<NbtCompound>, IClientExtensionProvider<NbtCompound, EnergyView> {
  public static final AbstractNesoBaseBlockEnergyProvider INSTANCE = new AbstractNesoBaseBlockEnergyProvider();

  private AbstractNesoBaseBlockEnergyProvider() {
  }

  @Override
  public Identifier getUid() {
    return HasuJadePlugin.NESO_BASE_BLOCK;
  }

  @Override
  public @Nullable List<ViewGroup<NbtCompound>> getGroups(Accessor<?> accessor) {
    if (!(accessor.getTarget() instanceof AbstractNesoBaseBlockEntity blockEntity)) {
      return null;
    }
    ItemStack stack = blockEntity.getItemStack();
    if (!(stack.getItem() instanceof SimpleEnergyItem energyItem)) {
      return null;
    }
    long capacity = energyItem.getEnergyCapacity(stack);
    long energy = energyItem.getStoredEnergy(stack);
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
