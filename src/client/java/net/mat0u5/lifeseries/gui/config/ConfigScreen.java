package net.mat0u5.lifeseries.gui.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.mat0u5.lifeseries.config.ClientConfigNetwork;
import net.mat0u5.lifeseries.gui.config.entries.GroupConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.mat0u5.lifeseries.MainClient.clientConfig;

public class ConfigScreen extends Screen {
    private static final int HEADER_HEIGHT_SMALL = 30;
    private static final int HEADER_HEIGHT_LARGE = 50;
    private static final int HEADER_TITLE_Y = 10;
    private static final int HEADER_CATEGORY_GAP = 5;
    private static final int HEADER_CATEGORY_Y = 24;
    private static final int HEADER_CATEGORY_MIN_WIDTH = 130;
    private static final int HEADER_CATEGORY_HEIGHT = 20;
    private static final int HEADER_CATEGORY_NAME_OFFSET_Y = 6;

    private static final int FOOTER_HEIGHT = 30;
    private static final int FOOTER_BUTTON_GAP = 4;
    private static final int FOOTER_BUTTON_WIDTH = 150;
    private static final int FOOTER_BUTTON_HEIGHT = 20;

    private final Screen parent;
    private final Map<String, List<ConfigEntry>> categories;
    private final List<String> categoryNames;

    private ConfigEntry focusedEntry;
    public ConfigListWidget listWidget;
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

        int listTop = this.categoryNames.size() > 1 ? HEADER_HEIGHT_LARGE : HEADER_HEIGHT_SMALL;

        this.listWidget = new ConfigListWidget(this.client, this.width, this.height - listTop - FOOTER_HEIGHT, listTop, ConfigEntry.PREFFERED_HEIGHT);
        listWidget.setScreen(this);

        this.addSelectableChild(this.listWidget);
        this.addDrawableChild(this.listWidget);

        this.refreshList();

        this.saveButton = ButtonWidget.builder(Text.of("Save & Quit"), button -> this.save())
                .dimensions(this.width / 2 + FOOTER_BUTTON_GAP, this.height - FOOTER_BUTTON_HEIGHT - FOOTER_BUTTON_GAP, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
                .build();
        this.addDrawableChild(this.saveButton);

        this.cancelButton = ButtonWidget.builder(Text.of("Discard Changes"), button -> this.close())
                .dimensions(this.width / 2 - FOOTER_BUTTON_WIDTH - FOOTER_BUTTON_GAP, this.height - FOOTER_BUTTON_HEIGHT - FOOTER_BUTTON_GAP, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
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

    public void onEntryValueChanged() {
        this.updateButtonStates();
    }

    public List<ConfigEntry> getAllEntries() {
        List<ConfigEntry> allSurfaceEntries = new ArrayList<>();
        for (List<ConfigEntry> entries : this.categories.values()) {
            allSurfaceEntries.addAll(entries);
        }
        return getAllEntries(allSurfaceEntries);
    }
    public List<ConfigEntry> getAllEntries(List<ConfigEntry> currentEntries) {
        List<ConfigEntry> allEntries = new ArrayList<>();
        for (ConfigEntry entry : currentEntries) {
            if (entry instanceof GroupConfigEntry groupEntry) {
                allEntries.addAll(getAllEntries(groupEntry.getChildEntries()));
            }
            else {
                allEntries.add(entry);
            }
        }
        return allEntries;
    }

    private void updateButtonStates() {
        this.hasChanges = false;
        for (ConfigEntry entry : getAllEntries()) {
            if (entry.modified()) {
                this.hasChanges = true;
                break;
            }
        }
        this.saveButton.active = this.hasChanges && !this.hasErrors();
    }

    private boolean hasErrors() {
        for (ConfigEntry entry : getAllEntries()) {
            if (entry.hasError()) {
                return true;
            }
        }
        return false;
    }

    private void save() {
        List<ConfigEntry> allSurfaceEntriesClient = new ArrayList<>();
        List<ConfigEntry> allSurfaceEntriesServer = new ArrayList<>();
        for (Map.Entry<String, List<ConfigEntry>> category : this.categories.entrySet()) {
            if (category.getKey().equals("Server")) {
                allSurfaceEntriesServer.addAll(category.getValue());
            }
            else if (category.getKey().equals("Client")) {
                allSurfaceEntriesClient.addAll(category.getValue());
            }
        }

        for (ConfigEntry entry : getAllEntries(allSurfaceEntriesServer)) {
            // Server
            if (!entry.modified()) continue;
            if (entry instanceof GroupConfigEntry) continue;
            NetworkHandlerClient.sendConfigUpdate(
                    entry.getValueType().toString(),
                    entry.getFieldName(),
                    List.of(entry.getValueAsString())
            );
        }
        for (ConfigEntry entry : getAllEntries(allSurfaceEntriesClient)) {
            // Client
            if (!entry.modified()) continue;
            if (entry instanceof GroupConfigEntry) continue;
            String id = entry.getFieldName();
            String valueStr = entry.getValueAsString();
            clientConfig.setProperty(id, valueStr);
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

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, HEADER_TITLE_Y, TextColors.WHITE);

        if (this.categoryNames.size() > 1) {
            this.renderCategoryTabs(context, mouseX, mouseY);
        }

        if (this.hasErrors()) {
            //TODO
            context.drawTextWithShadow(this.textRenderer, Text.of("Errors"), 10, 20, TextColors.LIGHT_RED);
        }

    }

    private void renderCategoryTabs(DrawContext context, int mouseX, int mouseY) {
        int tabWidth = Math.min(HEADER_CATEGORY_MIN_WIDTH, this.width / this.categoryNames.size());
        int startX = (this.width - ((tabWidth+HEADER_CATEGORY_GAP) * this.categoryNames.size())) / 2;

        for (int i = 0; i < this.categoryNames.size(); i++) {
            int tabX = startX + i * (tabWidth+HEADER_CATEGORY_GAP);
            int tabY = HEADER_CATEGORY_Y;
            int tabHeight = HEADER_CATEGORY_HEIGHT;

            boolean isSelected = i == this.selectedCategory;
            boolean isHovered = mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + tabHeight;

            int color = isSelected ? TextColors.WHITE_A128 : (isHovered ? TextColors.WHITE_A64 : TextColors.WHITE_A32);
            context.fill(tabX, tabY, tabX + tabWidth, tabY + tabHeight, color);

            String categoryName = this.categoryNames.get(i);
            int textColor = isSelected ? TextColors.WHITE : TextColors.PASTEL_WHITE;
            context.drawCenteredTextWithShadow(this.textRenderer, categoryName, tabX + tabWidth / 2, tabY + HEADER_CATEGORY_NAME_OFFSET_Y, textColor);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.categoryNames.size() > 1 && button == 0) {
            int tabWidth = Math.min(HEADER_CATEGORY_MIN_WIDTH, this.width / this.categoryNames.size());
            int startX = (this.width - ((tabWidth+HEADER_CATEGORY_GAP) * this.categoryNames.size())) / 2;

            for (int i = 0; i < this.categoryNames.size(); i++) {
                int tabX = startX + i * (tabWidth+HEADER_CATEGORY_GAP);
                int tabY = HEADER_CATEGORY_Y;
                int tabHeight = HEADER_CATEGORY_HEIGHT;

                if (mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + tabHeight) {
                    this.selectedCategory = i;
                    this.refreshList();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    public ConfigEntry getFocusedEntry() {
        return focusedEntry;
    }

    public void setFocusedEntry(ConfigEntry entry) {
        if (entry instanceof GroupConfigEntry) return;
        if (focusedEntry == entry) return;

        if (focusedEntry != null) {
            focusedEntry.setFocused(false);
        }
        focusedEntry = entry;
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

            public CategoryBuilder addEntry(ConfigEntry entry) {
                this.parent.categories.get(this.categoryName).add(entry);
                return this;
            }

            public Builder endCategory() {
                return this.parent;
            }
        }
    }
}