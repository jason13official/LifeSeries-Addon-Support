package net.mat0u5.lifeseries.client.render;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.client.ClientKeybinds;
import net.mat0u5.lifeseries.mixin.client.MinecraftClientMixin;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.Trivia;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TextRenderer {
    private static long renderTicks = 0;
    public static void renderText(DrawContext context) {
        renderTicks++;
        MinecraftClient client = MinecraftClient.getInstance();
        int yPos = client.getWindow().getScaledHeight() - 5;
        yPos += renderGameNotBroken(client, context, yPos);
        yPos += renderMimicryTimer(client, context, yPos);
        yPos += renderSuperpowerCooldown(client, context, yPos);
        yPos += renderTriviaTimer(client, context, yPos);
    }

    public static int renderGameNotBroken(MinecraftClient client, DrawContext context, int y) {
        if (client.player == null) return 0;
        if (!ClientRenderUtils.isGameFullyFrozen) return 0;
        long currentMillis = System.currentTimeMillis();
        int guiScale = client.options.getGuiScale().getValue();
        if (guiScale <= 3 && guiScale != 0) {

            String textString = "Don't worry, the game is not broken ";
            if (currentMillis % 1500 <= 750) textString = "§7§n"+textString;
            else textString = "§7"+textString;

            if (currentMillis % 500 <= 250) textString += "/o/";
            else textString += "\\o\\";

            Text text = Text.literal(textString);


            int screenWidth = client.getWindow().getScaledWidth();
            int x = screenWidth - 5;
            renderTextLeft(context, text, x, y);

            return -client.textRenderer.fontHeight-10;
        }
        else {
            String textString0 = "Don't worry,";
            String textString1 = "the game isn't broken ";

            if (currentMillis % 1500 <= 750) {
                textString0 = "§7§n"+textString0;
                textString1 = "§7§n"+textString1;
            }
            else {
                textString0 = "§7"+textString0;
                textString1 = "§7"+textString1;
            }

            if (currentMillis % 500 <= 250) textString1 += "/o/";
            else textString1 += "\\o\\";

            Text text0 = Text.literal(textString0);
            Text text1 = Text.literal(textString1);


            int screenWidth = client.getWindow().getScaledWidth();
            int x = screenWidth - 5;
            renderTextLeft(context, text1, x, y);
            renderTextLeft(context, text0, x - ((client.textRenderer.getWidth(text1)-client.textRenderer.getWidth(text0))/2),  y - (client.textRenderer.fontHeight+1));

            return -client.textRenderer.fontHeight*2-15;
        }
    }

    public static int renderTriviaTimer(MinecraftClient client, DrawContext context, int y) {
        if (!Trivia.isDoingTrivia()) return 0;
        if (client.currentScreen != null) return 0;

        int secondsLeft = (int) Trivia.getRemainingTime();

        Text actualTimer = Text.of(OtherUtils.formatTimeMillis(secondsLeft*1000L));
        Text timerText = Text.of("§7Trivia timer: ");

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;

        if (secondsLeft <= 5) renderTextLeft(context, actualTimer, x, y, 0xFFbf2222);
        else if (secondsLeft <= 30) renderTextLeft(context, actualTimer, x, y, 0xFFd6961a);
        else renderTextLeft(context, actualTimer, x, y, 0xFFffffff);

        renderTextLeft(context, timerText, x - client.textRenderer.getWidth(actualTimer), y);

        return -client.textRenderer.fontHeight-7;
    }

    private static long lastPressed = 0;
    public static int renderSuperpowerCooldown(MinecraftClient client, DrawContext context, int y) {
        if (ClientKeybinds.superpower != null && ClientKeybinds.superpower.isPressed()) lastPressed = System.currentTimeMillis();

        if (MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP == 0) return 0;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP) return 0;
        long millisLeft = MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP - currentMillis;
        if (millisLeft > 10000000) return 0;

        long pressedAgo = System.currentTimeMillis() - lastPressed;
        boolean keyPressed = pressedAgo < 500;
        if (pressedAgo > 6000) return 0;

        Text timerText = Text.of((keyPressed?"§c§n":"§7")+"Superpower cooldown:§f "+OtherUtils.formatTimeMillis(millisLeft));

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;
        renderTextLeft(context, timerText, x, y);

        return -client.textRenderer.fontHeight-7;
    }

    public static int renderMimicryTimer(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.MIMICRY_COOLDOWN_TIMESTAMP == 0) return 0;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= MainClient.MIMICRY_COOLDOWN_TIMESTAMP) return 0;
        long millisLeft = MainClient.MIMICRY_COOLDOWN_TIMESTAMP - currentMillis;
        if (millisLeft > 10000000) return 0;

        Text timerText = Text.of("§7Mimic power cooldown: §f"+OtherUtils.formatTimeMillis(millisLeft));

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;
        renderTextLeft(context, timerText, x, y);

        return -client.textRenderer.fontHeight-7;
    }

    public static void renderTextLeft(DrawContext context, Text text, int x, int y) {
        renderTextLeft(context, text, x, y, 0x3c3c3c);
    }

    public static void renderTextLeft(DrawContext context, Text text, int x, int y, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        context.drawText(client.textRenderer, text, x - client.textRenderer.getWidth(text), y - client.textRenderer.fontHeight, color, false);
    }
}
