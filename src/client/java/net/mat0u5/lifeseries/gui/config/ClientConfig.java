package net.mat0u5.lifeseries.gui.config;

import net.mat0u5.lifeseries.config.*;
import net.mat0u5.lifeseries.gui.config.entries.GroupConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.simple.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

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

        GroupConfigEntry groupEntry = new GroupConfigEntry(
                new TextConfigEntry("textEntry", Text.of("Da group")),
                List.of(
                        new BooleanConfigEntry("booleanEntry", Text.of("Boolean Entry"), true, false),
                        new StringConfigEntry("stringEntry", Text.of("String Entry"), "default", "default"),
                        new IntegerConfigEntry("integerEntry", Text.of("Integer Entry"), 42, 0),
                        new DoubleConfigEntry("doubleEntry", Text.of("Double Entry"), 3.14, 0.0)
                )
        );
        GroupConfigEntry groupEntry2 = new GroupConfigEntry(
                new BooleanConfigEntry("textEntry", Text.of("Da group2"),false,false),
                List.of(
                        new BooleanConfigEntry("booleanEntry1", Text.of("Boolean Entry1"), true, false),
                        new StringConfigEntry("stringEntry1", Text.of("String Entry1"), "default", "default"),
                        new IntegerConfigEntry("integerEntry1", Text.of("Integer Entry1"), 42, 0),
                        new DoubleConfigEntry("doubleEntry1", Text.of("Double Entry1"), 3.14, 0.0)
                )
        );

        categoryClient.addEntry(new StringConfigEntry("username", Text.of("Username"), "player", "player"));
        categoryClient.addEntry(groupEntry);
        categoryClient.addEntry(new BooleanConfigEntry("enabled", Text.of("Enabled"), true, true));
        categoryClient.addEntry(groupEntry2);
        categoryClient.addEntry(new BooleanConfigEntry("enabled", Text.of("Disabled"), false, false));

        /*
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
         */

        MinecraftClient.getInstance().setScreen(builder.build());
    }

    public static void handleConfigObject(ConfigScreen.Builder.CategoryBuilder category, ConfigObject object) {
        if (object instanceof BooleanObject booleanObject) {
            category.addEntry(new BooleanConfigEntry(booleanObject.id, Text.of(booleanObject.name), booleanObject.booleanValue, booleanObject.defaultValue));
        }
        else if (object instanceof StringObject stringObject) {
            category.addEntry(new StringConfigEntry(stringObject.id, Text.of(stringObject.name), stringObject.stringValue, stringObject.defaultValue));
        }
        else if (object instanceof IntegerObject intObject) {
            category.addEntry(new IntegerConfigEntry(intObject.id, Text.of(intObject.name), intObject.integerValue, intObject.defaultValue));
        }
        else if (object instanceof DoubleObject doubleObject) {
            category.addEntry(new DoubleConfigEntry(doubleObject.id, Text.of(doubleObject.name), doubleObject.doubleValue, doubleObject.defaultValue));
        }
    }
}
