package net.mat0u5.lifeseries.client.gui;

import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChooseWildcardScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE_LEFT = Identifier.of("lifeseries","textures/gui/trivia_question1.png");
    private static final Identifier BACKGROUND_TEXTURE_RIGHT = Identifier.of("lifeseries","textures/gui/trivia_question2.png");

    private static final int BG_WIDTH = 320;
    private static final int BG_HEIGHT = 180;

    public static final int TEXT_COLOR = 0x3c3c3c;

    public ChooseWildcardScreen() {
        super(Text.literal("Choose Wildcard Screen"));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void init() {
        super.init();
        int startX = (this.width - BG_WIDTH) / 2;
        int oneThirdX = startX + BG_WIDTH / 3;
        int twoThirdX = startX + (BG_WIDTH / 3)*2;
        int endX = startX + BG_WIDTH;
        int startY = (this.height - BG_HEIGHT) / 2;

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Size Shifting"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","size_shifting");
                        })
                        .position(oneThirdX - 40, startY  + 45)
                        .size(80, 20)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Hunger"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","hunger");
                        })
                        .position(oneThirdX - 40, startY  + 75)
                        .size(80, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Snails"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","snails");
                        })
                        .position(oneThirdX - 40, startY  + 105)
                        .size(80, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Time Dilation"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","time_dilation");
                        })
                        .position(oneThirdX - 40, startY  + 135)
                        .size(80, 20)
                        .build()
        );


        /*

         */

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Trivia"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","trivia");
                        })
                        .position(twoThirdX - 40, startY  + 45)
                        .size(80, 20)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Mob Swap"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","mob_swap");
                        })
                        .position(twoThirdX - 40, startY  + 75)
                        .size(80, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Superpowers"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","superpowers");
                        })
                        .position(twoThirdX - 40, startY  + 105)
                        .size(80, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Callback"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            NetworkHandlerClient.sendStringPacket("selected_wildcard","callback");
                        })
                        .position(twoThirdX - 40, startY  + 135)
                        .size(80, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        // X
        int startX = (this.width - BG_WIDTH) / 2;
        int endX = startX + BG_WIDTH;
        int centerX = (startX + endX) / 2;

        // Y
        int startY = (this.height - BG_HEIGHT) / 2;
        int endY = startY + BG_HEIGHT;
        int centerY = (startY + endY) / 2;

        // Background
        //? if <= 1.21 {
        context.drawTexture(BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT);
        context.drawTexture(BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT);
        //?} else {
        /*context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256);
        *///?}

        String prompt = "Select the Wildcard for this session.";
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(prompt), centerX, startY + 20);

        super.render(context, mouseX, mouseY, delta);
    }
}
