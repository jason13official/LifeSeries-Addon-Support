package net.mat0u5.lifeseries.client.gui.series;

import net.mat0u5.lifeseries.client.gui.trivia.QuizScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.Trivia;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfirmSeriesAnswerScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE_LEFT = Identifier.of("lifeseries","textures/gui/trivia_question1.png");
    private static final Identifier BACKGROUND_TEXTURE_RIGHT = Identifier.of("lifeseries","textures/gui/trivia_question2.png");

    public static final int TEXT_COLOR = 0x3c3c3c;

    private static final int BG_WIDTH = 320;
    private static final int BG_HEIGHT = 180;

    private final ChooseSeriesScreen parent;
    private final String seriesName;

    public ConfirmSeriesAnswerScreen(ChooseSeriesScreen parent, String seriesName) {
        super(Text.literal("Confirm Answer"));
        this.parent = parent;
        this.seriesName = seriesName;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void init() {
        super.init();
        int startX = (this.width - BG_WIDTH) / 2;
        int startY = (this.height - BG_HEIGHT) / 2;
        int oneFourthX = startX + BG_WIDTH / 4;
        int threeFourthX = startX + (BG_WIDTH / 4)*3;

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Confirm"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("set_series", seriesName);
                        })
                        .position(oneFourthX - 30, startY + BG_HEIGHT - 40)
                        .size(60, 20)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Cancel"), btn -> {
                            if (this.client != null) this.client.setScreen(parent);
                        })
                        .position(threeFourthX - 30, startY + BG_HEIGHT - 40)
                        .size(60, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int startX = (this.width - BG_WIDTH) / 2;
        int startY = (this.height - BG_HEIGHT) / 2;
        //? if <= 1.21 {
        context.drawTexture(BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT);
        context.drawTexture(BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT);
        //?} else {
        /*context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256);
        *///?}


        String prompt = "WARNING: you have already selected a series.";
        int textWidth = textRenderer.getWidth(prompt);
        int textX = startX + (BG_WIDTH - textWidth) / 2;
        int textY = startY + 30;
        context.drawText(textRenderer, prompt, textX, textY, TEXT_COLOR, false);


        String prompt3 = "Changing it might cause some saved data to be lost (lives, ...).";
        int textWidth3 = textRenderer.getWidth(prompt3);
        int textX3 = startX + (BG_WIDTH - textWidth3) / 2;
        int textY3 = startY + 30 + textRenderer.fontHeight + 5;
        context.drawText(textRenderer, prompt3, textX3, textY3, TEXT_COLOR, false);

        String prompt2 = "Change the series to " + seriesName + "?";
        int textWidth2 = textRenderer.getWidth(prompt2);
        int textX2 = startX + (BG_WIDTH - textWidth2) / 2;
        int textY2 = startY + 40 + (textRenderer.fontHeight + 5)*2;
        context.drawText(textRenderer, prompt2, textX2, textY2, TEXT_COLOR, false);

        super.render(context, mouseX, mouseY, delta);
    }
}