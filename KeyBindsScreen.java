package net.minecraft.client.gui.screens.options.controls;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class KeyBindsScreen extends OptionsSubScreen {
  private static final Component TITLE = Component.translatable("controls.keybinds.title");
  @Nullable
  public KeyMapping selectedKey;
  public long lastKeySelection;
  private KeyBindsList keyBindsList;
  private Button resetButton;

  public KeyBindsScreen(Screen lastScreen, Options options) {
    super(lastScreen, options, TITLE);
  }

  protected void addContents() {
    this.keyBindsList = (KeyBindsList)this.layout.addToContents(new KeyBindsList(this, this.minecraft));
  }

  protected void addOptions() {
  }

  protected void addFooter() {
    this.resetButton = Button.builder(Component.translatable("controls.resetAll"), (button) -> {
      for(KeyMapping keyMapping : this.options.keyMappings) {
        keyMapping.setKey(keyMapping.getDefaultKey());
      }

      this.keyBindsList.resetMappingAndUpdateButtons();
    }).build();
    LinearLayout linearLayout = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
    linearLayout.addChild(this.resetButton);
    linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).build());
  }

  protected void repositionElements() {
    this.layout.arrangeElements();
    this.keyBindsList.updateSize(this.width, this.layout);
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (this.selectedKey != null) {
      this.selectedKey.setKey(Type.MOUSE.getOrCreate(button));
      this.selectedKey = null;
      this.keyBindsList.resetMappingAndUpdateButtons();
      return true;
    } else {
      return super.mouseClicked(mouseX, mouseY, button);
    }
  }

  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (this.selectedKey != null) {
      if (keyCode == 256) {
        this.selectedKey.setKey(InputConstants.UNKNOWN);
      } else {
        this.selectedKey.setKey(InputConstants.getKey(keyCode, scanCode));
      }

      this.selectedKey = null;
      this.lastKeySelection = Util.getMillis();
      this.keyBindsList.resetMappingAndUpdateButtons();
      return true;
    } else {
      return super.keyPressed(keyCode, scanCode, modifiers);
    }
  }

  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    boolean bl = false;

    for(KeyMapping keyMapping : this.options.keyMappings) {
      if (!keyMapping.isDefault()) {
        bl = true;
        break;
      }
    }

    this.resetButton.active = bl;
  }
}
