package net.mat0u5.lifeseries.gui.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.mat0u5.lifeseries.gui.config.entries.TextFieldConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.simple.*;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Map<String, List<ConfigEntry>> categories;
    private final List<String> categoryNames;

    private ConfigListWidget listWidget;
    private ButtonWidget saveButton;
    private ButtonWidget cancelButton;
    private int selectedCategory = 0;
    private boolean hasChanges = false;

    public ConfigScreen(Screen parent, Text title, Map<String, List<ConfigEntry>> categories) {
        super(title);
        this.parent = parent;
        this.categories = categories;
        this.categoryNames = Lists.newArrayList(categories.keySet());

        this.initializeConfigEntries();
    }

    private void initializeConfigEntries() {
        for (List<ConfigEntry> entries : this.categories.values()) {
            for (ConfigEntry entry : entries) {
                entry.setScreen(this);
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int listTop = this.categoryNames.size() > 1 ? 50 : 30;

        this.listWidget = new ConfigListWidget(this.client, this.width, this.height - listTop - 30, listTop, 20);

        this.addSelectableChild(this.listWidget);
        this.addDrawableChild(this.listWidget);

        this.refreshList();

        this.saveButton = ButtonWidget.builder(Text.of("Save & Quit"), button -> this.save())
                .dimensions(this.width / 2 + 4, this.height - 24, 150, 20)
                .build();
        this.addDrawableChild(this.saveButton);

        this.cancelButton = ButtonWidget.builder(Text.of("Discard Changes"), button -> this.close())
                .dimensions(this.width / 2 - 154, this.height - 24, 150, 20)
                .build();
        this.addDrawableChild(this.cancelButton);

        this.updateButtonStates();
    }

    private void refreshList() {
        this.listWidget.clearAllEntries();
        if (this.selectedCategory < this.categoryNames.size()) {
            String categoryName = this.categoryNames.get(this.selectedCategory);
            List<ConfigEntry> entries = this.categories.get(categoryName);
            if (entries != null) {
                for (ConfigEntry entry : entries) {
                    this.listWidget.addEntry(entry);
                }
            }
        }
    }

    public void markChanged() {
        this.hasChanges = true;
        this.updateButtonStates();
    }

    private void updateButtonStates() {
        this.saveButton.active = this.hasChanges && !this.hasErrors();
    }

    private boolean hasErrors() {
        for (List<ConfigEntry> entries : this.categories.values()) {
            for (ConfigEntry entry : entries) {
                if (entry.hasError()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void save() {

        for (Map.Entry<String, List<ConfigEntry>> category : this.categories.entrySet()) {
            if (category.getKey().equals("Client")) continue;
            for (ConfigEntry entry : category.getValue()) {
                if (!entry.modified()) continue;

                NetworkHandlerClient.sendConfigUpdate(
                        entry.getValueType(),
                        entry.getFieldName(),
                        List.of(entry.getValueAsString())
                );
            }
        }

        this.client.setScreen(this.parent);
    }

    @Override
    public void close() {
        if (this.hasChanges) {
            this.client.setScreen(new ConfirmScreen(
                    confirmed -> {
                        if (confirmed) {
                            this.client.setScreen(this.parent);
                        } else {
                            this.client.setScreen(this);
                        }
                    },
                    Text.of("Changes Not Saved"),
                    Text.of("Are you sure you want to quit editing the config? Changes will not be saved!"),
                    Text.of("Quit & Discard Changes"),
                    Text.of("Cancel")
            ));
        } else {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        //this.renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);

        if (this.categoryNames.size() > 1) {
            this.renderCategoryTabs(context, mouseX, mouseY);
        }

        if (this.hasErrors()) {
            context.drawTextWithShadow(this.textRenderer, Text.of("Errors"), 10, 20, 0xFF5555);
        }

    }

    private void renderCategoryTabs(DrawContext context, int mouseX, int mouseY) {
        int tabWidth = Math.min(130, this.width / this.categoryNames.size());
        int startX = (this.width - ((tabWidth+5) * this.categoryNames.size())) / 2;

        for (int i = 0; i < this.categoryNames.size(); i++) {
            int tabX = startX + i * (tabWidth+5);
            int tabY = 24;
            int tabHeight = 20;

            boolean isSelected = i == this.selectedCategory;
            boolean isHovered = mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + tabHeight;

            int color = isSelected ? 0x80FFFFFF : (isHovered ? 0x40FFFFFF : 0x20FFFFFF);
            context.fill(tabX, tabY, tabX + tabWidth, tabY + tabHeight, color);

            String categoryName = this.categoryNames.get(i);
            int textColor = isSelected ? 0xFFFFFF : 0xCCCCCC;
            context.drawCenteredTextWithShadow(this.textRenderer, categoryName, tabX + tabWidth / 2, tabY + 6, textColor);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.categoryNames.size() > 1 && button == 0) {
            int tabWidth = Math.min(130, this.width / this.categoryNames.size());
            int startX = (this.width - (tabWidth * this.categoryNames.size())) / 2;

            for (int i = 0; i < this.categoryNames.size(); i++) {
                int tabX = startX + i * tabWidth;
                int tabY = 24;
                int tabHeight = 20;

                if (mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + tabHeight) {
                    this.selectedCategory = i;
                    this.refreshList();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public net.minecraft.client.font.TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    public void unfocusTextEntries() {
        for (ConfigListWidget.ConfigEntryWidget entry : this.listWidget.children()) {
            if (entry.getConfigEntry() instanceof TextFieldConfigEntry textFieldEntry) {
                textFieldEntry.setFocused(false);
            }
        }
    }

    public static class Builder {
        private final Screen parent;
        private final Text title;
        private final Map<String, List<ConfigEntry>> categories = Maps.newLinkedHashMap();

        public Builder(Screen parent, Text title) {
            this.parent = parent;
            this.title = title;
        }

        public CategoryBuilder addCategory(String name) {
            this.categories.put(name, Lists.newArrayList());
            return new CategoryBuilder(this, name);
        }

        public ConfigScreen build() {
            return new ConfigScreen(this.parent, this.title, this.categories);
        }

        public static class CategoryBuilder {
            private final Builder parent;
            private final String categoryName;

            public CategoryBuilder(Builder parent, String categoryName) {
                this.parent = parent;
                this.categoryName = categoryName;
            }

            public CategoryBuilder addString(String fieldName, Text displayName, String value, String defaultValue) {
                this.parent.categories.get(this.categoryName).add(new StringConfigEntry(fieldName, displayName, value, defaultValue));
                return this;
            }

            public CategoryBuilder addBoolean(String fieldName, Text displayName, boolean value, boolean defaultValue) {
                this.parent.categories.get(this.categoryName).add(new BooleanConfigEntry(fieldName, displayName, value, defaultValue));
                return this;
            }

            public CategoryBuilder addInteger(String fieldName, Text displayName, int value, int defaultValue, int min, int max) {
                this.parent.categories.get(this.categoryName).add(new IntegerConfigEntry(fieldName, displayName, value, defaultValue, min, max));
                return this;
            }

            public CategoryBuilder addDouble(String fieldName, Text displayName, double value, double defaultValue, double min, double max) {
                this.parent.categories.get(this.categoryName).add(new DoubleConfigEntry(fieldName, displayName, value, defaultValue, min, max));
                return this;
            }

            public CategoryBuilder addFloat(String fieldName, Text displayName, float value, float defaultValue, float min, float max) {
                this.parent.categories.get(this.categoryName).add(new FloatConfigEntry(fieldName, displayName, value, defaultValue, min, max));
                return this;
            }

            public CategoryBuilder addInteger(String fieldName, Text displayName, int value, int defaultValue) {
                this.parent.categories.get(this.categoryName).add(new IntegerConfigEntry(fieldName, displayName, value, defaultValue, null, null));
                return this;
            }

            public CategoryBuilder addDouble(String fieldName, Text displayName, double value, double defaultValue) {
                this.parent.categories.get(this.categoryName).add(new DoubleConfigEntry(fieldName, displayName, value, defaultValue, null, null));
                return this;
            }

            public CategoryBuilder addFloat(String fieldName, Text displayName, float value, float defaultValue) {
                this.parent.categories.get(this.categoryName).add(new FloatConfigEntry(fieldName, displayName, value, defaultValue, null, null));
                return this;
            }

            public Builder endCategory() {
                return this.parent;
            }
        }
    }
}