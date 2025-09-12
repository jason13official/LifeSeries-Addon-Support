package net.minecraft.client.gui.screens.options.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class KeyBindsList extends ContainerObjectSelectionList<Entry> {
  private static final int ITEM_HEIGHT = 20;
  final KeyBindsScreen keyBindsScreen;
  private int maxNameWidth;

  public KeyBindsList(KeyBindsScreen keyBindsScreen, Minecraft minecraft) {
    super(minecraft, keyBindsScreen.width, keyBindsScreen.layout.getContentHeight(), keyBindsScreen.layout.getHeaderHeight(), 20);
    this.keyBindsScreen = keyBindsScreen;
    KeyMapping[] keyMappings = (KeyMapping[])ArrayUtils.clone(minecraft.options.keyMappings);
    Arrays.sort(keyMappings);
    String string = null;

    for(KeyMapping keyMapping : keyMappings) {
      String string2 = keyMapping.getCategory();
      if (!string2.equals(string)) {
        string = string2;
        this.addEntry(new CategoryEntry(Component.translatable(string2)));
      }

      Component component = Component.translatable(keyMapping.getName());
      int i = minecraft.font.width(component);
      if (i > this.maxNameWidth) {
        this.maxNameWidth = i;
      }

      this.addEntry(new KeyEntry(keyMapping, component));
    }

  }

  public void resetMappingAndUpdateButtons() {
    KeyMapping.resetMapping();
    this.refreshEntries();
  }

  public void refreshEntries() {
    this.children().forEach(Entry::refreshEntry);
  }

  public int getRowWidth() {
    return 340;
  }

  @Environment(EnvType.CLIENT)
  public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    abstract void refreshEntry();
  }

  @Environment(EnvType.CLIENT)
  public class CategoryEntry extends Entry {
    final Component name;
    private final int width;

    public CategoryEntry(final Component name) {
      this.name = name;
      this.width = KeyBindsList.this.minecraft.font.width(this.name);
    }

    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
      Font var10001 = KeyBindsList.this.minecraft.font;
      Component var10002 = this.name;
      int var10003 = KeyBindsList.this.width / 2 - this.width / 2;
      int var10004 = top + height;
      Objects.requireNonNull(KeyBindsList.this.minecraft.font);
      guiGraphics.drawString(var10001, var10002, var10003, var10004 - 9 - 1, -1);
    }

    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
      return null;
    }

    public List<? extends GuiEventListener> children() {
      return Collections.emptyList();
    }

    public List<? extends NarratableEntry> narratables() {
      return ImmutableList.of(new NarratableEntry() {
        public NarratableEntry.NarrationPriority narrationPriority() {
          return NarrationPriority.HOVERED;
        }

        public void updateNarration(NarrationElementOutput narrationElementOutput) {
          narrationElementOutput.add(NarratedElementType.TITLE, CategoryEntry.this.name);
        }
      });
    }

    protected void refreshEntry() {
    }
  }

  @Environment(EnvType.CLIENT)
  public class KeyEntry extends Entry {
    private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
    private static final int PADDING = 10;
    private final KeyMapping key;
    private final Component name;
    private final Button changeButton;
    private final Button resetButton;
    private boolean hasCollision = false;

    KeyEntry(final KeyMapping key, final Component name) {
      this.key = key;
      this.name = name;
      this.changeButton = Button.builder(name, (button) -> {
        KeyBindsList.this.keyBindsScreen.selectedKey = key;
        KeyBindsList.this.resetMappingAndUpdateButtons();
      }).bounds(0, 0, 75, 20).createNarration((supplier) -> key.isUnbound() ? Component.translatable("narrator.controls.unbound", new Object[]{name}) : Component.translatable("narrator.controls.bound", new Object[]{name, supplier.get()})).build();
      this.resetButton = Button.builder(RESET_BUTTON_TITLE, (button) -> {
        key.setKey(key.getDefaultKey());
        KeyBindsList.this.resetMappingAndUpdateButtons();
      }).bounds(0, 0, 50, 20).createNarration((supplier) -> Component.translatable("narrator.controls.reset", new Object[]{name})).build();
      this.refreshEntry();
    }

    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
      int i = KeyBindsList.this.scrollBarX() - this.resetButton.getWidth() - 10;
      int j = top - 2;
      this.resetButton.setPosition(i, j);
      this.resetButton.render(guiGraphics, mouseX, mouseY, partialTick);
      int k = i - 5 - this.changeButton.getWidth();
      this.changeButton.setPosition(k, j);
      this.changeButton.render(guiGraphics, mouseX, mouseY, partialTick);
      Font var10001 = KeyBindsList.this.minecraft.font;
      Component var10002 = this.name;
      int var10004 = top + height / 2;
      Objects.requireNonNull(KeyBindsList.this.minecraft.font);
      guiGraphics.drawString(var10001, var10002, left, var10004 - 9 / 2, -1);
      if (this.hasCollision) {
        int l = 3;
        int m = this.changeButton.getX() - 6;
        guiGraphics.fill(m, top - 1, m + 3, top + height, -65536);
      }

    }

    public List<? extends GuiEventListener> children() {
      return ImmutableList.of(this.changeButton, this.resetButton);
    }

    public List<? extends NarratableEntry> narratables() {
      return ImmutableList.of(this.changeButton, this.resetButton);
    }

    protected void refreshEntry() {
      this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
      this.resetButton.active = !this.key.isDefault();
      this.hasCollision = false;
      MutableComponent mutableComponent = Component.empty();
      if (!this.key.isUnbound()) {
        for(KeyMapping keyMapping : KeyBindsList.this.minecraft.options.keyMappings) {
          if (keyMapping != this.key && this.key.same(keyMapping)) {
            if (this.hasCollision) {
              mutableComponent.append(", ");
            }

            this.hasCollision = true;
            mutableComponent.append(Component.translatable(keyMapping.getName()));
          }
        }
      }

      if (this.hasCollision) {
        this.changeButton.setMessage(Component.literal("[ ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.RED));
        this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", new Object[]{mutableComponent})));
      } else {
        this.changeButton.setTooltip((Tooltip)null);
      }

      if (KeyBindsList.this.keyBindsScreen.selectedKey == this.key) {
        this.changeButton.setMessage(Component.literal("> ").append(this.changeButton.getMessage().copy().withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.UNDERLINE})).append(" <").withStyle(ChatFormatting.YELLOW));
      }

    }
  }
}
