package net.mat0u5.lifeseries.gui.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.mat0u5.lifeseries.gui.config.entries.BooleanConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.IntegerConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.StringConfigEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ModernConfigScreen extends Screen {
    private final Screen parent;
    private final Map<String, List<ConfigEntry>> categories;
    private final List<String> categoryNames;
    private final Consumer<Map<String, Map<String, Object>>> onSaveCallback;

    private ConfigListWidget listWidget;
    private ButtonWidget saveButton;
    private ButtonWidget cancelButton;
    private int selectedCategory = 0;
    private boolean hasChanges = false;

    public ModernConfigScreen(Screen parent, Text title, Map<String, List<ConfigEntry>> categories, Consumer<Map<String, Map<String, Object>>> onSaveCallback) {
        super(title);
        this.parent = parent;
        this.categories = categories;
        this.categoryNames = Lists.newArrayList(categories.keySet());
        this.onSaveCallback = onSaveCallback;

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

        int listTop = this.categoryNames.size() > 1 ? 60 : 40;
        this.listWidget = new ConfigListWidget(this.client, this.width, this.height, listTop, this.height - 40, 25);

        this.addSelectableChild(this.listWidget);
        this.addDrawableChild(this.listWidget);

        this.refreshList();

        this.saveButton = ButtonWidget.builder(Text.of("Save"), button -> this.save())
                .dimensions(this.width / 2 - 154, this.height - 32, 150, 20)
                .build();
        this.addDrawableChild(this.saveButton);

        this.cancelButton = ButtonWidget.builder(Text.of("Cancel"), button -> this.close())
                .dimensions(this.width / 2 + 4, this.height - 32, 150, 20)
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
        Map<String, Map<String, Object>> configData = Maps.newLinkedHashMap();

        for (Map.Entry<String, List<ConfigEntry>> category : this.categories.entrySet()) {
            Map<String, Object> categoryData = Maps.newLinkedHashMap();
            for (ConfigEntry entry : category.getValue()) {
                categoryData.put(entry.getFieldName(), entry.getValue());
            }
            configData.put(category.getKey(), categoryData);
        }

        if (this.onSaveCallback != null) {
            this.onSaveCallback.accept(configData);
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
                    Text.of("Title"),
                    Text.of("Message"),
                    Text.of("Confirm"),
                    Text.of("Cancel")
            ));
        } else {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);

        if (this.categoryNames.size() > 1) {
            this.renderCategoryTabs(context, mouseX, mouseY);
        }

        if (this.hasErrors()) {
            context.drawTextWithShadow(this.textRenderer, Text.of("Errors"), 10, 20, 0xFF5555);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCategoryTabs(DrawContext context, int mouseX, int mouseY) {
        int tabWidth = Math.min(150, this.width / this.categoryNames.size());
        int startX = (this.width - (tabWidth * this.categoryNames.size())) / 2;

        for (int i = 0; i < this.categoryNames.size(); i++) {
            int tabX = startX + i * tabWidth;
            int tabY = 35;
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
            int tabWidth = Math.min(150, this.width / this.categoryNames.size());
            int startX = (this.width - (tabWidth * this.categoryNames.size())) / 2;

            for (int i = 0; i < this.categoryNames.size(); i++) {
                int tabX = startX + i * tabWidth;
                int tabY = 35;
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

    public static class Builder {
        private final Screen parent;
        private final Text title;
        private final Map<String, List<ConfigEntry>> categories = Maps.newLinkedHashMap();
        private Consumer<Map<String, Map<String, Object>>> onSave;

        public Builder(Screen parent, Text title) {
            this.parent = parent;
            this.title = title;
        }

        public Builder setOnSave(Consumer<Map<String, Map<String, Object>>> onSave) {
            this.onSave = onSave;
            return this;
        }

        public CategoryBuilder addCategory(String name) {
            this.categories.put(name, Lists.newArrayList());
            return new CategoryBuilder(this, name);
        }

        public ModernConfigScreen build() {
            return new ModernConfigScreen(this.parent, this.title, this.categories, this.onSave);
        }

        public static class CategoryBuilder {
            private final Builder parent;
            private final String categoryName;

            public CategoryBuilder(Builder parent, String categoryName) {
                this.parent = parent;
                this.categoryName = categoryName;
            }

            public CategoryBuilder addString(String fieldName, Text displayName, String defaultValue) {
                this.parent.categories.get(this.categoryName).add(new StringConfigEntry(fieldName, displayName, defaultValue));
                return this;
            }

            public CategoryBuilder addBoolean(String fieldName, Text displayName, boolean defaultValue) {
                this.parent.categories.get(this.categoryName).add(new BooleanConfigEntry(fieldName, displayName, defaultValue));
                return this;
            }

            public CategoryBuilder addInteger(String fieldName, Text displayName, int defaultValue, int min, int max) {
                this.parent.categories.get(this.categoryName).add(new IntegerConfigEntry(fieldName, displayName, defaultValue, min, max));
                return this;
            }

            public Builder endCategory() {
                return this.parent;
            }
        }
    }
}
