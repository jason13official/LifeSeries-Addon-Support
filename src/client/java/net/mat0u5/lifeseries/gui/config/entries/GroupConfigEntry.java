package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ConfigListWidget;
import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.gui.config.entries.simple.BooleanConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.simple.TextConfigEntry;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GroupConfigEntry extends EmptyConfigEntry {
    private final ConfigEntry mainEntry;
    private final List<ConfigEntry> childEntries;
    private boolean isExpanded = false;
    private static final int CHILD_INDENT = 20;

    private int x;
    private int y;

    public GroupConfigEntry(ConfigEntry mainEntry, List<ConfigEntry> childEntries) {
        super("", Text.empty());
        this.mainEntry = mainEntry;
        this.childEntries = new ArrayList<>(childEntries);
    }

    @Override
    public void setScreen(ConfigScreen screen) {
        super.setScreen(screen);
        if (mainEntry != null) {
            mainEntry.setScreen(screen);
        }
        for (ConfigEntry child : childEntries) {
            child.setScreen(screen);
        }
    }

    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.y = y;
        renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {

        int currentY = y;

        if (mainEntry != null && screen != null) {
            int entryHeight = mainEntry.getPreferredHeight();
            boolean entryHovered = mouseX >= x && mouseX < x + width &&
                    mouseY >= currentY && mouseY < currentY + entryHeight;

            mainEntry.render(context, x, y, width, entryHeight, mouseX, mouseY, entryHovered, tickDelta);
            currentY += entryHeight + ConfigListWidget.ENTRY_GAP;
        }


        boolean shouldBeExpanded = shouldExpand();
        if (shouldBeExpanded != isExpanded) {
            isExpanded = shouldBeExpanded;
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                int entryHeight = child.getPreferredHeight();
                boolean entryHovered = mouseX >= x && mouseX < x + width &&
                        mouseY >= currentY && mouseY < currentY + entryHeight;
                child.render(context, x + CHILD_INDENT, currentY, width - CHILD_INDENT, entryHeight, mouseX, mouseY, entryHovered, tickDelta);
                currentY += entryHeight + ConfigListWidget.ENTRY_GAP;
            }
        }

        renderExpandIcon(context, x, y, isExpanded, currentY);
    }

    private void renderExpandIcon(DrawContext context, int x, int y, boolean expanded, int endY) {
        String text = expanded ? "- " : "+ ";
        RenderUtils.drawTextRight(context, textRenderer, TextColors.WHITE, Text.of(text), x + 15, y + 6);
        context.fill(1, y, 2, endY - ConfigListWidget.ENTRY_GAP, 0x80FFFFFF);
    }

    private boolean shouldExpand() {
        if (mainEntry instanceof BooleanConfigEntry booleanEntry) {
            return booleanEntry.getValue();
        }
        if (mainEntry instanceof TextConfigEntry textEntry) {
            return textEntry.clicked;
        }
        return isExpanded;
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        int currentY = (int) mouseY - this.y;

        if (currentY < 0 || mainEntry == null) return false;

        if (currentY <= mainEntry.getPreferredHeight()) {
            mainEntry.setFocused(true);
            if (mainEntry.mouseClicked(mouseX, mouseY, button)) return true;
        }

        if (isExpanded) {
            double childY = mainEntry.getPreferredHeight();
            for (ConfigEntry child : childEntries) {
                int childHeight = child.getPreferredHeight();
                if (currentY >= childY && currentY < childY + childHeight + ConfigListWidget.ENTRY_GAP) {
                    child.setFocused(true);
                    if (child.mouseClicked(mouseX, mouseY, button)) return true;
                }
                childY += childHeight;
            }
        }

        return false;
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        if (mainEntry != null && (!isExpanded || mainEntry.isFocused)) {
            mainEntry.setFocused(true);
            boolean handled = mainEntry.keyPressed(keyCode, scanCode, modifiers);
            if (handled) return true;
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused) continue;
                child.setFocused(true);
                if (child.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        if (mainEntry != null && (!isExpanded || mainEntry.isFocused)) {
            mainEntry.setFocused(true);
            boolean handled = mainEntry.charTyped(chr, modifiers);
            if (handled) return true;
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused) continue;
                child.setFocused(true);
                if (child.charTyped(chr, modifiers)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getPreferredHeight() {
        int height = 20;

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                height += child.getPreferredHeight() + 2;
            }
        }

        return height;
    }

    @Override
    public String getValueType() {
        return "group";
    }

    @Override
    public boolean hasError() {
        boolean hasError = mainEntry != null && mainEntry.hasError();

        for (ConfigEntry child : childEntries) {
            if (child.hasError()) {
                hasError = true;
                break;
            }
        }

        return hasError;
    }

    @Override
    protected void markChanged() {
        super.markChanged();
        if (mainEntry != null) {
            mainEntry.markChanged();
        }
    }

    public void addChildEntry(ConfigEntry entry) {
        entry.setScreen(this.screen);
        childEntries.add(entry);
    }

    public void removeChildEntry(ConfigEntry entry) {
        childEntries.remove(entry);
    }

    public List<ConfigEntry> getChildEntries() {
        return new ArrayList<>(childEntries);
    }

    public ConfigEntry getMainEntry() {
        return mainEntry;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    @Override
    public void setFocused(boolean focused) {
        this.isFocused = focused;

        if (focused && screen != null) {
            ConfigEntry currentlyFocused = screen.getFocusedEntry();
            if (currentlyFocused != null && !childEntries.contains(currentlyFocused)) {
                screen.setFocusedEntry(this);
            }
        }
    }
}