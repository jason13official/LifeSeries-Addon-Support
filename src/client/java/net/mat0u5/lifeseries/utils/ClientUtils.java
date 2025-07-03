package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.MainClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClientUtils {

    public static boolean shouldPreventGliding() {
        if (!MainClient.preventGliding) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }

    @Nullable
    public static PlayerEntity getPlayer(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        if (client.world == null) return null;
        return client.world.getPlayerByUuid(uuid);
    }
}
