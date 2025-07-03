package net.mat0u5.lifeseries.gui.other;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ChooseWildcardScreen extends DefaultScreen {

    public ChooseWildcardScreen() {
        super(Text.literal("Choose Wildcard Screen"));
    }

    @Override
    protected void init() {
        super.init();
        int oneThirdX = startX + BG_WIDTH / 3;
        int twoThirdX = startX + (BG_WIDTH / 3)*2;

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
    public void render(DrawContext context, int mouseX, int mouseY) {
        String prompt = "Select the Wildcard for this session.";
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(prompt), centerX, startY + 20);
    }
}
