package net.mat0u5.lifeseries.gui.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientConfig {
    public static void openConfig() {
        ModernConfigScreen.Builder builder = new ModernConfigScreen.Builder(MinecraftClient.getInstance().currentScreen, Text.of("My Config"));
        ModernConfigScreen.Builder.CategoryBuilder categoryGeneral = builder.addCategory("General");
        categoryGeneral.addString("username", Text.of("Username"), "player");
        categoryGeneral.addBoolean("enabled", Text.of("Enabled"), true);
        categoryGeneral.addInteger("count", Text.of("Count"), 10, 1, 100);
        categoryGeneral.endCategory();

        ModernConfigScreen.Builder.CategoryBuilder categoryClient = builder.addCategory("Client");
        categoryClient.addString("usernamee", Text.of("1Username"), "player");
        categoryClient.addBoolean("enabledd", Text.of("1Enabled"), true);
        categoryClient.addInteger("countt", Text.of("1Count"), 10, 1, 100);
        categoryClient.endCategory();


        builder.setOnSave(configData -> {
            System.out.println("Config saved: " + configData);
        });

        MinecraftClient.getInstance().setScreen(builder.build());
    }
}
