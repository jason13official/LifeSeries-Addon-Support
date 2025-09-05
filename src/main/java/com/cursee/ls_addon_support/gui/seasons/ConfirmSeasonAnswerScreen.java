package com.cursee.ls_addon_support.gui.seasons;

import com.cursee.ls_addon_support.gui.DefaultSmallScreen;
import com.cursee.ls_addon_support.network.NetworkHandlerClient;
import com.cursee.ls_addon_support.render.RenderUtils;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ConfirmSeasonAnswerScreen extends DefaultSmallScreen {

  private final Screen parent;
  private final Seasons season;

  public ConfirmSeasonAnswerScreen(Screen parent, Seasons season) {
    super(Text.literal("Confirm Answer"), 2.2f, 1.6f);
    this.parent = parent;
    this.season = season;
  }

  @Override
  public boolean allowCloseButton() {
    return false;
  }

  @Override
  protected void init() {
    super.init();
    int startX = (this.width - BG_WIDTH) / 2;
    int startY = (this.height - BG_HEIGHT) / 2;
    int fifth2 = startX + (BG_WIDTH / 5) * 2;
    int fifth3 = startX + (BG_WIDTH / 5) * 3;

    this.addDrawableChild(
        ButtonWidget.builder(Text.literal("Confirm"), btn -> {
              this.close();
              NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, season.getName());
            })
            .position(fifth2 - 40, startY + BG_HEIGHT - 35)
            .size(60, 20)
            .build()
    );

    this.addDrawableChild(
        ButtonWidget.builder(Text.literal("Cancel"), btn -> {
                if (this.client != null) {
                    this.client.setScreen(parent);
                }
            })
            .position(fifth3 - 20, startY + BG_HEIGHT - 35)
            .size(60, 20)
            .build()
    );
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY) {
    Text prompt1 = Text.of("WARNING: you have already selected a season.");
    Text prompt2 = Text.of("Changing it might cause some saved data to be lost (lives, ...).");
    Text prompt3 = TextUtils.formatPlain("Change the season to {}?", season.getName());
    RenderUtils.drawTextCenter(context, textRenderer, prompt1, centerX, startY + 15);
    RenderUtils.drawTextCenter(context, textRenderer, prompt2, centerX,
        startY + 20 + textRenderer.fontHeight);
    RenderUtils.drawTextCenter(context, textRenderer, prompt3, centerX,
        startY + 35 + textRenderer.fontHeight * 2);
  }
}