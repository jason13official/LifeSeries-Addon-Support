package com.cursee.ls_addon_support.gui.config.entries.extra.time;

import com.cursee.ls_addon_support.gui.config.entries.ConfigEntry;
import com.cursee.ls_addon_support.gui.config.entries.interfaces.IPopup;
import com.cursee.ls_addon_support.gui.config.entries.interfaces.ITextFieldAddonPopup;
import com.cursee.ls_addon_support.gui.config.entries.main.DoubleConfigEntry;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MinutesConfigEntry extends DoubleConfigEntry implements ITextFieldAddonPopup {

  public MinutesConfigEntry(String fieldName, String displayName, String description, double value,
      double defaultValue) {
    super(fieldName, displayName, description, value, defaultValue);
  }

  @Override
  protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX,
      int mouseY, boolean hovered, float tickDelta) {
    super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    renderPopup(context, mouseX, mouseY, tickDelta);
  }

  @Override
  public TextFieldWidget getTextField() {
    return textField;
  }

  @Override
  public TextRenderer getTextRenderer() {
    return textRenderer;
  }

  @Override
  public Text getPopupText() {
    return Text.literal(OtherUtils.formatSecondsToReadable((int) (value * 60)))
        .formatted(Formatting.GRAY);
  }

  @Override
  public boolean shouldShowPopup() {
      if (textField == null) {
          return false;
      }
      if (hasError()) {
          return false;
      }

      if (isFocused()) {
          return true;
      }

    if (isHovered) {
      ConfigEntry entry = screen.getFocusedEntry();
        if (!(entry instanceof IPopup popup)) {
            return true;
        }
        if (popup == this) {
            return true;
        }
      return !popup.shouldShowPopup();

    }
    return false;
  }

  @Override
  public ConfigTypes getValueType() {
    return ConfigTypes.MINUTES;
  }
}
