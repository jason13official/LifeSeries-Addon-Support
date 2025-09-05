package com.cursee.ls_addon_support.gui.config.entries.extra;

import com.cursee.ls_addon_support.gui.config.entries.main.StringConfigEntry;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class EnchantListConfigEntry extends StringConfigEntry {

  public EnchantListConfigEntry(String fieldName, String displayName, String description,
      String value, String defaultValue) {
    super(fieldName, displayName, description, value, defaultValue);
  }

  @Override
  protected void onTextChanged(String text) {
    super.onTextChanged(text);
    reloadEntriesRaw(text);
  }

  protected void reloadEntriesRaw(String text) {
    String raw = text;
    raw = raw.replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "");
    List<String> items = new ArrayList<>(Arrays.asList(raw.split(",")));
    reloadEntries(items);
  }

  protected void reloadEntries(List<String> items) {
      if (MinecraftClient.getInstance().world == null) {
          return;
      }

    List<RegistryKey<Enchantment>> newList = new ArrayList<>();
    boolean errors = false;

    Registry<Enchantment> enchantmentRegistry = MinecraftClient.getInstance().world.getRegistryManager()

        .getOrThrow(RegistryKey.ofRegistry(Identifier.of("minecraft", "enchantment")));

    for (String enchantmentId : items) {
        if (enchantmentId.isEmpty()) {
            continue;
        }
        if (!enchantmentId.contains(":")) {
            enchantmentId = "minecraft:" + enchantmentId;
        }

      try {
        Identifier id = Identifier.of(enchantmentId);
        Enchantment enchantment = enchantmentRegistry.get(id);

        if (enchantment != null) {
          newList.add(enchantmentRegistry.getKey(enchantment).orElseThrow());
        } else {
          setError(TextUtils.formatString("Invalid enchantment: '{}'", enchantmentId));
          errors = true;
        }
      } catch (Exception e) {
        setError(TextUtils.formatString("Error parsing enchantment ID: '{}'", enchantmentId));
        errors = true;
      }
    }

    if (!errors) {
      clearError();
    }
  }

  @Override
  public boolean hasCustomErrors() {
    return true;
  }

  @Override
  public ConfigTypes getValueType() {
    return ConfigTypes.ENCHANT_LIST;
  }
}
