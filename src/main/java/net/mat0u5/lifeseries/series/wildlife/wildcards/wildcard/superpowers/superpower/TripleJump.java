package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.AttributeUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class TripleJump extends ToggleableSuperpower {

    public TripleJump(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.TRIPLE_JUMP;
    }

    @Override
    public void tick() {
        if (!active) return;
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        StatusEffectInstance jump = new StatusEffectInstance(StatusEffects.JUMP_BOOST, 219, 2, false, false, false);
        player.addStatusEffect(jump);
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        AttributeUtils.setSafeFallHeight(player, 9);
        player.playSoundToPlayer(SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, -1);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        player.removeStatusEffect(StatusEffects.JUMP_BOOST);
        AttributeUtils.resetSafeFallHeight(player);
        player.playSoundToPlayer(SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, 0);
    }
}
