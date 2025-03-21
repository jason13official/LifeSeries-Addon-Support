package net.mat0u5.lifeseries.client.gui.series;

import net.mat0u5.lifeseries.client.gui.trivia.ConfirmQuizAnswerScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChooseSeriesScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE_LEFT = Identifier.of("lifeseries","textures/gui/trivia_question1.png");
    private static final Identifier BACKGROUND_TEXTURE_RIGHT = Identifier.of("lifeseries","textures/gui/trivia_question2.png");

    private static final Identifier TEXTURE_SELECTED = Identifier.of("lifeseries","textures/gui/selected.png");

    private static final Identifier TEXTURE_THIRDLIFE = Identifier.of("lifeseries","textures/gui/thirdlife.png");
    private static final Identifier TEXTURE_LASTLIFE = Identifier.of("lifeseries","textures/gui/lastlife.png");
    private static final Identifier TEXTURE_DOUBLELIFE = Identifier.of("lifeseries","textures/gui/doublelife.png");
    private static final Identifier TEXTURE_LIMITEDLIFE = Identifier.of("lifeseries","textures/gui/limitedlife.png");
    private static final Identifier TEXTURE_SECRETLIFE = Identifier.of("lifeseries","textures/gui/secretlife.png");
    private static final Identifier TEXTURE_WILDLIFE = Identifier.of("lifeseries","textures/gui/wildlife.png");


    private static final int BG_WIDTH = 320;
    private static final int BG_HEIGHT = 180;

    public static final int TEXT_COLOR = 0x3c3c3c;
    public static boolean hasSelectedBefore = false;

    public ChooseSeriesScreen(boolean hasSelectedBefore) {
        super(Text.literal("Choose Series Screen"));
        this.hasSelectedBefore = hasSelectedBefore;
    }

    public int getRegion(int x, int y) {
        // X
        int startX = (this.width - BG_WIDTH) / 2;
        int endX = startX + BG_WIDTH;
        int centerX = (startX + endX) / 2;

        int oneFourthX = startX + BG_WIDTH / 4;
        int threeFourthX = startX + (BG_WIDTH / 4)*3;

        // Y
        int startY = (this.height - BG_HEIGHT) / 2;

        if (Math.abs(y - (startY + 32 + 32)) < 32) {
            if (Math.abs(x - oneFourthX) < 32) return 1;
            if (Math.abs(x - centerX) < 32) return 2;
            if (Math.abs(x - threeFourthX) < 32) return 3;
        }
        if (Math.abs(y - (startY + 96 + 32)) < 32) {
            if (Math.abs(x - oneFourthX) < 32) return 4;
            if (Math.abs(x - centerX) < 32) return 5;
            if (Math.abs(x - threeFourthX) < 32) return 6;
        }
        return 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 0) { // Left-click
            int region = getRegion((int) mouseX, (int) mouseY);
            if (region != 0) {
                if (hasSelectedBefore) {
                    if (region == 1) this.client.setScreen(new ConfirmSeriesAnswerScreen(this, "Third Life"));
                    if (region == 2) this.client.setScreen(new ConfirmSeriesAnswerScreen(this, "Last Life"));
                    if (region == 3) this.client.setScreen(new ConfirmSeriesAnswerScreen(this, "Double Life"));
                    if (region == 4) this.client.setScreen(new ConfirmSeriesAnswerScreen(this, "Limited Life"));
                    if (region == 5) this.client.setScreen(new ConfirmSeriesAnswerScreen(this, "Secret Life"));
                    if (region == 6) this.client.setScreen(new ConfirmSeriesAnswerScreen(this, "Wild Life"));
                }
                else {
                    if (region == 1) NetworkHandlerClient.sendStringPacket("set_series", "Third Life");
                    if (region == 2) NetworkHandlerClient.sendStringPacket("set_series", "Last Life");
                    if (region == 3) NetworkHandlerClient.sendStringPacket("set_series", "Double Life");
                    if (region == 4) NetworkHandlerClient.sendStringPacket("set_series", "Limited Life");
                    if (region == 5) NetworkHandlerClient.sendStringPacket("set_series", "Secret Life");
                    if (region == 6) NetworkHandlerClient.sendStringPacket("set_series", "Wild Life");
                    if (this.client != null) this.client.setScreen(null);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        // X
        int startX = (this.width - BG_WIDTH) / 2;
        int endX = startX + BG_WIDTH;
        int centerX = (startX + endX) / 2;

        int oneFourthX = startX + BG_WIDTH / 4;
        int threeFourthX = startX + (BG_WIDTH / 4)*3;

        // Y
        int startY = (this.height - BG_HEIGHT) / 2;

        int region = getRegion(mouseX, mouseY);

        // Background + images
        //? if <= 1.21 {
        context.drawTexture(BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT);
        context.drawTexture(BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT);

        if (region != 0) {
            if (region == 1) context.drawTexture(TEXTURE_SELECTED,oneFourthX-32, startY + 32, 0, 0, 64, 64);
            if (region == 2) context.drawTexture(TEXTURE_SELECTED,centerX-32, startY + 32, 0, 0, 64, 64);
            if (region == 3) context.drawTexture(TEXTURE_SELECTED,threeFourthX-32, startY + 32, 0, 0, 64, 64);

            if (region == 4) context.drawTexture(TEXTURE_SELECTED,oneFourthX-32, startY + 96, 0, 0, 64, 64);
            if (region == 5) context.drawTexture(TEXTURE_SELECTED,centerX-32, startY + 96, 0, 0, 64, 64);
            if (region == 6) context.drawTexture(TEXTURE_SELECTED,threeFourthX-32, startY + 96, 0, 0, 64, 64);
        }

        context.getMatrices().push();
        context.getMatrices().scale(0.25f, 0.25f, 1.0f);

        context.drawTexture(TEXTURE_THIRDLIFE, (oneFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_LASTLIFE, (centerX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_DOUBLELIFE, (threeFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_LIMITEDLIFE, (oneFourthX-32) * 4, (startY + 96) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_SECRETLIFE, (centerX-32) * 4, (startY + 96) * 4, 0, 0, 256, 256);
        context.drawTexture(TEXTURE_WILDLIFE, (threeFourthX-32) * 4, (startY + 96) * 4, 0, 0, 256, 256);


        context.getMatrices().pop();
        //?} else {
        /*context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256);

        if (region != 0) {
            if (region == 1) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, oneFourthX-32, startY + 32, 0, 0, 64, 64, 64, 64);
            if (region == 2) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, centerX-32, startY + 32, 0, 0, 64, 64, 64, 64);
            if (region == 3) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, threeFourthX-32, startY + 32, 0, 0, 64, 64, 64, 64);

            if (region == 4) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, oneFourthX-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 5) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, centerX-32, startY + 96, 0, 0, 64, 64, 64, 64);
            if (region == 6) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, threeFourthX-32, startY + 96, 0, 0, 64, 64, 64, 64);
        }

        context.getMatrices().push();
        context.getMatrices().scale(0.25f, 0.25f, 1.0f);

        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_THIRDLIFE, (oneFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_LASTLIFE, (centerX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_DOUBLELIFE, (threeFourthX-32) * 4, (startY + 32) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_LIMITEDLIFE, (oneFourthX-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SECRETLIFE, (centerX-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_WILDLIFE, (threeFourthX-32) * 4, (startY + 96) * 4, 0, 0, 256, 256, 256, 256);


        context.getMatrices().pop();
        *///?}

        String prompt = "Select the series you want to play.";
        drawTextCenter(context, Text.of(prompt), centerX, startY + 20);

        super.render(context, mouseX, mouseY, delta);
    }

    public void drawTextCenter(DrawContext context, Text text, int x, int y) {
        context.drawText(this.textRenderer, text, x - this.textRenderer.getWidth(text)/2, y, TEXT_COLOR, false);
    }
}

