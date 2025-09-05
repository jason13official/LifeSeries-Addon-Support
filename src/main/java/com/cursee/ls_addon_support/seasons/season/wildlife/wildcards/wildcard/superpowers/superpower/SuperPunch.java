package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class SuperPunch extends ToggleableSuperpower {

  private long ticks = 0;
  private Entity riding = null;

  public SuperPunch(ServerPlayerEntity player) {
    super(player);
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.SUPER_PUNCH;
  }

  @Override
  public void activate() {
    super.activate();
    ServerPlayerEntity player = getPlayer();
      if (player != null) {
          NetworkHandlerServer.sendVignette(player, -1);
      }
  }

  @Override
  public void deactivate() {
    super.deactivate();
    ServerPlayerEntity player = getPlayer();
    if (player != null) {
      NetworkHandlerServer.sendVignette(player, 0);
      if (player.hasVehicle()) {
        player.dismountVehicle();
      }
    }
  }

  @Override
  public void tick() {
    ticks++;
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    if (ticks % 5 == 0) {
      if (riding != null && !player.hasVehicle()) {
        syncEntityPassengers(riding, PlayerUtils.getServerWorld(player));
        riding = null;
      }
    }
  }

  public void tryRideEntity(Entity entity) {
    ServerPlayerEntity rider = getPlayer();
      if (rider == null) {
          return;
      }

      if (entity.hasPassengers()) {
          return;
      }

    ServerWorld riderWorld = PlayerUtils.getServerWorld(rider);

    if (rider.hasVehicle()) {
      Entity vehicle = rider.getVehicle();
      rider.dismountVehicle();

      syncEntityPassengers(vehicle, riderWorld);
    }

    boolean rideResult = rider.startRiding(entity, true);

    if (rideResult) {
      riding = entity;
      syncEntityPassengers(entity, riderWorld);
    }
  }

  private void syncEntityPassengers(Entity entity, ServerWorld world) {
    EntityPassengersSetS2CPacket passengersPacket = new EntityPassengersSetS2CPacket(entity);

    for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(world, entity.getBlockPos())) {
      trackingPlayer.networkHandler.sendPacket(passengersPacket);
    }

    if (entity instanceof ServerPlayerEntity ridingPlayer) {
      ridingPlayer.networkHandler.sendPacket(passengersPacket);
    }

    for (Entity passenger : entity.getPassengerList()) {
      if (passenger instanceof ServerPlayerEntity ridingPlayer) {
        ridingPlayer.networkHandler.sendPacket(passengersPacket);
      }
    }
  }
}
