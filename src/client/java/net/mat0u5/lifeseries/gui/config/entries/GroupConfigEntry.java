package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ConfigListWidget;
import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GroupConfigEntry<T extends ConfigEntry & IEntryGroupHeader> extends EmptyConfigEntry {
    private static final String ENTRY_TYPE = "group";
    private static final int CHILD_INDENT = 20;
    protected static final int EXPAND_TEXT_OFFSET_X = LABEL_OFFSET_X - 10;
    protected static final int EXPAND_TEXT_OFFSET_Y = LABEL_OFFSET_Y;
    private static final int EXPAND_SIDEBAR_THICKNESS = 1;
    private static final int EXPAND_SIDEBAR_OFFSET_X = -9;

    private final T mainEntry;
    private final List<ConfigEntry> childEntries;
    private boolean isExpanded = false;
    private boolean showSidebar;
    private boolean renderBottomBar = false;

    private int y;

    public GroupConfigEntry(T mainEntry, List<ConfigEntry> childEntries, boolean showSidebar, boolean openByDefault) {
        super("", Text.empty());
        this.mainEntry = mainEntry;
        this.childEntries = new ArrayList<>(childEntries);
        this.showSidebar = showSidebar;
        if (openByDefault) {
            mainEntry.expand();
        }
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

        renderExpandIcon(context, x, y, isExpanded, currentY, width);
    }

    private void renderExpandIcon(DrawContext context, int x, int y, boolean expanded, int endY, int width) {
        String text = expanded ? "- " : "+ ";
        RenderUtils.drawTextRight(context, textRenderer, TextColors.WHITE, Text.of(text), x + EXPAND_TEXT_OFFSET_X, y + EXPAND_TEXT_OFFSET_Y);
        if (showSidebar) {
            context.fill(x+EXPAND_SIDEBAR_OFFSET_X, y, x+EXPAND_SIDEBAR_OFFSET_X+EXPAND_SIDEBAR_THICKNESS, endY - ConfigListWidget.ENTRY_GAP, TextColors.WHITE_A128);
        }
        if (expanded && renderBottomBar) {
            context.fill(x+EXPAND_SIDEBAR_OFFSET_X, endY - ConfigListWidget.ENTRY_GAP, Math.max(x + LABEL_OFFSET_X + CHILD_INDENT + 20, (x+width)/2), endY - ConfigListWidget.ENTRY_GAP+EXPAND_SIDEBAR_THICKNESS, TextColors.WHITE_A32);
        }
    }

    private boolean shouldExpand() {
        return mainEntry.shouldExpand();
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
        if (mainEntry != null && mainEntry.isFocused()) {
            if (mainEntry.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused()) continue;
                if (child.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        if (mainEntry != null && mainEntry.isFocused()) {
            if (mainEntry.charTyped(chr, modifiers)) {
                return true;
            }
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused()) continue;
                if (child.charTyped(chr, modifiers)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getPreferredHeight() {
        int height = PREFFERED_HEIGHT;
        if (mainEntry != null) {
            height = mainEntry.getPreferredHeight();
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                height += child.getPreferredHeight() + ConfigListWidget.ENTRY_GAP;
            }
        }

        return height;
    }

    @Override
    public String getValueType() {
        return ENTRY_TYPE;
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
    public boolean isFocused() {
        boolean isFocused = super.isFocused();
        if (isFocused) {
            return true;
        }
        for (ConfigEntry child : childEntries) {
            if (child.isFocused()) {
                return true;
            }
        }
        return false;
    }
}