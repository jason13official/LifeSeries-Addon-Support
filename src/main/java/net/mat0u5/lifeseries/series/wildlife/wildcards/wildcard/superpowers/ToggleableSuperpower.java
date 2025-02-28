package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ToggleableSuperpower extends Superpower {

    public ToggleableSuperpower(ServerPlayerEntity player) {
        super(player);
    }

    public void onKeyPressed() {
        if (System.currentTimeMillis() < cooldown) {
            sendCooldownPacket();
            return;
        }
        if (active) {
            deactivate();
        }
        else {
            activate();
        }
    }

    public int activateCooldownMillis() {
        return 50;
    }

    public int deactivateCooldownMillis() {
        return 1000;
    }

    public void activate() {
        active = true;
        cooldown(activateCooldownMillis());
    }

    public void deactivate() {
        active = false;
        cooldown(deactivateCooldownMillis());
    }
}
