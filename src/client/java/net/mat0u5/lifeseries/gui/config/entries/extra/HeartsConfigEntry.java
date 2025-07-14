package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.interfaces.ITextFieldAddonPopup;
import net.mat0u5.lifeseries.gui.config.entries.main.IntegerConfigEntry;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class HeartsConfigEntry extends IntegerConfigEntry implements ITextFieldAddonPopup {
    private static final String HEART_SYMBOL = "♥";
    private static final String HEART_ROW = "♥♥♥♥♥♥♥♥♥♥";
    private static final String HALF_HEART_SYMBOL = "♡";

    public HeartsConfigEntry(String fieldName, String displayName, String description, int value, int defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);
    }

    public HeartsConfigEntry(String fieldName, String displayName, String description, int value, int defaultValue, Integer minValue, Integer maxValue) {
        super(fieldName, displayName, description, value, defaultValue, minValue, maxValue);
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        renderPopup(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public Text getPopupText() {
        return Text.of("");
    }

    public List<MutableText> getHeartPopupText() {
        if (value == null) return List.of();

        int hearts = value / 2;
        boolean hasHalfHeart = (value % 2) == 1;


        if (hearts == 0 && !hasHalfHeart) {
            return List.of(Text.literal("No hearts").formatted(Formatting.GRAY));
        }

        List<MutableText> heartsList = new ArrayList<>();

        StringBuilder topRow = new StringBuilder();
        topRow.repeat(HEART_SYMBOL, (hearts % 10));
        if (hasHalfHeart) {
            topRow.append(HALF_HEART_SYMBOL);
        }
        if (!topRow.isEmpty()) {
            heartsList.add(Text.literal(topRow.toString()).formatted(Formatting.RED));
        }

        if (hearts >= 500) hearts = 500;
        while (hearts >= 10) {
            hearts -= 10;
            heartsList.add(Text.literal(HEART_ROW).formatted(Formatting.RED));
        }

        heartsList.set(heartsList.size()-1, heartsList.getLast().append(Text.literal(String.format(" (%d HP)", value)).formatted(Formatting.GRAY)));

        return heartsList;
    }

    @Override
    public boolean shouldShowPopup() {
        return (isFocused() || (isHovered && screen.getFocusedEntry() == this)) && textField != null;
    }

    @Override
    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public TextFieldWidget getTextField() {
        return textField;
    }

    @Override
    public int getPopupWidth() {
        int maxWidth = 0;
        for (MutableText text : getHeartPopupText()) {
            int width = getTextRenderer().getWidth(text);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth+2;
    }

    @Override
    public int getPopupHeight() {
        return (getTextRenderer().fontHeight-1) * getHeartPopupText().size()+2;
    }

    @Override
    public void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta) {
        TextRenderer textRenderer = getTextRenderer();
        int currentX = x+1;
        int currentY = y+1;
        for (MutableText text : getHeartPopupText()) {
            context.drawText(textRenderer, text, currentX, currentY, TextColors.WHITE, false);
            currentY += getTextRenderer().fontHeight-1;
        }
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.HEARTS;
    }
}