package net.mat0u5.lifeseries.gui.config;

import net.mat0u5.lifeseries.config.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientConfig {
    public static void openConfig() {

        ConfigScreen.Builder builder = new ConfigScreen.Builder(MinecraftClient.getInstance().currentScreen, Text.of("Life Series Config"));

        ConfigScreen.Builder.CategoryBuilder categoryGeneral = builder.addCategory("Server");
        //TODO sub-categories for general and season specific

        for (int i = 0; i < 100; i++) {
            if (ClientsideConfig.config.containsKey(i)) {
                ConfigObject configObject = ClientsideConfig.config.get(i);
                handleConfigObject(categoryGeneral, configObject);
            }
            else {
                break;
            }
        }
        boolean anyInSeasonSpecificCategory = false;
        for (int i = 100; i < 200; i++) {
            if (ClientsideConfig.config.containsKey(i)) {
                ConfigObject configObject = ClientsideConfig.config.get(i);
                handleConfigObject(categoryGeneral, configObject);
                anyInSeasonSpecificCategory = true;
            }
            else {
                break;
            }
        }

        ConfigScreen.Builder.CategoryBuilder categoryClient = builder.addCategory("Client");
        categoryClient.addString("username", Text.of("Username"), "player", "player");
        categoryClient.addBoolean("enabled", Text.of("Enabled"), true, true);
        categoryClient.addInteger("count", Text.of("Count int"), 5, 10, 1, 100);
        categoryClient.addDouble("count", Text.of("Count double"), 1, 0.5, 0, 1);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);
        categoryClient.addFloat("count", Text.of("Count float"), 1f, 1f, 0, 100);

        MinecraftClient.getInstance().setScreen(builder.build());
    }

    public static void handleConfigObject(ConfigScreen.Builder.CategoryBuilder category, ConfigObject object) {
        if (object instanceof BooleanObject booleanObject) {
            category.addBoolean(booleanObject.id, Text.of(booleanObject.name), booleanObject.booleanValue, booleanObject.defaultValue);
        }
        else if (object instanceof StringObject stringObject) {
            category.addString(stringObject.id, Text.of(stringObject.name), stringObject.stringValue, stringObject.defaultValue);
        }
        else if (object instanceof IntegerObject intObject) {
            category.addInteger(intObject.id, Text.of(intObject.name), intObject.integerValue, intObject.defaultValue);
        }
        else if (object instanceof DoubleObject doubleObject) {
            category.addDouble(doubleObject.id, Text.of(doubleObject.name), doubleObject.doubleValue, doubleObject.defaultValue);
        }
    }
}
