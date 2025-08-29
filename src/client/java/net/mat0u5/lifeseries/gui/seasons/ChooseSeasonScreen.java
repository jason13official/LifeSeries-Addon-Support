package net.mat0u5.lifeseries.gui.seasons;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
//? if >= 1.21.2 && <= 1.21.5
/*import net.minecraft.client.render.RenderLayer;*/
//? if >= 1.21.6
/*import net.minecraft.client.gl.RenderPipelines;*/

public class ChooseSeasonScreen extends DefaultScreen {

    private static final Identifier TEXTURE_SELECTED = Identifier.of("lifeseries","textures/gui/selected.png");

    private static final Identifier TEXTURE_THIRDLIFE = Seasons.THIRD_LIFE.getLogo();
    private static final Identifier TEXTURE_LASTLIFE = Seasons.LAST_LIFE.getLogo();
    private static final Identifier TEXTURE_DOUBLELIFE = Seasons.DOUBLE_LIFE.getLogo();
    private static final Identifier TEXTURE_LIMITEDLIFE = Seasons.LIMITED_LIFE.getLogo();
    private static final Identifier TEXTURE_SECRETLIFE = Seasons.SECRET_LIFE.getLogo();
    private static final Identifier TEXTURE_WILDLIFE = Seasons.WILD_LIFE.getLogo();
    private static final Identifier TEXTURE_PASTLIFE = Seasons.PAST_LIFE.getLogo();

    public static boolean hasSelectedBefore = false;

    public ChooseSeasonScreen(boolean hasSelectedBefore) {
        super(Text.literal("Choose Season Screen"), 1f, 1.03f);
        this.hasSelectedBefore = hasSelectedBefore;
    }

    public int getRegion(int x, int y) {
        // X
        int startX = (this.width - BG_WIDTH) / 2;
        int endX = startX + BG_WIDTH;
        int centerX = (startX + endX) / 2;

        int oneFourthX = startX + BG_WIDTH / 4;
        int threeFourthX = startX + (BG_WIDTH / 4)*3;

        int col4_1 = startX + BG_WIDTH / 5 - 10;
        int col4_2 = startX + (BG_WIDTH / 5)*2 - 5;
        int col4_3 = startX + (BG_WIDTH / 5)*3 + 5;
        int col4_4 = startX + (BG_WIDTH / 5)*4 + 10;

        // Y
        int startY = (this.height - BG_HEIGHT) / 2;

        if (Math.abs(y - (startY + 32 + 32)) < 32) {
            if (Math.abs(x - oneFourthX) < 32) return 1;
            if (Math.abs(x - centerX) < 32) return 2;
            if (Math.abs(x - threeFourthX) < 32) return 3;
        }
        if (Math.abs(y - (startY + 96 + 32)) < 32) {
            if (Math.abs(x - col4_1) < 32) return 4;
            if (Math.abs(x - col4_2) < 32) return 5;
            if (Math.abs(x - col4_3) < 32) return 6;
            if (Math.abs(x - col4_4) < 32) return 7;
        }

        Text aprilFools = Text.of("April Fools Seasons");
        int textWidth = textRenderer.getWidth(aprilFools);
        int textHeight = textRenderer.fontHeight;
        Rectangle rect = new Rectangle(endX-9-textWidth, endY-9-textHeight, textWidth+1, textHeight+1);

        if (x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height) {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left-click
            int region = getRegion((int) mouseX, (int) mouseY);
            if (region == -1 && this.client != null) {
                this.client.setScreen(new ChooseExtraSeasonScreen(hasSelectedBefore));
                return true;
            }
            else if (region != 0) {
                if (hasSelectedBefore && this.client != null) {
                    if (region == 1) this.client.setScreen(new ConfirmSeasonAnswerScreen(this, Seasons.THIRD_LIFE.getName()));
                    if (region == 2) this.client.setScreen(new ConfirmSeasonAnswerScreen(this, Seasons.LAST_LIFE.getName()));
                    if (region == 3) this.client.setScreen(new ConfirmSeasonAnswerScreen(this, Seasons.DOUBLE_LIFE.getName()));
                    if (region == 4) this.client.setScreen(new ConfirmSeasonAnswerScreen(this, Seasons.LIMITED_LIFE.getName()));
                    if (region == 5) this.client.setScreen(new ConfirmSeasonAnswerScreen(this, Seasons.SECRET_LIFE.getName()));
                    if (region == 6) this.client.setScreen(new ConfirmSeasonAnswerScreen(this, Seasons.WILD_LIFE.getName()));
                    if (region == 7) this.client.setScreen(new ConfirmSeasonAnswerScreen(this, Seasons.PAST_LIFE.getName()));
                }
                else {
                    if (region == 1) NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, Seasons.THIRD_LIFE.getName());
                    if (region == 2) NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, Seasons.LAST_LIFE.getName());
                    if (region == 3) NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, Seasons.DOUBLE_LIFE.getName());
                    if (region == 4) NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, Seasons.LIMITED_LIFE.getName());
                    if (region == 5) NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, Seasons.SECRET_LIFE.getName());
                    if (region == 6) NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, Seasons.WILD_LIFE.getName());
                    if (region == 7) NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, Seasons.PAST_LIFE.getName());
                    if (this.client != null) this.client.setScreen(null);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        int oneFourthX = startX + BG_WIDTH / 4;
        int threeFourthX = startX + (BG_WIDTH / 4)*3;

        int col4_1 = startX + BG_WIDTH / 5 - 10;
        int col4_2 = startX + (BG_WIDTH / 5)*2 - 5;
        int col4_3 = startX + (BG_WIDTH / 5)*3 + 5;
        int col4_4 = startX + (BG_WIDTH / 5)*4 + 10;

        int region = getRegion(mouseX, mouseY);

        // Background + images
        //? if <= 1.21 {

        if (region != 0) {
            if (region == 1) context.drawTexture(TEXTURE_SELECTED,oneFourthX-32, startY + 32, 0, 0, 64, 64);
            if (region == 2) context.drawTexture(TEXTURE_SELECTED,centerX-32, startY + 32, 0, 0, 64, 64);
            if (region == 3) context.drawTexture(TEXTURE_SELECTED,threeFourthX-32, startY + 32, 0, 0, 64, 64);

            if (region == 4) context.drawTexture(TEXTURE_SELECTED,col4_1-32, startY + 96, 0, 0, 64, 64);
            if (region == 5) context.drawTexture(TEXTURE_SELECTED,col4_2-32, startY + 96, 0, 0, 64, 64);
            if (region == 6) context.drawTexture(TEXTURE_SELECTED,col4_3-32, startY + 96, 0, 0, 64, 64);
            if (region == 7) context.drawTexture(TEXTURE_SELECTED,col4_4-32, startY + 96, 0, 0, 64, 64);
        }

        context.getMatrices().push();
        context.getMatrices().scale(0.25f, 0.25f, 1.0f);

        context.drawTexture(TEXTURE_THIRDLIFE, (oneFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_LASTLIFE, (centerX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_DOUBLELIFE, (threeFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256);

        context.drawTexture(TEXTURE_LIMITEDLIFE, (col4_1-32) * 4, (startY + 96) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_SECRETLIFE, (col4_2-32) * 4, (startY + 96) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_WILDLIFE, (col4_3-32) * 4, (startY + 96) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_PASTLIFE, (col4_4-32) * 4, (startY + 96) * 4, 0, 0, 256, 256);


        context.getMatrices().pop();
        //?} else if <= 1.21.5 {
        /*if (region != 0) {
            if (region == 1) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, oneFourthX-32, startY + 32, 0, 0, 64, 64, 64, 64);
            if (region == 2) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, centerX-32, startY + 32, 0, 0, 64, 64, 64, 64);
            if (region == 3) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, threeFourthX-32, startY + 32, 0, 0, 64, 64, 64, 64);

            if (region == 4) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, col4_1-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 5) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, col4_2-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 6) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, col4_3-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 7) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, col4_4-32, startY + 96, 0, 0, 64, 64, 64, 64);
        }

        context.getMatrices().push();
        context.getMatrices().scale(0.25f, 0.25f, 1.0f);

        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_THIRDLIFE, (oneFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_LASTLIFE, (centerX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_DOUBLELIFE, (threeFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);

        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_LIMITEDLIFE, (col4_1-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SECRETLIFE, (col4_2-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_WILDLIFE, (col4_3-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_PASTLIFE, (col4_4-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);


        context.getMatrices().pop();
        *///?} else {
        /*if (region != 0) {
            if (region == 1) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, oneFourthX-32, startY + 32, 0, 0, 64, 64, 64, 64);
            if (region == 2) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, centerX-32, startY + 32, 0, 0, 64, 64, 64, 64);
            if (region == 3) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, threeFourthX-32, startY + 32, 0, 0, 64, 64, 64, 64);

            if (region == 4) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, col4_1-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 5) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, col4_2-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 6) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, col4_3-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 7) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, col4_4-32, startY + 96, 0, 0, 64, 64, 64, 64);
        }

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(0.25f, 0.25f);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_THIRDLIFE, (oneFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_LASTLIFE, (centerX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_DOUBLELIFE, (threeFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_LIMITEDLIFE, (col4_1-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SECRETLIFE, (col4_2-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_WILDLIFE, (col4_3-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_PASTLIFE, (col4_4-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);


        context.getMatrices().popMatrix();
        *///?}

        String prompt = "Select the season you want to play.";
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(prompt), centerX, startY + 20);

        Text aprilFools = Text.of("April Fools Seasons");
        int textWidth = textRenderer.getWidth(aprilFools);
        int textHeight = textRenderer.fontHeight;

        Rectangle rect = new Rectangle(endX-9-textWidth, endY-9-textHeight, textWidth+1, textHeight+1);

        context.fill(rect.x - 1, rect.y - 1, rect.x + rect.width + 1, rect.y, DEFAULT_TEXT_COLOR); // top border
        context.fill(rect.x - 1, rect.y + rect.height, rect.x + rect.width + 2, rect.y + rect.height + 2, DEFAULT_TEXT_COLOR); // bottom
        context.fill(rect.x - 1, rect.y, rect.x, rect.y + rect.height, DEFAULT_TEXT_COLOR); // left
        context.fill(rect.x + rect.width, rect.y-1, rect.x + rect.width + 2, rect.y + rect.height, DEFAULT_TEXT_COLOR); // right

        if (region == -1) {
            RenderUtils.drawTextLeft(context, this.textRenderer, TextColors.WHITE, aprilFools, rect.x+1, rect.y+1);
        }
        else {
            RenderUtils.drawTextLeft(context, this.textRenderer, DEFAULT_TEXT_COLOR, aprilFools, rect.x+1, rect.y+1);
        }
    }
}

