package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class BooleanConfigEntry extends ConfigEntry {
    private final ButtonWidget button;
    private final boolean defaultValue;
    private boolean value;

    public BooleanConfigEntry(String fieldName, Text displayName, boolean defaultValue) {
        super(fieldName, displayName);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.button = ButtonWidget.builder(this.getButtonText(), this::onButtonClick)
                .dimensions(0, 0, 60, 20)
                .build();
    }

    private void onButtonClick(ButtonWidget button) {
        this.value = !this.value;
        this.button.setMessage(this.getButtonText());
        this.markChanged();
    }

    private Text getButtonText() {
        return this.value ? Text.of("True") : Text.of("False");
    }

    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // Render label
        context.drawTextWithShadow(textRenderer, this.displayName, x + 5, y + 6, 0xFFFFFF);

        // Position and render button
        this.button.setX(x + width - 65);
        this.button.setY(y + 2);
        this.button.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.button.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.value = (Boolean) value;
            this.button.setMessage(this.getButtonText());
        }
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }
}