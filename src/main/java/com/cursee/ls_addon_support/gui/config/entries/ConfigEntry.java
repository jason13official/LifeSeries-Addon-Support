package com.cursee.ls_addon_support.gui.config.entries;

import com.cursee.ls_addon_support.gui.config.ConfigScreen;
import com.cursee.ls_addon_support.render.RenderUtils;
import com.cursee.ls_addon_support.utils.TextColors;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class ConfigEntry {

  public static final int PREFFERED_HEIGHT = 20;
  public static final int MAX_DESCRIPTION_WIDTH = 250;
  protected static final int LABEL_OFFSET_X = 25;
  protected static final int LABEL_OFFSET_Y = 6;
  protected static final int RESET_BUTTON_WIDTH = 50;
  protected static final int ERROR_LABEL_OFFSET_Y = 8;
  private static final float HIGHTLIGHT_FADE = 0.1f;
  private static final int RESET_BUTTON_OFFSET_X = -5;
  protected static final int ERROR_LABEL_OFFSET_X = -RESET_BUTTON_WIDTH + RESET_BUTTON_OFFSET_X - 2;
  private static final int RESET_BUTTON_OFFSET_Y = 2;
  private static final int RESET_BUTTON_HEIGHT = 16;
  protected final String fieldName;
  protected final String displayName;
  protected final String description;
  public float highlightAlpha = 0.0f;
  protected TextRenderer textRenderer;
  protected ConfigScreen screen;
  protected boolean hasError = false;
  protected String errorMessage = "";
  protected ButtonWidget resetButton;
  protected boolean isHovered = false;
  protected GroupConfigEntry<?> parentGroup;
  private boolean isFocused = false;

  public ConfigEntry(String fieldName, String displayName, String description) {
    this.fieldName = fieldName;
    this.displayName = displayName;
    this.description = description;
    this.textRenderer = MinecraftClient.getInstance().textRenderer;
    initializeResetButton();
  }

  private void initializeResetButton() {
    if (!hasResetButton()) {
      return;
    }
    resetButton = ButtonWidget.builder(Text.of("Reset"), this::onResetClicked)
        .dimensions(0, 0, RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT)
        .build();
  }

  private void onResetClicked(ButtonWidget button) {
    resetToDefault();
    markChanged();
  }

  public void setScreen(ConfigScreen screen) {
    this.screen = screen;
  }

  public void render(DrawContext context, int x, int y, int width, int height, int mouseX,
      int mouseY, boolean hovered, float tickDelta) {
    isHovered = hovered;
    updateHighlightAnimation(tickDelta);

    if (highlightAlpha > 0.0f) {
      int highlightColor = TextColors.argb((int) (highlightAlpha * 128), 128, 128, 128);
      context.fill(x, y, x + width, y + height, highlightColor);
    }

    int textColor = hasError() ? TextColors.PASTEL_RED : TextColors.WHITE;
    int labelX = x + LABEL_OFFSET_X;
    int labelY = y + LABEL_OFFSET_Y;
    context.drawTextWithShadow(textRenderer, getDisplayName(), labelX, labelY, textColor);

    int resetButtonX = x + width - RESET_BUTTON_WIDTH + RESET_BUTTON_OFFSET_X;
    if (hasResetButton()) {
      resetButton.setX(resetButtonX);
      resetButton.setY(y + RESET_BUTTON_OFFSET_Y);
      resetButton.active = canReset();
      resetButton.render(context, mouseX, mouseY, tickDelta);
    }

    if (hasError()) {
      RenderUtils.drawTextRight(context, textRenderer, TextColors.PASTEL_RED, Text.of("⚠"),
          x + width + ERROR_LABEL_OFFSET_X, y + ERROR_LABEL_OFFSET_Y);
      if (isHovered) {
        Text errorText = TextUtils.format("§cERROR:\n{}", getErrorMessage());
          context.drawTooltip(textRenderer, textRenderer.wrapLines(errorText, MAX_DESCRIPTION_WIDTH), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY, false);
      }
    } else if (description != null && !description.isEmpty()) {
      if (mouseX >= labelX && mouseX <= labelX + textRenderer.getWidth(getDisplayName()) &&
          mouseY >= labelY && mouseY <= labelY + textRenderer.fontHeight) {
        Text descriptionText = getDisplayName().formatted(Formatting.UNDERLINE)
            .append("§r\n" + description);
          context.drawTooltip(textRenderer, textRenderer.wrapLines(descriptionText, MAX_DESCRIPTION_WIDTH), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY, false);
      }
    }

    renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
  }

  protected void updateHighlightAnimation(float tickDelta) {
    if (isHovered) {
      highlightAlpha = 1.0f;
    } else {
      highlightAlpha = Math.max(0.0f, highlightAlpha - tickDelta * HIGHTLIGHT_FADE);
    }
  }

  protected int getEntryContentWidth(int totalWidth) {
    return totalWidth - RESET_BUTTON_WIDTH - 15;
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (hasResetButton() && resetButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }
    return mouseClickedEntry(mouseX, mouseY, button);
  }

  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return keyPressedEntry(keyCode, scanCode, modifiers);
  }

  public boolean charTyped(char chr, int modifiers) {
    return charTypedEntry(chr, modifiers);
  }

  protected void setActualFocused(boolean focused) {
    this.isFocused = focused;
  }

  public boolean isFocused() {
    return isFocused;
  }

  public void setFocused(boolean focused) {
    setActualFocused(focused);
    if (focused && screen != null) {
      screen.setFocusedEntry(this);
    }
  }

  public int getPreferredHeight() {
    return PREFFERED_HEIGHT;
  }

  protected abstract void renderEntry(DrawContext context, int x, int y, int width, int height,
      int mouseX, int mouseY, boolean hovered, float tickDelta);

  protected abstract boolean mouseClickedEntry(double mouseX, double mouseY, int button);

  protected abstract boolean keyPressedEntry(int keyCode, int scanCode, int modifiers);

  protected abstract boolean charTypedEntry(char chr, int modifiers);

  protected abstract void resetToDefault();

  public abstract Object getValue();

  public abstract void setValue(Object value);

  public abstract String getValueAsString();

  public abstract Object getDefaultValue();

  public abstract String getDefaultValueAsString();

  public abstract Object getStartingValue();

  public abstract String getStartingValueAsString();

  public abstract ConfigTypes getValueType();

  public boolean modified() {
    return !Objects.equals(getValue(), getStartingValue());
  }

  public boolean canReset() {
    return !Objects.equals(getValue(), getDefaultValue());
  }

  public String getFieldName() {
    return fieldName;
  }

  public MutableText getDisplayName() {
    return Text.literal(displayName);
  }

  public String getDescription() {
    return description;
  }

  public boolean hasError() {
    return hasError;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  protected void setError(String errorMessage) {
    this.hasError = true;
    this.errorMessage = errorMessage;
  }

  protected void clearError() {
    this.hasError = false;
    this.errorMessage = "";
  }

  protected void markChanged() {
    if (screen != null) {
      screen.onEntryValueChanged();
    }
  }

  protected boolean hasResetButton() {
    return true;
  }
}