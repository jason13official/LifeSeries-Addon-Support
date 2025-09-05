package com.cursee.ls_addon_support.gui.config;

import com.cursee.ls_addon_support.gui.config.entries.ConfigEntry;
import com.cursee.ls_addon_support.utils.TextColors;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

public class ConfigListWidget extends
    AlwaysSelectedEntryListWidget<ConfigListWidget.ConfigEntryWidget> {

  public static final int ENTRY_GAP = 2;
  private static final int MAX_HIGHLIGHTED_ENTRIES = 3;
  private static final int SCROLLBAR_OFFSET_X = 6;
  protected ConfigScreen screen;

  public ConfigListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
    super(client, width, height, y, itemHeight);
  }

  public void setScreen(ConfigScreen screen) {
    this.screen = screen;
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

    int maxScroll = getMaxScrollY();
    if (getScrolledAmount() > maxScroll) {
      setScrollY(maxScroll);
    }

    int listLeft = getX();
    int listTop = getY();
    int listRight = listLeft + width;
    int listBottom = listTop + height;

    context.fill(listLeft, listTop, listRight, listBottom, TextColors.BLACK_A32);

    int currentY = getCurrentY();

    Map<Float, ConfigEntry> highlightedEntries = new TreeMap<>();

    for (int i = 0; i < getEntryCount(); i++) {
      ConfigEntryWidget entry = getEntry(i);
      ConfigEntry configEntry = entry.getConfigEntry();
      int entryHeight = configEntry.getPreferredHeight();

      if (currentY + entryHeight >= listTop && currentY < listBottom) {
        int entryWidth = getRowWidth();
        int entryLeft = listLeft + (width - entryWidth) / 2;

        boolean hovered = mouseX >= entryLeft && mouseX < entryLeft + entryWidth &&
            mouseY >= currentY && mouseY < currentY + entryHeight;

        entry.render(context, i, currentY, entryLeft, entryWidth, entryHeight, mouseX, mouseY,
            hovered, delta);

        List<ConfigEntry> withChildren = List.of(configEntry);

        if (screen != null) {
          withChildren = screen.getAllEntries(withChildren);
          if (!withChildren.contains(configEntry)) {
            withChildren.add(configEntry);
          }
        }

        for (ConfigEntry highlightEntry : withChildren) {
          if (highlightEntry.highlightAlpha > 0.0f) {
            highlightedEntries.put(highlightEntry.highlightAlpha, highlightEntry);
          }
        }

      }

      currentY += entryHeight + ENTRY_GAP;
    }

    int highlightedCount = highlightedEntries.size();
    if (highlightedCount > MAX_HIGHLIGHTED_ENTRIES) {
      int pos = 0;
      for (ConfigEntry entry : highlightedEntries.values()) {
        if (pos >= MAX_HIGHLIGHTED_ENTRIES) {
          break;
        }
        entry.highlightAlpha = 0.0f;
        pos++;
      }
    }

    if (maxScroll > 0) {
      int scrollbarX = listRight - SCROLLBAR_OFFSET_X;
      int scrollbarTop = listTop;
      int scrollbarBottom = listBottom;
      int scrollbarHeight = scrollbarBottom - scrollbarTop;

      context.fill(scrollbarX, scrollbarTop, scrollbarX + SCROLLBAR_OFFSET_X, scrollbarBottom,
          TextColors.BLACK_A64);

      int handleHeight = Math.max(10,
          scrollbarHeight * scrollbarHeight / (scrollbarHeight + maxScroll));
      int handleY =
          scrollbarTop + (int) ((scrollbarHeight - handleHeight) * getScrolledAmount() / maxScroll);
      context.fill(scrollbarX + 1, handleY, scrollbarX + SCROLLBAR_OFFSET_X - 1,
          handleY + handleHeight, TextColors.WHITE_A128);
    }
  }

  @Override
  public int getMaxScrollY() {
    int totalHeight = 0;
    for (int i = 0; i < getEntryCount(); i++) {
      totalHeight += getEntry(i).getConfigEntry().getPreferredHeight();
    }
    return Math.max(0, totalHeight - height + 8);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (!isMouseOver(mouseX, mouseY)) {
      return false;
    }

    int listTop = getY();
    int currentY = getCurrentY();

    for (int i = 0; i < getEntryCount(); i++) {
      ConfigEntryWidget entry = getEntry(i);
      int entryHeight = entry.getConfigEntry().getPreferredHeight() + ENTRY_GAP;

      if (mouseY >= currentY && mouseY < currentY + entryHeight) {
        setFocused(entry);
        entry.getConfigEntry().setFocused(true);
        return entry.mouseClicked(mouseX, mouseY, button);
      }

      currentY += entryHeight;
    }

    return false;
  }

  public double getScrolledAmount() {
    return getScrollY();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    ConfigEntryWidget entry = getFocused();
      if (entry == null) {
          return false;
      }
    return entry.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    ConfigEntryWidget entry = getFocused();
      if (entry == null) {
          return false;
      }
    return entry.charTyped(chr, modifiers);
  }

  public int getCurrentY() {
    return getY() + 4 - (int) getScrolledAmount();
  }

  @Override
  public int getEntryCount() {
    return this.children().size();
  }

  public static class ConfigEntryWidget extends Entry<ConfigEntryWidget> {

    private final ConfigEntry configEntry;

    public ConfigEntryWidget(ConfigEntry configEntry) {
      this.configEntry = configEntry;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth,
        int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
      int preferredHeight = configEntry.getPreferredHeight();
      configEntry.render(context, x, y, entryWidth, preferredHeight, mouseX, mouseY, hovered,
          tickDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      configEntry.setFocused(true);
      return configEntry.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      configEntry.setFocused(true);
      return configEntry.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
      configEntry.setFocused(true);
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