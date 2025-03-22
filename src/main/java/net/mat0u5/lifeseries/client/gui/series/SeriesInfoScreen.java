package net.mat0u5.lifeseries.client.gui.series;

import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.doublelife.DoubleLife;
import net.mat0u5.lifeseries.series.lastlife.LastLife;
import net.mat0u5.lifeseries.series.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.series.secretlife.SecretLife;
import net.mat0u5.lifeseries.series.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SeriesInfoScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE_LEFT = Identifier.of("lifeseries","textures/gui/trivia_question1.png");
    private static final Identifier BACKGROUND_TEXTURE_RIGHT = Identifier.of("lifeseries","textures/gui/trivia_question2.png");

    private static final Identifier TEXTURE_SELECTED = Identifier.of("lifeseries","textures/gui/selected.png");

    private static final Identifier TEXTURE_THIRDLIFE = Identifier.of("lifeseries","textures/gui/thirdlife.png");
    private static final Identifier TEXTURE_LASTLIFE = Identifier.of("lifeseries","textures/gui/lastlife.png");
    private static final Identifier TEXTURE_DOUBLELIFE = Identifier.of("lifeseries","textures/gui/doublelife.png");
    private static final Identifier TEXTURE_LIMITEDLIFE = Identifier.of("lifeseries","textures/gui/limitedlife.png");
    private static final Identifier TEXTURE_SECRETLIFE = Identifier.of("lifeseries","textures/gui/secretlife.png");
    private static final Identifier TEXTURE_WILDLIFE = Identifier.of("lifeseries","textures/gui/wildlife.png");



    private static final int BG_WIDTH = (int)(320 * 1.3f);
    private static final int BG_HEIGHT = (int)(180 * 1.3f);

    public static SeriesList series;

    public SeriesInfoScreen(SeriesList series) {
        super(Text.literal("Series Info Screen"));
        this.series = series;
    }

    public Identifier getSeriesLogo() {
        if (series == SeriesList.THIRD_LIFE) return TEXTURE_THIRDLIFE;
        if (series == SeriesList.LAST_LIFE) return TEXTURE_LASTLIFE;
        if (series == SeriesList.DOUBLE_LIFE) return TEXTURE_DOUBLELIFE;
        if (series == SeriesList.LIMITED_LIFE) return TEXTURE_LIMITEDLIFE;
        if (series == SeriesList.SECRET_LIFE) return TEXTURE_SECRETLIFE;
        if (series == SeriesList.WILD_LIFE) return TEXTURE_WILDLIFE;
        return null;
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
        int endY = startY + BG_HEIGHT;


        // Background + images
        //? if <= 1.21 {
        RenderUtils.drawTextureScaled(context, BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 1.3f, 1.3f);
        RenderUtils.drawTextureScaled(context, BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 1.3f, 1.3f);

        Identifier logo = getSeriesLogo();
        if (logo != null) {
            RenderUtils.drawTextureScaled(context, logo, startX+10, endY - 64 - 10, 0, 0, 256, 256, 0.25f, 0.25f);
            RenderUtils.drawTextureScaled(context, logo, endX - 64 - 10, endY - 64 - 10, 0, 0, 256, 256, 0.25f, 0.25f);
        }
        //?} else {
        /*RenderUtils.drawTextureScaled(context, BACKGROUND_TEXTURE_LEFT, startX, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256, 1.3f, 1.3f);
        RenderUtils.drawTextureScaled(context, BACKGROUND_TEXTURE_RIGHT, startX+BG_WIDTH/2, startY, 0, 0, BG_WIDTH/2, BG_HEIGHT, 256, 256, 1.3f, 1.3f);


        Identifier logo = getSeriesLogo();
        if (logo != null) {
            RenderUtils.drawTextureScaled(context, logo, startX+10, endY - 64 - 10, 0, 0, 256, 256, 256, 256, 0.25f, 0.25f);
            RenderUtils.drawTextureScaled(context, logo, endX - 64 - 10, endY - 64 - 10, 0, 0, 256, 256, 256, 256, 0.25f, 0.25f);
        }
        *///?}

        context.getMatrices().push();
        context.getMatrices().scale(1.5f, 1.5f, 1.0f);
        String seriesName = SeriesList.getFormattedStringNameFromSeries(series);
        RenderUtils.drawTextCenterScaled(context, this.textRenderer, Text.of("§0"+seriesName), centerX, startY + 10, 1.5f, 1.5f);
        context.getMatrices().pop();

        int currentY = startY + 50;
        MutableText adminCommandsText = Text.literal("§8Available §nadmin§8 commands: ");
        MutableText adminCommandsTextActual = null;
        if (series == SeriesList.THIRD_LIFE) adminCommandsTextActual = Text.literal(ThirdLife.COMMANDS_ADMIN_TEXT);
        if (series == SeriesList.LAST_LIFE) adminCommandsTextActual = Text.literal(LastLife.COMMANDS_ADMIN_TEXT);
        if (series == SeriesList.DOUBLE_LIFE) adminCommandsTextActual = Text.literal(DoubleLife.COMMANDS_ADMIN_TEXT);
        if (series == SeriesList.LIMITED_LIFE) adminCommandsTextActual = Text.literal(LimitedLife.COMMANDS_ADMIN_TEXT);
        if (series == SeriesList.SECRET_LIFE) adminCommandsTextActual = Text.literal(SecretLife.COMMANDS_ADMIN_TEXT);
        if (series == SeriesList.WILD_LIFE) adminCommandsTextActual = Text.literal(WildLife.COMMANDS_ADMIN_TEXT);
        if (adminCommandsTextActual != null) {
            MutableText combined = adminCommandsText.copy().append(adminCommandsTextActual);
            if (textRenderer.getWidth(combined) < (endX - startX)) {
                RenderUtils.drawTextLeft(context, this.textRenderer, combined, startX + 20, currentY);
                currentY += textRenderer.fontHeight + 5;
            }
            else {
                RenderUtils.drawTextLeft(context, this.textRenderer, adminCommandsText, startX + 20, currentY);
                currentY += textRenderer.fontHeight + 5;
                RenderUtils.drawTextLeft(context, this.textRenderer, Text.literal("  ").append(adminCommandsTextActual), startX + 20, currentY);
                currentY += textRenderer.fontHeight + 10;
            }
        }

        MutableText commandsText = Text.literal("§8Available §nnon-admin§8 commands: ");
        MutableText commandsTextActual = null;
        if (series == SeriesList.THIRD_LIFE) commandsTextActual = Text.literal(ThirdLife.COMMANDS_TEXT);
        if (series == SeriesList.LAST_LIFE) commandsTextActual = Text.literal(LastLife.COMMANDS_TEXT);
        if (series == SeriesList.DOUBLE_LIFE) commandsTextActual = Text.literal(DoubleLife.COMMANDS_TEXT);
        if (series == SeriesList.LIMITED_LIFE) commandsTextActual = Text.literal(LimitedLife.COMMANDS_TEXT);
        if (series == SeriesList.SECRET_LIFE) commandsTextActual = Text.literal(SecretLife.COMMANDS_TEXT);
        if (series == SeriesList.WILD_LIFE) commandsTextActual = Text.literal(WildLife.COMMANDS_TEXT);
        if (commandsTextActual != null) {
            MutableText combined = commandsText.copy().append(commandsTextActual);
            if (textRenderer.getWidth(combined) < (endX - startX)) {
                RenderUtils.drawTextLeft(context, this.textRenderer, combined, startX + 20, currentY);
                currentY += textRenderer.fontHeight + 15;
            }
            else {
                RenderUtils.drawTextLeft(context, this.textRenderer, commandsText, startX + 20, currentY);
                currentY += textRenderer.fontHeight + 5;
                RenderUtils.drawTextLeft(context, this.textRenderer, Text.literal("  ").append(commandsTextActual), startX + 20, currentY);
                currentY += textRenderer.fontHeight + 5;
            }
        }


        context.getMatrices().push();
        context.getMatrices().scale(1.15f, 1.15f, 1.0f);
        Text howToStart = Text.of("§0§nHow to start a session");
        RenderUtils.drawTextLeftScaled(context, this.textRenderer, howToStart, startX + 20, currentY+3, 1.15f, 1.15f);
        currentY += textRenderer.fontHeight + 13;
        context.getMatrices().pop();

        Text sessionTimer = Text.of("§8Run §3'/session timer set <time>'§8 to set the desired session time.");
        RenderUtils.drawTextLeft(context, this.textRenderer, sessionTimer, startX + 20, currentY);
        currentY += textRenderer.fontHeight + 5;

        Text sessionStart = Text.of("§8After that, run §3'/session start'§8 to start the session.");
        RenderUtils.drawTextLeft(context, this.textRenderer, sessionStart, startX + 20, currentY);
        currentY += textRenderer.fontHeight + 5;

        super.render(context, mouseX, mouseY, delta);
    }
}