package net.mat0u5.lifeseries.gui.other;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.config.entries.*;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class OldConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Current Season Config"));

        ConfigCategory category = builder.getOrCreateCategory(Text.of(Seasons.getFormattedStringNameFromSeason(MainClient.clientCurrentSeason)));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        SubCategoryBuilder generalCategory = entryBuilder.startSubCategory(Text.of("General Settings"));
        SubCategoryBuilder seasonSpecificCategory = entryBuilder.startSubCategory(Text.of("Season Specific Settings"));


        for (int i = 0; i < 100; i++) {
            if (ClientsideConfig.config.containsKey(i)) {
                ConfigObject configObject = ClientsideConfig.config.get(i);
                handleConfigObject(generalCategory, entryBuilder, configObject);
            }
            else {
                break;
            }
        }
        boolean anyInSeasonSpecificCategory = false;
        for (int i = 100; i < 200; i++) {
            if (ClientsideConfig.config.containsKey(i)) {
                ConfigObject configObject = ClientsideConfig.config.get(i);
                handleConfigObject(seasonSpecificCategory, entryBuilder, configObject);
                anyInSeasonSpecificCategory = true;
            }
            else {
                break;
            }
        }

        category.addEntry(
                generalCategory.build()
        );
        if (anyInSeasonSpecificCategory) {
            category.addEntry(
                    seasonSpecificCategory.build()
            );
        }


        builder.setSavingRunnable(ClientsideConfig::save);

        return builder.build();
    }

    public static void handleConfigObject(SubCategoryBuilder subCategory, ConfigEntryBuilder entryBuilder, ConfigObject object) {
        if (object instanceof BooleanObject booleanObject) {
            subCategory.add(entryBuilder.startBooleanToggle(Text.of(booleanObject.name), booleanObject.booleanValue)
                    .setDefaultValue(booleanObject.defaultValue)
                    .setTooltip(Text.of(booleanObject.description))
                    .setSaveConsumer(booleanObject::updateValue)
                    .build());
        }
        else if (object instanceof StringObject stringObject) {
            subCategory.add(entryBuilder.startStrField(Text.of(stringObject.name), stringObject.stringValue)
                    .setDefaultValue(stringObject.defaultValue)
                    .setTooltip(Text.of(stringObject.description))
                    .setSaveConsumer(stringObject::updateValue)
                    .build());
        }
        else if (object instanceof IntegerObject intObject) {
            subCategory.add(entryBuilder.startIntField(Text.of(intObject.name), intObject.integerValue)
                    .setDefaultValue(intObject.defaultValue)
                    .setTooltip(Text.of(intObject.description))
                    .setSaveConsumer(intObject::updateValue)
                    .build());
        }
        else if (object instanceof DoubleObject doubleObject) {
            subCategory.add(entryBuilder.startDoubleField(Text.of(doubleObject.name), doubleObject.doubleValue)
                    .setDefaultValue(doubleObject.defaultValue)
                    .setTooltip(Text.of(doubleObject.description))
                    .setSaveConsumer(doubleObject::updateValue)
                    .build());
        }
    }
}
