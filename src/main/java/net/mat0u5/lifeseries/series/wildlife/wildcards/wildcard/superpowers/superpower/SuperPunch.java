package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class SuperPunch extends ToggleableSuperpower {
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
        if (player != null) NetworkHandlerServer.sendVignette(player, -1);
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

    public void tryRideEntity(Entity entity) {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;

        if (player.hasVehicle()) {
            player.dismountVehicle();
        }

        player.startRiding(entity, true);
    }
}
