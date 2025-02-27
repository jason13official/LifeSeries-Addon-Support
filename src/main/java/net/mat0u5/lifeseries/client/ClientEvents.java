package net.mat0u5.lifeseries.client;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class ClientEvents {
    public static void onClientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        spawnInvisibilityParticles(client);
        if (player != null) {
            sendPackets(client, player);
        }
        ClientKeybinds.tick();
    }

    public static void spawnInvisibilityParticles(MinecraftClient client) {
        if (client.world == null) return;
        if (client.world.random.nextInt(15) != 0) return;
        for (PlayerEntity player : client.world.getPlayers()) {
            if (MainClient.invisiblePlayers.containsKey(player.getUuid())) {
                long time = MainClient.invisiblePlayers.get(player.getUuid());
                if (time > System.currentTimeMillis() || time == -1) {
                    ParticleManager particleManager = client.particleManager;

                    double x = player.getX() + (Math.random() - 0.5) * 0.6;
                    double y = player.getY() + Math.random() * 1.8;
                    double z = player.getZ() + (Math.random() - 0.5) * 0.6;

                    ParticleEffect invisibilityParticle = EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0x208891b5);
                    particleManager.addParticle(invisibilityParticle, x, y, z, 0, 0, 0);
                }
            }
        }
    }

    public static void sendPackets(MinecraftClient client, ClientPlayerEntity player) {
        if (MainClient.clientCurrentSeries == SeriesList.WILD_LIFE && MainClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) {
            //? if <= 1.21 {
            if (player.input.jumping) {
                NetworkHandlerClient.sendHoldingJumpPacket();
            }
            //?} else {
            /*if (player.input.playerInput.jump()) {
                NetworkHandlerClient.sendHoldingJumpPacket();
            }
            *///?}
        }
    }
}
