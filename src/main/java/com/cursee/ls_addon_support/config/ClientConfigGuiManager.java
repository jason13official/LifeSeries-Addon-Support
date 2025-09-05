package com.cursee.ls_addon_support.config;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.entries.BooleanObject;
import com.cursee.ls_addon_support.config.entries.ConfigObject;
import com.cursee.ls_addon_support.config.entries.DoubleObject;
import com.cursee.ls_addon_support.config.entries.IntegerObject;
import com.cursee.ls_addon_support.config.entries.StringObject;
import com.cursee.ls_addon_support.config.entries.TextObject;
import com.cursee.ls_addon_support.gui.config.ConfigScreen;
import com.cursee.ls_addon_support.gui.config.entries.ConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.GroupConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.NumberConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.BlockListConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.BoogeymanConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.EffectListConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.EnchantListConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.HeartsConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.ItemListConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.PercentageConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.StringListConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.time.MinutesConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.extra.time.SecondsConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.main.BooleanConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.main.DoubleConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.main.IntegerConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.main.StringConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.main.TextConfigEntry;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.interfaces.IEntryGroupHeader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientConfigGuiManager {

  public static void openConfig() {
    ConfigScreen.Builder builder = new ConfigScreen.Builder(
        MinecraftClient.getInstance().currentScreen, Text.of("Life Series Config"));
    if (!ClientConfigNetwork.configObjects.isEmpty()) {
      ConfigScreen.Builder.CategoryBuilder categoryGeneral = builder.addCategory("Server");
      addConfig(categoryGeneral, ClientConfigNetwork.configObjects);
    }

    ConfigScreen.Builder.CategoryBuilder categoryClient = builder.addCategory("Client");
    addConfig(categoryClient, ClientConfigNetwork.clientConfigObjects);

    if (LSAddonSupport.DEBUG) {
      addTestingCategory(builder);
    }

    MinecraftClient.getInstance().setScreen(builder.build());
  }

  public static void addConfig(ConfigScreen.Builder.CategoryBuilder category,
      Map<Integer, ConfigObject> allConfigObjects) {
    if (allConfigObjects.isEmpty()) {
      return;
    }
    Map<String, GroupConfigEntry<?>> groupEntries = new HashMap<>();
    Map<String, String> groupModifiers = new HashMap<>();

    for (Map.Entry<Integer, ConfigObject> entry : allConfigObjects.entrySet()) {
      ConfigObject configObject = entry.getValue();
      String groupInfo = configObject.getGroupInfo();
      ConfigEntry configEntry = handleConfigObject(configObject);
      if (configEntry == null) {
        LSAddonSupport.LOGGER.error("Config entry is null");
        continue;
      }

      if (groupInfo.startsWith("{") && groupInfo.contains("}")) {
        String groupPath = groupInfo.substring(1);
        String[] split = groupPath.split("}");
        groupPath = split[0];

        if (split.length >= 2) {
          groupModifiers.put(groupPath, split[1]);
        }

        GroupConfigEntry<?> groupEntry = createGroupEntry(configEntry,
            groupModifiers.get(groupPath));
        if (groupEntry != null) {
          groupEntries.put(groupPath, groupEntry);

          addToParentOrCategory(groupPath, groupEntry, groupEntries, category);
        }
      } else {
        if (groupInfo.isEmpty()) {
          category.addEntry(configEntry);
        } else {
          addToGroup(groupInfo, configEntry, groupEntries, category);
        }
      }
    }
  }

  private static void addToParentOrCategory(String groupPath, GroupConfigEntry<?> groupEntry,
      Map<String, GroupConfigEntry<?>> groupEntries,
      ConfigScreen.Builder.CategoryBuilder category) {
    int lastDotIndex = groupPath.lastIndexOf('.');
    if (lastDotIndex != -1) {
      String parentPath = groupPath.substring(0, lastDotIndex);
      GroupConfigEntry<?> parentGroup = groupEntries.get(parentPath);
      if (parentGroup != null) {
        parentGroup.addChildEntry(groupEntry);
      } else {
        category.addEntry(groupEntry);
      }
    } else {
      category.addEntry(groupEntry);
    }
  }

  private static void addToGroup(String groupInfo, ConfigEntry configEntry,
      Map<String, GroupConfigEntry<?>> groupEntries,
      ConfigScreen.Builder.CategoryBuilder category) {
    GroupConfigEntry<?> targetGroup = groupEntries.get(groupInfo);
    if (targetGroup != null) {
      targetGroup.addChildEntry(configEntry);
    } else {
      category.addEntry(configEntry);
    }
  }

  private static GroupConfigEntry<?> createGroupEntry(ConfigEntry configEntry, String modifier) {
    if (!(configEntry instanceof IEntryGroupHeader)) {
      LSAddonSupport.LOGGER.error("Warning: Group entry does not implement IEntryGroupHeader");
      return null;
    }

    boolean showSidebar = true;
    boolean openByDefault = true;

    if (modifier != null) {
      if (modifier.contains("no_sidebar")) {
        showSidebar = false;
      }
      if (modifier.contains("closed")) {
        openByDefault = false;
      }
    }

    if (configEntry instanceof TextConfigEntry textConfigEntry) {
      return new GroupConfigEntry<>(textConfigEntry, new ArrayList<>(), showSidebar, openByDefault);
    }
    if (configEntry instanceof BooleanConfigEntry booleanConfigEntry) {
      return new GroupConfigEntry<>(booleanConfigEntry, new ArrayList<>(), showSidebar,
          openByDefault);
    }
    if (configEntry instanceof NumberConfigEntry<?> numberConfigEntry) {
      return new GroupConfigEntry<>(numberConfigEntry, new ArrayList<>(), showSidebar,
          openByDefault);
    }

    return null;
  }

  public static ConfigEntry handleConfigObject(ConfigObject object) {
    if (object instanceof BooleanObject booleanObject) {
      if (booleanObject.configType == ConfigTypes.BOOGEYMAN) {
        return new BoogeymanConfigEntry(booleanObject.id, booleanObject.name,
            booleanObject.description, booleanObject.booleanValue, booleanObject.defaultValue);
      }
      return new BooleanConfigEntry(booleanObject.id, booleanObject.name, booleanObject.description,
          booleanObject.booleanValue, booleanObject.defaultValue);
    } else if (object instanceof StringObject stringObject) {
      if (stringObject.configType == ConfigTypes.ITEM_LIST) {
        return new ItemListConfigEntry(stringObject.id, stringObject.name, stringObject.description,
            stringObject.stringValue, stringObject.defaultValue);
      } else if (stringObject.configType == ConfigTypes.BLOCK_LIST) {
        return new BlockListConfigEntry(stringObject.id, stringObject.name,
            stringObject.description, stringObject.stringValue, stringObject.defaultValue);
      } else if (stringObject.configType == ConfigTypes.EFFECT_LIST) {
        return new EffectListConfigEntry(stringObject.id, stringObject.name,
            stringObject.description, stringObject.stringValue, stringObject.defaultValue);
      } else if (stringObject.configType == ConfigTypes.ENCHANT_LIST) {
        return new EnchantListConfigEntry(stringObject.id, stringObject.name,
            stringObject.description, stringObject.stringValue, stringObject.defaultValue);
      } else if (stringObject.configType == ConfigTypes.STRING_LIST) {
        List<String> args = new ArrayList<>(stringObject.args);
        if (args.size() >= 3) {
          args.removeFirst();
          args.removeFirst();
          args.removeFirst();
          return new StringListConfigEntry(stringObject.id, stringObject.name,
              stringObject.description, stringObject.stringValue, stringObject.defaultValue, args);
        }
      }
      return new StringConfigEntry(stringObject.id, stringObject.name, stringObject.description,
          stringObject.stringValue, stringObject.defaultValue);
    } else if (object instanceof IntegerObject intObject) {
      if (intObject.configType == ConfigTypes.HEARTS) {
        return new HeartsConfigEntry(intObject.id, intObject.name, intObject.description,
            intObject.integerValue, intObject.defaultValue);
      }
      if (intObject.configType == ConfigTypes.SECONDS) {
        return new SecondsConfigEntry(intObject.id, intObject.name, intObject.description,
            intObject.integerValue, intObject.defaultValue);
      }
      return new IntegerConfigEntry(intObject.id, intObject.name, intObject.description,
          intObject.integerValue, intObject.defaultValue);
    } else if (object instanceof DoubleObject doubleObject) {
      if (doubleObject.configType == ConfigTypes.PERCENTAGE) {
        return new PercentageConfigEntry(doubleObject.id, doubleObject.name,
            doubleObject.description, doubleObject.doubleValue, doubleObject.defaultValue);
      }
      if (doubleObject.configType == ConfigTypes.MINUTES) {
        return new MinutesConfigEntry(doubleObject.id, doubleObject.name, doubleObject.description,
            doubleObject.doubleValue, doubleObject.defaultValue);
      }
      return new DoubleConfigEntry(doubleObject.id, doubleObject.name, doubleObject.description,
          doubleObject.doubleValue, doubleObject.defaultValue);
    } else if (object instanceof TextObject textObject) {
      return new TextConfigEntry(textObject.id, textObject.name, textObject.description,
          textObject.clickable);
    }
    return null;
  }

  public static void addTestingCategory(ConfigScreen.Builder builder) {
    ConfigScreen.Builder.CategoryBuilder category = builder.addCategory("Testing");
    category.addEntry(new TextConfigEntry("", "Test", ""));
    //category.addEntry(new EnhancedStringConfigEntry("id","EnhancedString","description","This mod is a one-to-one recreation of Grian's Life Series in minecraft Fabric",""));
    category.addEntry(new PercentageConfigEntry("id2", "Percentage", "description2", 0.5, 0.5));
    category.addEntry(new HeartsConfigEntry("id3", "Hearts", "description3", 20, 20));
    //category.addEntry();
  }
}