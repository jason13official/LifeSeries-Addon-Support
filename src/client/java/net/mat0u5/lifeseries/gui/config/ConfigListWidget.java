package net.mat0u5.lifeseries.gui.config;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

public class ConfigListWidget extends AlwaysSelectedEntryListWidget<ConfigListWidget.ConfigEntryWidget> {

    public ConfigListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    public void addEntry(ConfigEntry configEntry) {
        addEntry(new ConfigEntryWidget(configEntry));
    }

    public void clearAllEntries() {
        clearEntries();
    }

    @Override
    public int getRowWidth() {
        return width - 20;
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        int listLeft = getX();
        int listTop = getY();
        int listRight = listLeft + width;
        int listBottom = listTop + height;

        context.fill(listLeft, listTop, listRight, listBottom, 0x20000000);

        int currentY = listTop + 4 - (int)getScrollAmount();

        for (int i = 0; i < getEntryCount(); i++) {
            ConfigEntryWidget entry = getEntry(i);
            int entryHeight = entry.getConfigEntry().getPreferredHeight();

            if (currentY + entryHeight >= listTop && currentY < listBottom) {
                int entryWidth = getRowWidth();
                int entryLeft = listLeft + (width - entryWidth) / 2;

                boolean hovered = mouseX >= entryLeft && mouseX < entryLeft + entryWidth &&
                        mouseY >= currentY && mouseY < currentY + entryHeight;

                entry.render(context, i, currentY, entryLeft, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
            }

            currentY += entryHeight + 2;
        }

        int maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollbarX = listRight - 6;
            int scrollbarTop = listTop;
            int scrollbarBottom = listBottom;
            int scrollbarHeight = scrollbarBottom - scrollbarTop;

            context.fill(scrollbarX, scrollbarTop, scrollbarX + 6, scrollbarBottom, 0x40000000);

            int handleHeight = Math.max(10, scrollbarHeight * scrollbarHeight / (scrollbarHeight + maxScroll));
            int handleY = scrollbarTop + (int)((scrollbarHeight - handleHeight) * getScrollAmount() / maxScroll);
            context.fill(scrollbarX + 1, handleY, scrollbarX + 5, handleY + handleHeight, 0x80FFFFFF);
        }
    }

    @Override
    public int getMaxScroll() {
        int totalHeight = 0;
        for (int i = 0; i < getEntryCount(); i++) {
            totalHeight += getEntry(i).getConfigEntry().getPreferredHeight();
        }
        return Math.max(0, totalHeight - height + 8);
    }

    @Override
    protected boolean isSelectButton(int button) {
        return false;
    }
    
    @Override
    protected boolean isScrollbarVisible() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int listTop = getY();
        int currentY = listTop + 4 - (int)getScrollAmount();

        for (int i = 0; i < getEntryCount(); i++) {
            ConfigEntryWidget entry = getEntry(i);
            int entryHeight = entry.getConfigEntry().getPreferredHeight();

            if (mouseY >= currentY && mouseY < currentY + entryHeight) {
                return entry.mouseClicked(mouseX, mouseY, button);
            }

            currentY += entryHeight;
        }

        return false;
    }

    public static class ConfigEntryWidget extends AlwaysSelectedEntryListWidget.Entry<ConfigEntryWidget> {
        private final ConfigEntry configEntry;

        public ConfigEntryWidget(ConfigEntry configEntry) {
            this.configEntry = configEntry;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int preferredHeight = configEntry.getPreferredHeight();
            configEntry.render(context, x, y, entryWidth, preferredHeight, mouseX, mouseY, hovered, tickDelta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return configEntry.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return configEntry.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return configEntry.charTyped(chr, modifiers);
        }

        @Override
        public Text getNarration() {
            return configEntry.getDisplayName();
        }

        public ConfigEntry getConfigEntry() {
            return configEntry;
        }
    }
}