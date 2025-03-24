package net.mat0u5.lifeseries.client.gui.series;

import net.mat0u5.lifeseries.client.gui.DefaultScreen;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.doublelife.DoubleLife;
import net.mat0u5.lifeseries.series.lastlife.LastLife;
import net.mat0u5.lifeseries.series.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.series.secretlife.SecretLife;
import net.mat0u5.lifeseries.series.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SeriesInfoScreen extends DefaultScreen {

    private static final Identifier TEXTURE_THIRDLIFE = Identifier.of("lifeseries","textures/gui/thirdlife.png");
    private static final Identifier TEXTURE_LASTLIFE = Identifier.of("lifeseries","textures/gui/lastlife.png");
    private static final Identifier TEXTURE_DOUBLELIFE = Identifier.of("lifeseries","textures/gui/doublelife.png");
    private static final Identifier TEXTURE_LIMITEDLIFE = Identifier.of("lifeseries","textures/gui/limitedlife.png");
    private static final Identifier TEXTURE_SECRETLIFE = Identifier.of("lifeseries","textures/gui/secretlife.png");
    private static final Identifier TEXTURE_WILDLIFE = Identifier.of("lifeseries","textures/gui/wildlife.png");

    public static SeriesList series;

    public SeriesInfoScreen(SeriesList series) {
        super(Text.literal("Series Info Screen"), 1.3f, 1.3f);
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
    public void render(DrawContext context, int mouseX, int mouseY) {
        // Background + images
        //? if <= 1.21 {
        Identifier logo = getSeriesLogo();
        if (logo != null) {
            RenderUtils.drawTextureScaled(context, logo, startX+10, endY - 64 - 5, 0, 0, 256, 256, 0.25f, 0.25f);
            RenderUtils.drawTextureScaled(context, logo, endX - 64 - 10, endY - 64 - 5, 0, 0, 256, 256, 0.25f, 0.25f);
        }
        //?} else {
        /*Identifier logo = getSeriesLogo();
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

        int currentY = startY + 40;
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
                currentY += textRenderer.fontHeight + 8;
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
                currentY += textRenderer.fontHeight + 10;
            }
            else {
                RenderUtils.drawTextLeft(context, this.textRenderer, commandsText, startX + 20, currentY);
                currentY += textRenderer.fontHeight + 5;
                RenderUtils.drawTextLeft(context, this.textRenderer, Text.literal("  ").append(commandsTextActual), startX + 20, currentY);
                currentY += textRenderer.fontHeight + 10;
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
        currentY += textRenderer.fontHeight + 15;

        Text configText = Text.of("§0§nRun §8§n'/lifeseries config'§0§n to open the Life Series configuration!");
        RenderUtils.drawTextLeft(context, this.textRenderer, configText, startX + 20, currentY);
        currentY += textRenderer.fontHeight + 5;
    }
}