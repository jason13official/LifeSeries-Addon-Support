package net.mat0u5.lifeseries.seasons.secretsociety;

import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

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
