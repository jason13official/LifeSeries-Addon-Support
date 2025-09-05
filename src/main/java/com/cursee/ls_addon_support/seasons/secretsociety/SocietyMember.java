package com.cursee.ls_addon_support.seasons.secretsociety;

import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;

public class SocietyMember {

  public final UUID uuid;
  public boolean initialized = false;

  public SocietyMember(ServerPlayerEntity player) {
    this.uuid = player.getUuid();
  }

  public ServerPlayerEntity getPlayer() {
    return PlayerUtils.getPlayer(uuid);
  }
}
