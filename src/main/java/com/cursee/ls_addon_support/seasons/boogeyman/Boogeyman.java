package com.cursee.ls_addon_support.seasons.boogeyman;

import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;

public class Boogeyman {

  public UUID uuid;
  public String name;
  public boolean cured = false;
  public boolean failed = false;
  public boolean died = false;

  public Boogeyman(ServerPlayerEntity player) {
    uuid = player.getUuid();
    name = player.getNameForScoreboard();
  }

  public ServerPlayerEntity getPlayer() {
    return PlayerUtils.getPlayer(uuid);
  }
}
