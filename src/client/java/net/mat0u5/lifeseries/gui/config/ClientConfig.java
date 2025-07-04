package net.mat0u5.lifeseries.gui.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientConfig {
    public static void openConfig() {
        ModernConfigScreen configScreen = new ModernConfigScreen.Builder(MinecraftClient.getInstance().currentScreen, Text.of("My Config"))
                .addCategory("General")
                .addString("username", Text.of("Username"), "player")
                .addBoolean("enabled", Text.of("Enabled"), true)
                .addInteger("count", Text.of("Count"), 10, 1, 100)
                .endCategory()
                .setOnSave(configData -> {
                    System.out.println("Config saved: " + configData);
                })
                .build();

        MinecraftClient.getInstance().setScreen(configScreen);
    }
}
