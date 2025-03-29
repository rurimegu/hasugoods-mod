package dev.rurino.hasugoods.util;

import java.util.List;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.ViewGroup;

public class HasuString {
  public static final String ENERGY_UNIT = "OP";

  public static String formatEnergy(long amount) {
    if (amount < 0) {
      return "0";
    }
    if (amount >= 1_000_000_000) {
      return String.format("%.1fB", amount / 1_000_000_000.0);
    }
    if (amount >= 1_000_000) {
      return String.format("%.1fM", amount / 1_000_000.0);
    }
    if (amount >= 1_000) {
      return String.format("%.1fK", amount / 1_000.0);
    }
    if (amount >= 0) {
      return String.valueOf(amount);
    }
    return "?";
  }

  public static Text formatEnergyTooltip(long amount, long capacity) {
    return Text.empty()
        .append(Text.literal(ENERGY_UNIT + ":").formatted(Formatting.GOLD))
        .append(" ")
        .append(HasuString.formatEnergy(amount))
        .append(Text.literal("/").formatted(Formatting.AQUA))
        .append(HasuString.formatEnergy(capacity));
  }

  public static ViewGroup<EnergyView.Data> createEnergyView(long amount, long capacity) {
    var group = new ViewGroup<>(List.of(new EnergyView.Data(amount, capacity)));
    group.getExtraData().putString("Unit", ENERGY_UNIT);
    return group;
  }
}
