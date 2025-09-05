package com.cursee.ls_addon_support.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public abstract class ButtonConfigEntry extends ConfigEntry {

  protected final ButtonWidget button;

  public ButtonConfigEntry(String fieldName, String displayName, String description,
      int buttonWidth, int buttonHeight) {
    super(fieldName, displayName, description);
    button = createButton(buttonWidth, buttonHeight);
  }

  protected ButtonWidget createButton(int width, int height) {
    return ButtonWidget.builder(getButtonText(), this::onButtonClick)
        .dimensions(0, 0, width, height)
        .build();
  }

  protected abstract Text getButtonText();

  protected abstract void onButtonClick(ButtonWidget button);

  protected void updateButtonText() {
    button.setMessage(getButtonText());
  }

  @Override
  protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX,
      int mouseY, boolean hovered, float tickDelta) {
    int entryWidth = getEntryContentWidth(width);
    button.setX(getButtonPosX(x, entryWidth));
    button.setY(getButtonPosY(y, height));
    button.render(context, mouseX, mouseY, tickDelta);
  }

  protected int getButtonPosX(int x, int entryWidth) {
    return x + entryWidth - button.getWidth() - 5;
  }

  protected int getButtonPosY(int y, int height) {
    //return y + (height - button.getHeight()) / 2; CENTER
    return y;
  }

  @Override
  protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
    this.button.mouseClicked(mouseX, mouseY, button);
    return true;
  }

  @Override
  protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
    return false;
  }

  @Override
  protected boolean charTypedEntry(char chr, int modifiers) {
    return false;
  }
}