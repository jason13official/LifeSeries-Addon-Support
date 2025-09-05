package com.cursee.ls_addon_support.gui.config.entries.interfaces;

import com.cursee.ls_addon_support.utils.TextColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;

public interface ITextFieldAddonPopup extends ITextPopup {

  TextFieldWidget getTextField();

  @Override
  default void renderBackground(DrawContext context, int x, int y, int width, int height,
      int mouseX, int mouseY, float tickDelta) {
    TextFieldWidget textField = getTextField();
    context.fill(x, y, x + width, y + height, TextColors.LIGHT_BLACK);
    context.drawBorder(x, y, width, height, TextColors.DARK_GRAY);
    context.drawBorder(x - 1, y - 1, width + 2, height + 1,
        textField.isFocused() ? TextColors.WHITE_BORDER : TextColors.LIGHT_GRAY);

    int textFieldX = textField.getX();
    int textFieldY = textField.getY();
    if (textField.getWidth() <= width) {
      context.fill(textFieldX + 1, textFieldY, textFieldX + textField.getWidth() - 1,
          textFieldY + 1, TextColors.DARK_GRAY);
    } else {
      context.fill(x, textFieldY, x + width, textFieldY + 1, TextColors.DARK_GRAY);
    }
  }

  default void renderPopup(DrawContext context, int mouseX, int mouseY, float tickDelta) {
      if (!shouldShowPopup()) {
          return;
      }
    TextFieldWidget textField = getTextField();
    int popupWidth = getActualPopupWidth();
    int popupX = textField.getX() + textField.getWidth() / 2 - popupWidth / 2;
    int popupY = Math.max(0, textField.getY() - getActualPopupHeight()) + 1;
    renderPopup(context, popupX, popupY, mouseX, mouseY, tickDelta);
  }
}
