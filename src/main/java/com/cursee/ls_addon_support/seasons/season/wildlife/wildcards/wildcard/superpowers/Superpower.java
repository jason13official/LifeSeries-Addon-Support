package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public abstract class Superpower {

  private final UUID playerUUID;
  public boolean active = false;
  public long cooldown = 0;

  public Superpower(ServerPlayerEntity player) {
    playerUUID = player.getUuid();
    SessionTranscript.newSuperpower(player, getSuperpower());
  }

  @Nullable
  public ServerPlayerEntity getPlayer() {
    return PlayerUtils.getPlayer(playerUUID);
  }

  public abstract Superpowers getSuperpower();

  public int getCooldownMillis() {
    return 1000;
  }

  public void tick() {
  }

  public void onKeyPressed() {
    if (System.currentTimeMillis() < cooldown) {
      sendCooldownPacket();
      return;
    }
    activate();
  }

  public void activate() {
    active = true;
    cooldown(getCooldownMillis());
  }

  public void deactivate() {
    active = false;
  }

  public void turnOff() {
    deactivate();
    NetworkHandlerServer.sendLongPacket(getPlayer(), PacketNames.SUPERPOWER_COOLDOWN, 0);
  }

  public void cooldown(int millis) {
    cooldown = System.currentTimeMillis() + millis;
  }

  public void sendCooldownPacket() {
    NetworkHandlerServer.sendLongPacket(getPlayer(), PacketNames.SUPERPOWER_COOLDOWN, cooldown);
  }
}
