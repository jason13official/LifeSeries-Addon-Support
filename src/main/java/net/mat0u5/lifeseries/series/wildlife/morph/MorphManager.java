package net.mat0u5.lifeseries.series.wildlife.morph;

import net.mat0u5.lifeseries.client.ClientUtils;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MorphManager {
    private static final Map<UUID, MorphComponent> morphComponents = new HashMap<>();

    public static MorphComponent getOrCreateComponent(PlayerEntity player) {
        UUID playerId = player.getUuid();
        return morphComponents.computeIfAbsent(playerId, k -> new MorphComponent(player));
    }

    public static void removeComponent(ServerPlayerEntity player) {
        morphComponents.remove(player.getUuid());
        syncFromPlayer(player);
    }

    @Nullable
    public static MorphComponent getComponent(PlayerEntity player) {
        return morphComponents.get(player.getUuid());
    }

    public static boolean hasComponent(PlayerEntity player) {
        return morphComponents.containsKey(player.getUuid());
    }

    public static void setMorph(PlayerEntity player, EntityType<?> morph) {
        MorphComponent component = getOrCreateComponent(player);
        component.setMorph(morph);
        syncFromPlayer(player);
    }

    public static void resetMorph(PlayerEntity player) {
        setMorph(player, null);
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {
        getOrCreateComponent(player);
    }

    public static void onPlayerDisconnect(ServerPlayerEntity player) {
        removeComponent(player);
    }

    public static void clientTick(PlayerEntity player) {
        MorphComponent component = getComponent(player);
        if (component != null) {
            component.clientTick();
        }
    }

    public static void syncFromPlayer(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        MorphComponent component = getComponent(serverPlayer);
        String typeStr = "null";
        if (component != null) typeStr = component.getTypeAsString();
        NetworkHandlerServer.sendStringListPackets("morph", List.of(serverPlayer.getUuidAsString(), typeStr));
    }

    public static void syncToPlayer(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        for (ServerPlayerEntity otherPlayer : PlayerUtils.getAllPlayers()) {
            MorphComponent component = getOrCreateComponent(otherPlayer);
            String typeStr = component.getTypeAsString();
            NetworkHandlerServer.sendStringListPacket(serverPlayer, "morph", List.of(otherPlayer.getUuidAsString(), typeStr));
        }
    }

    public static void setFromPacket(UUID uuid, EntityType<?> morph) {
        PlayerEntity player = ClientUtils.getPlayer(uuid);
        if (player != null) {
            MorphComponent component = getOrCreateComponent(player);
            component.setMorph(morph);
        }
    }

    /*
    private void loadFromPlayer() {
        if (player == null) return;
        NbtCompound playerData = ((IEntityDataSaver) player).getPersistentData();
        if (playerData.contains(MORPH_NBT_KEY)) {
            NbtCompound morphData = playerData.getCompound(MORPH_NBT_KEY);
            if (morphData.contains(TYPE_KEY)) {
                morph = Registries.ENTITY_TYPE.get(Identifier.of(morphData.getString(TYPE_KEY)));
            }
        }
    }

    private void saveToPlayer() {
        if (player == null) return;
        NbtCompound playerData = ((IEntityDataSaver) player).getPersistentData();
        NbtCompound morphData = new NbtCompound();

        if (morph != null) {
            morphData.putString(TYPE_KEY, Registries.ENTITY_TYPE.getId(morph).toString());
        }

        playerData.put(MORPH_NBT_KEY, morphData);
    }
    */
}