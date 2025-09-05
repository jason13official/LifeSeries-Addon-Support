package com.cursee.ls_addon_support.utils.world;

import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
// import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
//? if >= 1.21.2
import net.minecraft.loot.context.LootWorldContext;

public class ItemSpawner {

  private static final Random random = new Random();
  HashMap<ItemStack, Integer> lootTable = new HashMap<>();

  public static List<ItemStack> getRandomItemsFromLootTable(MinecraftServer server,
      ServerWorld world, ServerPlayerEntity player, Identifier lootTableId) {
      if (server == null || world == null || player == null) {
          return new ArrayList<>();
      }
    try {
      LootWorldContext parameters = new LootWorldContext.Builder(world)
          .add(LootContextParameters.ORIGIN, player.getPos())
          .add(LootContextParameters.THIS_ENTITY, player)
          .build(LootContextTypes.COMMAND);

      LootTable lootTable = world.getServer()
          .getReloadableRegistries()
          .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId));

      if (lootTable == null) {
        LSAddonSupport.LOGGER.error("Loot table not found: " + lootTableId);
        return new ArrayList<>();
      }

      List<ItemStack> generatedLoot = lootTable.generateLoot(parameters);

      if (generatedLoot == null || generatedLoot.isEmpty()) {
        LSAddonSupport.LOGGER.error("No loot generated from table: " + lootTableId);
        return new ArrayList<>();
      }

      return generatedLoot;
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
    return new ArrayList<>();
  }

  public void addItem(ItemStack item, int weight) {
    lootTable.put(item.copy(), weight);
  }

  public ItemStack getRandomItem() {
    if (lootTable.isEmpty()) {
      return null;
    }

    int totalWeight = lootTable.values().stream().mapToInt(Integer::intValue).sum();

    int randomWeight = random.nextInt(totalWeight);

    for (Map.Entry<ItemStack, Integer> entry : lootTable.entrySet()) {
      randomWeight -= entry.getValue();
      if (randomWeight < 0) {
        return entry.getKey().copy();
      }
    }

    return null;
  }

}
