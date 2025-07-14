package net.mat0u5.lifeseries.gui.config.entries.extra;


import net.mat0u5.lifeseries.gui.config.entries.interfaces.ITextFieldAddonPopup;
import net.mat0u5.lifeseries.gui.config.entries.main.DoubleConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PercentageConfigEntry extends DoubleConfigEntry implements ITextFieldAddonPopup {

    public PercentageConfigEntry(String fieldName, String displayName, String description, double value, double defaultValue) {
        super(fieldName, displayName, description, value, defaultValue, 0.0, 1.0);
    }

    public PercentageConfigEntry(String fieldName, String displayName, String description, double value, double defaultValue, Double minValue, Double maxValue) {
        super(fieldName, displayName, description, value, defaultValue, minValue, maxValue);
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        renderPopup(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public TextFieldWidget getTextField() {
        return textField;
    }

    @Override
    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public Text getPopupText() {
        return Text.literal((value*100)+"%").formatted(Formatting.GRAY);
    }

    @Override
    public boolean shouldShowPopup() {
        return (isHovered || isFocused()) && value != null;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.PERCENTAGE;
    }
}