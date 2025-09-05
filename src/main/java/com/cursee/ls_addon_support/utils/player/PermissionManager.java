package com.cursee.ls_addon_support.utils.player;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionManager {

  public static boolean isAdmin(ServerPlayerEntity player) {
      if (player == null) {
          return false;
      }
      if (LSAddonSupport.isClientPlayer(player.getUuid())) {
          return true;
      }
    return player.hasPermissionLevel(2);
  }
}
