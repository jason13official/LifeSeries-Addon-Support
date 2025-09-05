package com.cursee.ls_addon_support.gui.other;

import com.cursee.ls_addon_support.gui.DefaultScreen;
import com.cursee.ls_addon_support.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SnailTextureInfoScreen extends DefaultScreen {

  public SnailTextureInfoScreen() {
    super(Text.of("Snail Textures Info"), 1.4f, 1.4f);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY) {
    RenderUtils.drawTextCenter(context, textRenderer, Text.of("§0§nSnail Textures Info"), centerX,
        startY + 10);
    int currentY = startY + 30;
    currentY += 5 + RenderUtils.drawTextLeftWrapLines(context, textRenderer, DEFAULT_TEXT_COLOR,
        Text.of(
            "To create your custom snail textures, you must first open the snail model (§8./config/lifeseries/wildlife/snailskins/snail.bbmodel§r) in Blockbench."),
        startX + 15, currentY, backgroundWidth - 30, 5);
    currentY += 5 + RenderUtils.drawTextLeftWrapLines(context, textRenderer, DEFAULT_TEXT_COLOR,
        Text.of(
            "After that, you should hide the parachute and propeller layers, as they just get in the way and are usually not visible anyway."),
        startX + 15, currentY, backgroundWidth - 30, 5);
    currentY += 5 + RenderUtils.drawTextLeftWrapLines(context, textRenderer, DEFAULT_TEXT_COLOR,
        Text.of(
            "Then, after you paint the skin however you wish, you need to save the skin, change the texture file name to the username of the player it belongs to. Then, put it in the config folder (§8./config/lifeseries/wildlife/snailskins/§r) on the server. Then reload, and it should be added."),
        startX + 15, currentY, backgroundWidth - 30, 5);
    currentY += 15 + RenderUtils.drawTextLeftWrapLines(context, textRenderer, DEFAULT_TEXT_COLOR,
        Text.of(
            "So for example, since my minecraft username is Mat0u5, the file would be located at §8§n./config/lifeseries/wildlife/snailskins/Mat0u5.png"),
        startX + 15, currentY, backgroundWidth - 30, 5);
    currentY += 5 + RenderUtils.drawTextLeftWrapLines(context, textRenderer, DEFAULT_TEXT_COLOR,
        Text.of(
            "This is a pretty complicated process and it requires knowledge of Blockbench. If you have any questions, join the discord (Use the §8'/lifeseries discord'§r command) or just try to google it."),
        startX + 15, currentY, backgroundWidth - 30, 5);
  }
}
