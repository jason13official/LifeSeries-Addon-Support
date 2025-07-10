package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.entries.*;
import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.gui.config.entries.*;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.simple.*;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.*;

public class ClientConfig {
    public static void openConfig() {
        ConfigScreen.Builder builder = new ConfigScreen.Builder(MinecraftClient.getInstance().currentScreen, Text.of("Life Series Config"));
        ConfigScreen.Builder.CategoryBuilder categoryGeneral = builder.addCategory("Server");
        addServerConfig(categoryGeneral);

        ConfigScreen.Builder.CategoryBuilder categoryClient = builder.addCategory("Client");
        addClientConfig(categoryClient);

        MinecraftClient.getInstance().setScreen(builder.build());
    }

    public static void addServerConfig(ConfigScreen.Builder.CategoryBuilder category) {
        Map<String, ConfigEntry> groupEntries = new HashMap<>();
        Map<String, String> groupModifiers = new HashMap<>();
        Map<String, List<ConfigEntry>> groupChildren = new HashMap<>();

        for (Map.Entry<Integer, ConfigObject> entry : ClientConfigNetwork.groupConfigObjects.entrySet()) {
            ConfigObject configObject = entry.getValue();
            String groupInfo = configObject.getGroupInfo();

            if (groupInfo.startsWith("{") && groupInfo.contains("}")) {
                String groupPath = groupInfo.substring(1);
                String[] split = groupPath.split("}");
                groupPath = split[0];
                ConfigEntry configEntry = handleConfigObject(configObject);
                if (configEntry != null) {
                    groupEntries.put(groupPath, configEntry);
                    if (split.length >= 2) {
                        groupModifiers.put(groupPath, split[1]);
                    }
                    groupChildren.put(groupPath, new ArrayList<>());
                }
            }
        }

        for (Map.Entry<Integer, ConfigObject> entry : ClientConfigNetwork.configObjects.entrySet()) {
            ConfigObject configObject = entry.getValue();
            String groupInfo = configObject.getGroupInfo();
            ConfigEntry configEntry = handleConfigObject(configObject);

            if (configEntry != null) {
                if (groupInfo == null || groupInfo.isEmpty()) {
                    category.addEntry(configEntry);
                } else {
                    if (groupChildren.containsKey(groupInfo)) {
                        groupChildren.get(groupInfo).add(configEntry);
                    } else {
                        category.addEntry(configEntry);
                    }
                }
            }
        }

        List<String> sortedGroupPaths = new ArrayList<>(groupEntries.keySet());
        sortedGroupPaths.sort(String::compareTo);

        Set<String> processedGroups = new HashSet<>();

        for (String groupPath : sortedGroupPaths) {
            if (processedGroups.contains(groupPath)) {
                continue;
            }

            ConfigEntry groupEntry = createGroupHierarchy(groupPath, groupEntries, groupModifiers, groupChildren, processedGroups);
            if (groupEntry != null) {
                category.addEntry(groupEntry);
            }
        }
    }

    public static void addClientConfig(ConfigScreen.Builder.CategoryBuilder category) {
        category.addEntry(new TextConfigEntry("empty", Text.of("Hmmm so empty, huh?"), false));
    }

    private static ConfigEntry createGroupHierarchy(String groupPath,
                                                    Map<String, ConfigEntry> groupEntries,
                                                    Map<String, String> groupModifiers,
                                                    Map<String, List<ConfigEntry>> groupChildren,
                                                    Set<String> processedGroups) {

        ConfigEntry mainEntry = groupEntries.get(groupPath);
        List<ConfigEntry> children = new ArrayList<>(groupChildren.get(groupPath));

        String groupPrefix = groupPath + ".";
        for (String otherPath : groupEntries.keySet()) {
            if (otherPath.startsWith(groupPrefix) && !processedGroups.contains(otherPath)) {
                String remainder = otherPath.substring(groupPrefix.length());
                if (!remainder.contains(".")) {
                    ConfigEntry subGroup = createGroupHierarchy(otherPath, groupEntries, groupModifiers, groupChildren, processedGroups);
                    if (subGroup != null) {
                        children.add(subGroup);
                    }
                    processedGroups.add(otherPath);
                }
            }
        }

        processedGroups.add(groupPath);

        if (mainEntry instanceof IEntryGroupHeader) {
            if (!children.isEmpty()) {
                boolean showSidebar = true;
                boolean openByDefault = true;

                if (groupModifiers.containsKey(groupPath)) {
                    String modifier = groupModifiers.get(groupPath);
                    if (modifier.contains("no_sidebar")) {
                        showSidebar = false;
                    }
                    if (modifier.contains("closed")) {
                        openByDefault = false;
                    }
                }

                if (mainEntry instanceof TextConfigEntry textConfigEntry) {
                    return new GroupConfigEntry<>(textConfigEntry, children, showSidebar, openByDefault);
                }
                if (mainEntry instanceof BooleanConfigEntry booleanConfigEntry) {
                    return new GroupConfigEntry<>(booleanConfigEntry, children, showSidebar, openByDefault);
                }

            } else {
                return mainEntry;
            }
        } else {
            if (!children.isEmpty()) {
                Main.LOGGER.error("Warning: Group entry for '" + groupPath + "' does not implement IEntryGroupHeader but has children");
                return null;
            } else {
                return mainEntry;
            }
        }
        return null;
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
