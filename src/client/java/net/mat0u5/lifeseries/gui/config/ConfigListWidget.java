package net.mat0u5.lifeseries.gui.config;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

public class ConfigListWidget extends AlwaysSelectedEntryListWidget<ConfigListWidget.ConfigEntryWidget> {

    public ConfigListWidget(MinecraftClient client, int width, int height, int y, int bottom, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    public void addEntry(ConfigEntry configEntry) {
        this.addEntry(new ConfigEntryWidget(configEntry));
    }

    public void clearAllEntries() {
        this.clearEntries();
    }

    @Override
    public int getRowWidth() {
        return this.width - 40;
    }
/*
    @Override
    protected void renderBackground(DrawContext context) {
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x20000000);
    }
*/

    public static class ConfigEntryWidget extends AlwaysSelectedEntryListWidget.Entry<ConfigEntryWidget> {
        private final ConfigEntry configEntry;

        public ConfigEntryWidget(ConfigEntry configEntry) {
            this.configEntry = configEntry;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (hovered) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x20FFFFFF);
            }

            this.configEntry.render(context, x, y, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.configEntry.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return this.configEntry.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return this.configEntry.charTyped(chr, modifiers);
        }

        @Override
        public Text getNarration() {
            return this.configEntry.getDisplayName();
        }

        public ConfigEntry getConfigEntry() {
            return this.configEntry;
        }
    }
}