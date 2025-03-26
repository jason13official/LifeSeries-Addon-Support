package net.mat0u5.lifeseries.client;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.ItemStackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class ClientUtils {
    public static boolean shouldPreventGliding() {
        if (!MainClient.preventGliding) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        ItemStack helmet = client.player.getInventory().getArmorStack(3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }
}
