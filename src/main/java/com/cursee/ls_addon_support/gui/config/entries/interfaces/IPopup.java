package com.cursee.ls_addon_support.gui.config.entries.interfaces;

import com.cursee.ls_addon_support.utils.TextColors;
import net.minecraft.client.gui.DrawContext;

public interface IPopup {

  boolean shouldShowPopup();

  int getPopupWidth();

  int getPopupHeight();

  void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX,
      int mouseY, float tickDelta);

  default int getActualPopupWidth() {
    return getPopupWidth() + getPadding();
  }

  default int getActualPopupHeight() {
    return getPopupHeight() + getPadding();
  }

  default int getPadding() {
    return 4;
  }

  default void renderPopup(DrawContext context, int x, int y, int mouseX, int mouseY,
      float tickDelta) {
      if (!shouldShowPopup()) {
          return;
      }
    context.getMatrices().pushMatrix();
    int width = getActualPopupWidth();
    int height = getActualPopupHeight();
    renderBackground(context, x, y, width, height, mouseX, mouseY, tickDelta);
    renderContent(context, x + getPadding() / 2, y + getPadding() / 2, width, height, mouseX,
        mouseY, tickDelta);
    context.getMatrices().popMatrix();
  }

  default void renderBackground(DrawContext context, int x, int y, int width, int height,
      int mouseX, int mouseY, float tickDelta) {
    context.fill(x, y, x + width, y + height, TextColors.LIGHT_BLACK);
    context.drawBorder(x, y, width, height, TextColors.LIGHT_GRAY);
  }
}