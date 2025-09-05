package com.cursee.ls_addon_support.utils;

import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.world.ItemStackUtils;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

public class ClientUtils {

  public static boolean shouldPreventGliding() {
      if (!LSAddonSupportClient.preventGliding) {
          return false;
      }
    MinecraftClient client = MinecraftClient.getInstance();
      if (client == null) {
          return false;
      }
      if (client.player == null) {
          return false;
      }
    ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
    return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
  }

  @Nullable
  public static PlayerEntity getPlayer(UUID uuid) {
    MinecraftClient client = MinecraftClient.getInstance();
      if (client == null) {
          return null;
      }
      if (client.world == null) {
          return null;
      }
    return client.world.getPlayerByUuid(uuid);
  }

  @Nullable
  public static Team getPlayerTeam() {
    MinecraftClient client = MinecraftClient.getInstance();
      if (client == null) {
          return null;
      }
      if (client.player == null) {
          return null;
      }
    return client.player.getScoreboardTeam();
  }
}
