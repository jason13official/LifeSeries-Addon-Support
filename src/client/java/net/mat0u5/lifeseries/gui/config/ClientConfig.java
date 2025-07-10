package net.mat0u5.lifeseries.gui.config;

import net.mat0u5.lifeseries.config.entries.*;
import net.mat0u5.lifeseries.gui.config.entries.*;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.simple.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static void openConfig() {

        ConfigScreen.Builder builder = new ConfigScreen.Builder(MinecraftClient.getInstance().currentScreen, Text.of("Life Series Config"));

        ConfigScreen.Builder.CategoryBuilder categoryGeneral = builder.addCategory("Server");
        //TODO sub-categories for general and season specific
        List<ConfigEntry> generalEntries = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (ClientsideConfig.config.containsKey(i)) {
                ConfigObject configObject = ClientsideConfig.config.get(i);
                ConfigEntry entry = handleConfigObject(configObject);
                if (entry != null) {
                    generalEntries.add(entry);
                }
            }
            else {
                break;
            }
        }

        List<ConfigEntry> seasonSpecificEntries = new ArrayList<>();
        for (int i = 100; i < 200; i++) {
            if (ClientsideConfig.config.containsKey(i)) {
                ConfigObject configObject = ClientsideConfig.config.get(i);
                ConfigEntry entry = handleConfigObject(configObject);
                if (entry != null) {
                    seasonSpecificEntries.add(entry);
                }
            }
            else {
                break;
            }
        }

        TextConfigEntry generalGroup = new TextConfigEntry("group_general", Text.of("General Settings"), true);
        categoryGeneral.addEntry(new GroupConfigEntry<>(generalGroup, generalEntries, false, false));
        if (!seasonSpecificEntries.isEmpty()) {
            TextConfigEntry seasonSpecificGroup = new TextConfigEntry("group_season_specific", Text.of("Season Specific Settings"), true);
            categoryGeneral.addEntry(new GroupConfigEntry<>(seasonSpecificGroup, seasonSpecificEntries, false, false));
        }



        ConfigScreen.Builder.CategoryBuilder categoryClient = builder.addCategory("Client");

        GroupConfigEntry<BooleanConfigEntry> groupEntry2 = new GroupConfigEntry<>(
                new BooleanConfigEntry("textEntry2", Text.of("Da group2"),false,false),
                List.of(
                        new BooleanConfigEntry("booleanEntry1", Text.of("Boolean Entry1"), true, false),
                        new StringConfigEntry("stringEntry1", Text.of("String Entry1"), "default", "default"),
                        new IntegerConfigEntry("integerEntry1", Text.of("Integer Entry1"), 42, 0),
                        new DoubleConfigEntry("doubleEntry1", Text.of("Double Entry1"), 3.14, 0.0)
                ), true, true
        );
        GroupConfigEntry<TextConfigEntry> groupEntry = new GroupConfigEntry<>(
                new TextConfigEntry("textEntry", Text.of("Da group")),
                List.of(
                        new BooleanConfigEntry("booleanEntry", Text.of("Boolean Entry"), true, false),
                        new StringConfigEntry("stringEntry", Text.of("String Entry"), "default", "default"),
                        new IntegerConfigEntry("integerEntry", Text.of("Integer Entry"), 42, 0),
                        groupEntry2,
                        new DoubleConfigEntry("doubleEntry", Text.of("Double Entry"), 3.14, 0.0)
                ), true, true
        );

        categoryClient.addEntry(new StringConfigEntry("username", Text.of("Username"), "player", "player"));
        categoryClient.addEntry(groupEntry);
        categoryClient.addEntry(new BooleanConfigEntry("enabled", Text.of("Enabled"), true, true));

        MinecraftClient.getInstance().setScreen(builder.build());
    }

    public static ConfigEntry handleConfigObject(ConfigObject object) {
        if (object instanceof BooleanObject booleanObject) {
            return new BooleanConfigEntry(booleanObject.id, Text.of(booleanObject.name), booleanObject.booleanValue, booleanObject.defaultValue);
        }
        else if (object instanceof StringObject stringObject) {
            return new StringConfigEntry(stringObject.id, Text.of(stringObject.name), stringObject.stringValue, stringObject.defaultValue);
        }
        else if (object instanceof IntegerObject intObject) {
            return new IntegerConfigEntry(intObject.id, Text.of(intObject.name), intObject.integerValue, intObject.defaultValue);
        }
        else if (object instanceof DoubleObject doubleObject) {
            return new DoubleConfigEntry(doubleObject.id, Text.of(doubleObject.name), doubleObject.doubleValue, doubleObject.defaultValue);
        }
        else if (object instanceof TextObject textObject) {
            return new TextConfigEntry(textObject.id, Text.of(textObject.name), textObject.clickable);
        }
        return null;
    }
}
