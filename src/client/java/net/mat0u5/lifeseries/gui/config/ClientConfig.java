package net.mat0u5.lifeseries.gui.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientConfig {
    public static void openConfig() {
        ModernConfigScreen.Builder builder = new ModernConfigScreen.Builder(MinecraftClient.getInstance().currentScreen, Text.of("My Config"));

        ModernConfigScreen.Builder.CategoryBuilder categoryGeneral = builder.addCategory("Server");
        categoryGeneral.addString("username", Text.of("Username"), "player", "player");
        categoryGeneral.addBoolean("enabled", Text.of("Enabled"), true, true);
        categoryGeneral.addInteger("count", Text.of("Count int"), 5, 10, 1, 100);
        categoryGeneral.addDouble("count", Text.of("Count double"), 1, 0.5, 0, 1);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryGeneral.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);

        ModernConfigScreen.Builder.CategoryBuilder categoryClient = builder.addCategory("Client");
        categoryClient.addString("usernamee", Text.of("1Username"), "Mat0u5", "player");
        categoryClient.addBoolean("enabledd", Text.of("1Enabled"), true, false);
        categoryClient.addInteger("countt", Text.of("1Count"), 10, 10, 1, 100);

        builder.setOnSave(configData -> {
            System.out.println("Config saved: " + configData);
        });

        MinecraftClient.getInstance().setScreen(builder.build());
    }
}
