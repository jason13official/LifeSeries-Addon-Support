package com.cursee.ls_addon_support.gui.config.entries;

import com.cursee.ls_addon_support.utils.TextColors;
import java.util.Objects;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public abstract class TextFieldConfigEntry extends ConfigEntry {

  protected static final int DEFAULT_TEXT_FIELD_HEIGHT = 18;
  private static final int DEFAULT_TEXT_FIELD_WIDTH = 100;
  private static final int TEXT_FIELD_OFFSET_X = -5;
  private static final int TEXT_FIELD_OFFSET_Y = 1;
  protected final TextFieldWidget textField;
  private int maxTextFieldLength = 8192;

  public TextFieldConfigEntry(String fieldName, String displayName, String description) {
    this(fieldName, displayName, description, DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT);
  }

  public TextFieldConfigEntry(String fieldName, String displayName, String description,
      int textFieldWidth) {
    this(fieldName, displayName, description, textFieldWidth, DEFAULT_TEXT_FIELD_HEIGHT);
  }

  public TextFieldConfigEntry(String fieldName, String displayName, String description,
      int textFieldWidth, int textFieldHeight) {
    super(fieldName, displayName, description);
    textField = new TextFieldWidget(textRenderer, 0, 0, textFieldWidth, textFieldHeight,
        Text.empty());
    textField.setChangedListener(this::onChanged);
    textField.setMaxLength(maxTextFieldLength);
  }

  protected abstract void initializeTextField();

  private void onChanged(String text) {
    onTextChanged(text);
    postTextChanged();
  }

  protected void onTextChanged(String text) {
    if (text.length() >= maxTextFieldLength) {
      while (text.length() >= maxTextFieldLength && maxTextFieldLength < 1_000_000_000) {
        maxTextFieldLength *= 2;
      }
      textField.setMaxLength(maxTextFieldLength);
    }
  }

  protected void postTextChanged() {
  }

  protected void renderAdditionalContent(DrawContext context, int x, int y, int width, int height,
      int mouseX, int mouseY, boolean hovered, float tickDelta) {
  }

  @Override
  protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX,
      int mouseY, boolean hovered, float tickDelta) {
    int entryWidth = getEntryContentWidth(width);

    renderAdditionalContent(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);

    textField.setX(getTextFieldPosX(x, entryWidth));
    textField.setY(getTextFieldPosY(y, height));
    textField.render(context, mouseX, mouseY, tickDelta);

    if (hasError()) {
      textField.setEditableColor(TextColors.PASTEL_RED);
    } else {
      textField.setEditableColor(TextColors.WHITE);
    }
  }

  protected int getTextFieldPosX(int x, int entryWidth) {
    return x + entryWidth - textField.getWidth() + TEXT_FIELD_OFFSET_X;
  }

  protected int getTextFieldPosY(int y, int height) {
    //return y + (height - textField.getHeight()) / 2; CENTER
    return y + TEXT_FIELD_OFFSET_Y;
  }

  @Override
  public void setFocused(boolean focused) {
    super.setFocused(focused);
    textField.setFocused(focused);
  }

  @Override
  protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
    textField.mouseClicked(mouseX, mouseY, button);
    return true;
  }

  @Override
  protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
    return textField.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected boolean charTypedEntry(char chr, int modifiers) {
    return textField.charTyped(chr, modifiers);
  }

  @Override
  protected void resetToDefault() {
    setText(getDefaultValueAsString());
    if (!hasCustomErrors()) {
      clearError();
    }
  }

  public void setText(String text) {
    onTextChanged(text);
    postTextChanged();
    textField.setText(text);
  }

  public boolean hasCustomErrors() {
    return false;
  }

  @Override
  public boolean modified() {
    return !Objects.equals(textField.getText(), getStartingValueAsString());
  }

  @Override
  public boolean canReset() {
    return !Objects.equals(textField.getText(), getDefaultValueAsString());
  }
}