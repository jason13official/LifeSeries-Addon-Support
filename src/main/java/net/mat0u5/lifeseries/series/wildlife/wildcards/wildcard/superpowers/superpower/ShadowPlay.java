package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ShadowPlay extends Superpower {
    public ShadowPlay(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.SHADOW_PLAY;
    }

    @Override
    public int getCooldownSeconds() {
        return 60;
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        List<ServerPlayerEntity> affectedPlayers = player.getServerWorld().getEntitiesByClass(ServerPlayerEntity.class, player.getBoundingBox().expand(7), playerEntity -> playerEntity.distanceTo(player) <= 7);
        StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0);
        StatusEffectInstance invis = new StatusEffectInstance(StatusEffects.INVISIBILITY, 100, 0);
        affectedPlayers.remove(player);
        for (ServerPlayerEntity affectedPlayer : affectedPlayers) {
            affectedPlayer.addStatusEffect(blindness);
        }
        player.addStatusEffect(invis);
        NetworkHandlerServer.sendPlayerInvisible(player.getUuid(), System.currentTimeMillis()+5000);
    }
}
