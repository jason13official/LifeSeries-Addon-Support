package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.gui.config.entries.simple.BooleanConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.simple.TextConfigEntry;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GroupConfigEntry extends ConfigEntry {
    private final ConfigEntry mainEntry;
    private final List<ConfigEntry> childEntries;
    private boolean isExpanded = false;
    private static final int CHILD_INDENT = 20;

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
        mainEntry.render(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int iconX = x + 5;
        int iconY = y + 6;
        renderExpandIcon(context, iconX, iconY, isExpanded);

        boolean shouldBeExpanded = shouldExpand();
        if (shouldBeExpanded != isExpanded) {
            isExpanded = shouldBeExpanded;
        }
/*
        if (mainEntry != null) {
            int mainEntryX = x + 15;
            int mainEntryWidth = width - 15;
            mainEntry.render(context, mainEntryX, y, mainEntryWidth, 20, mouseX, mouseY, hovered, tickDelta);
        } else {
            int textColor = TextColors.WHITE;
            context.drawTextWithShadow(textRenderer, displayName, x + 25, y + 6, textColor);
        }
 */

        if (isExpanded) {
            int childY = y + 20;
            for (ConfigEntry child : childEntries) {
                int childHeight = child.getPreferredHeight();
                child.render(context, x + CHILD_INDENT, childY, width - CHILD_INDENT, childHeight, mouseX, mouseY, false, tickDelta);
                childY += childHeight + 2;
            }
        }
    }

    private void renderExpandIcon(DrawContext context, int x, int y, boolean expanded) {
        int color = 0xFFFFFFFF;

        if (expanded) {
            context.fill(x + 2, y, x + 6, y + 1, color);
            context.fill(x + 3, y + 1, x + 5, y + 2, color);
            context.fill(x + 4, y + 2, x + 4, y + 3, color);
        } else {
            context.fill(x, y + 2, x + 1, y + 6, color);
            context.fill(x + 1, y + 3, x + 2, y + 5, color);
            context.fill(x + 2, y + 4, x + 3, y + 4, color);
        }
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
        //THIS DOES NOT WORK AT ALL
        if (mouseY <= 20) {
            if (mainEntry != null) {
                boolean handled = mainEntry.mouseClicked(mouseX, mouseY, button);
                if (handled) {
                    return true;
                }
            }
            /*
            if (!(mainEntry instanceof BooleanConfigEntry)) {
                isExpanded = !isExpanded;
            }
             */
            return true;
        }

        if (isExpanded) {
            double childY = 20;
            for (ConfigEntry child : childEntries) {
                int childHeight = child.getPreferredHeight();
                if (mouseY >= childY && mouseY < childY + childHeight) {
                    return child.mouseClicked(mouseX - CHILD_INDENT, mouseY - childY, button);
                }
                childY += childHeight + 2;
            }
        }

        return false;
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        if (mainEntry != null && (!isExpanded || mainEntry.isFocused)) {
            boolean handled = mainEntry.keyPressed(keyCode, scanCode, modifiers);
            if (handled) return true;
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused) continue;
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
            boolean handled = mainEntry.charTyped(chr, modifiers);
            if (handled) return true;
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused) continue;
                if (child.charTyped(chr, modifiers)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void resetToDefault() {
        if (mainEntry != null) {
            mainEntry.resetToDefault();
        }
        for (ConfigEntry child : childEntries) {
            child.resetToDefault();
        }
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
    public Object getValue() {
        return mainEntry != null ? mainEntry.getValue() : null;
    }

    @Override
    public String getValueAsString() {
        return mainEntry != null ? mainEntry.getValueAsString() : "";
    }

    @Override
    public Object getDefaultValue() {
        return mainEntry != null ? mainEntry.getDefaultValue() : null;
    }

    @Override
    public String getDefaultValueAsString() {
        return mainEntry != null ? mainEntry.getDefaultValueAsString() : "";
    }

    @Override
    public Object getStartingValue() {
        return mainEntry != null ? mainEntry.getStartingValue() : null;
    }

    @Override
    public String getValueType() {
        return "group";
    }

    @Override
    public void setValue(Object value) {
        if (mainEntry != null) {
            mainEntry.setValue(value);
        }
    }

    @Override
    public boolean modified() {
        boolean modified = mainEntry != null && mainEntry.modified();

        for (ConfigEntry child : childEntries) {
            if (child.modified()) {
                modified = true;
                break;
            }
        }

        return modified;
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
    public boolean canReset() {
        boolean canReset = mainEntry != null && mainEntry.canReset();

        for (ConfigEntry child : childEntries) {
            if (child.canReset()) {
                canReset = true;
                break;
            }
        }

        return canReset;
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
}