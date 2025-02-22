package dev.rurino.hasugoods.item.badge;

import java.util.List;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UnopenedBadge extends Item {

  public static final RegistryKey<LootTable> UNOPENED_BADGE_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE,
      Hasugoods.id("item/unopened_badge"));

  public UnopenedBadge(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient)
      return super.use(world, user, hand);
    ServerWorld serverWorld = (ServerWorld) world;
    LootTable supplier = serverWorld.getServer().getReloadableRegistries().getLootTable(UNOPENED_BADGE_LOOT_TABLE);
    List<ItemStack> stacks = supplier
        .generateLoot(new LootWorldContext.Builder(serverWorld).build(LootContextTypes.EMPTY));
    if (stacks.isEmpty())
      return ActionResult.FAIL;
    for (ItemStack stack : stacks) {
      user.giveItemStack(stack);
    }
    ItemStack handStack = user.getStackInHand(hand);
    handStack.decrement(1);
    return ActionResult.SUCCESS;
  }
}
