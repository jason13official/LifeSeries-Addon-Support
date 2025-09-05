package com.cursee.ls_addon_support.gui.config.entries.main;

import com.cursee.ls_addon_support.gui.config.entries.EmptyConfigEntry;
import com.cursee.ls_addon_support.render.RenderUtils;
import com.cursee.ls_addon_support.utils.TextColors;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.interfaces.IEntryGroupHeader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TextConfigEntry extends EmptyConfigEntry implements IEntryGroupHeader {

  protected static final int TEXT_OFFSET_X = -5;
  protected static final int TEXT_OFFSET_Y = LABEL_OFFSET_Y;

  private final boolean clickable;
  public boolean clicked;

  public TextConfigEntry(String fieldName, String displayName, String description) {
    this(fieldName, displayName, description, true);
  }

  public TextConfigEntry(String fieldName, String displayName, String description,
      boolean clickable) {
    super(fieldName, displayName, description);
    this.clickable = clickable;
  }

  @Override
  protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX,
      int mouseY, boolean hovered, float tickDelta) {
      if (!clickable) {
          return;
      }
    String text = !clicked ? "Click to expand" : "Click to collapse";
    RenderUtils.drawTextRight(context, textRenderer, TextColors.LIGHT_GRAY_A128, Text.of(text),
        x + width + TEXT_OFFSET_X, y + TEXT_OFFSET_Y);
  }

  @Override
  protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
    if (clickable && button == 0) {
      clicked = !clicked;
    }
    return clickable;
  }

  @Override
  public ConfigTypes getValueType() {
    return ConfigTypes.TEXT;
  }

  @Override
  public void expand() {
    clicked = true;
  }

  @Override
  public boolean shouldExpand() {
    return clicked;
  }
}