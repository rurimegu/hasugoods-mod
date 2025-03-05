package dev.rurino.hasugoods.item.neso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.entity.NesoEntity;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.OshiItem;
import dev.rurino.hasugoods.util.OshiUtils.NesoSize;
import dev.rurino.hasugoods.util.OshiUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class NesoItem extends OshiItem {
  // #region Static fields

  protected static record NesoItemEntry(RegistryKey<Item> key, NesoItem item) {
  }

  protected static final Map<String, NesoItemEntry> ALL_NESOS = new HashMap<>();

  public static String nesoKey(String oshiKey, NesoSize size) {
    return oshiKey + "_neso_" + size.name().toLowerCase();
  }

  protected static Optional<NesoItemEntry> getNesoItemEntry(String oshiKey, NesoSize size) {
    return Optional.ofNullable(ALL_NESOS.get(nesoKey(oshiKey, size)));
  }

  public static Optional<NesoItem> getNesoItem(String oshiKey, NesoSize size) {
    return getNesoItemEntry(oshiKey, size).map(neso -> neso.item());
  }

  public static Optional<RegistryKey<Item>> getNesoItemKey(String oshiKey, NesoSize size) {
    return getNesoItemEntry(oshiKey, size).map(neso -> neso.key());
  }

  public static List<NesoItem> getAllNesos(NesoSize size) {
    return ALL_NESOS.values().stream()
        .filter(entry -> entry.item().nesoSize == size)
        .map(entry -> entry.item()).toList();
  }

  public static List<NesoItem> getAllNesos() {
    return ALL_NESOS.values().stream().map(entry -> entry.item()).toList();
  }

  private static NesoItem registerNeso(String oshiKey, NesoSize size) {
    String itemKey = nesoKey(oshiKey, size);
    RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Hasugoods.id(itemKey));
    Rarity rarity = switch (size) {
      case SMALL -> Rarity.COMMON;
      case MEDIUM -> Rarity.UNCOMMON;
      case LARGE -> Rarity.RARE;
    };
    NesoItem item = (NesoItem) ModItems.register(
        key,
        new NesoItem(new Settings().maxCount(1).rarity(rarity).registryKey(key), oshiKey, size));
    ALL_NESOS.put(itemKey, new NesoItemEntry(key, (NesoItem) item));
    return item;
  }

  public static void initialize() {
    for (NesoSize size : NesoSize.values()) {
      registerNeso(OshiUtils.KAHO_KEY, size);
    }
  }

  // #endregion Static fields

  protected final NesoSize nesoSize;

  public NesoItem(Settings settings, String oshiKey, NesoSize size) {
    super(settings, oshiKey);
    this.nesoSize = size;
  }

  public NesoSize getNesoSize() {
    return nesoSize;
  }

  public EntityType<NesoEntity> getEntityType() {
    Optional<EntityType<NesoEntity>> type = NesoEntity.getNesoEntityType(oshiKey, nesoSize);
    if (type.isEmpty()) {
      Hasugoods.LOGGER.error("Neso entity type not found: {} {}", oshiKey, nesoSize);
    }
    return type.get();
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    // Place the neso entity on the block if the block is not solid
    World world = context.getWorld();
    if (world.isClient)
      return ActionResult.CONSUME;

    ItemStack itemStack = context.getStack();
    BlockPos blockPos = context.getBlockPos();
    Direction direction = context.getSide();
    BlockState blockState = world.getBlockState(blockPos);
    EntityType<NesoEntity> entityType = getEntityType();

    BlockPos blockPos2;
    if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
      blockPos2 = blockPos;
    } else {
      blockPos2 = blockPos.offset(direction);
    }

    if (entityType.spawnFromItemStack((ServerWorld) world, itemStack, context.getPlayer(), blockPos2,
        SpawnReason.SPAWN_ITEM_USE, true,
        !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
      itemStack.decrement(1);
      world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
    }

    return ActionResult.SUCCESS;
  }

}
