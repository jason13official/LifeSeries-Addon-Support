package net.mat0u5.lifeseries.client;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.ItemStackUtils;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class ClientUtils {
    public static boolean shouldPreventGliding() {
        if (!MainClient.preventGliding) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }
}
