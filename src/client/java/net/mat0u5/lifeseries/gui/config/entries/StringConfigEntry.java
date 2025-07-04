package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class StringConfigEntry extends ConfigEntry {
    private final TextFieldWidget textField;
    private final String defaultValue;

    public StringConfigEntry(String fieldName, Text displayName, String defaultValue) {
        super(fieldName, displayName);
        this.defaultValue = defaultValue;
        this.textField = new TextFieldWidget(textRenderer, 0, 0, 150, 20, Text.empty());
        this.textField.setText(defaultValue);
        this.textField.setChangedListener(this::onTextChanged);
    }

    private void onTextChanged(String text) {
        this.clearError();
        this.markChanged();
    }

    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.drawTextWithShadow(textRenderer, this.displayName, x + 5, y + 6, 0xFFFFFF);

        this.textField.setX(x + width - 155);
        this.textField.setY(y + 2);
        this.textField.render(context, mouseX, mouseY, tickDelta);

        if (this.hasError()) {
            context.drawTextWithShadow(textRenderer, "!", x + width - 170, y + 6, 0xFF5555);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.textField.charTyped(chr, modifiers);
    }

    @Override
    public Object getValue() {
        return this.textField.getText();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String) {
            this.textField.setText((String) value);
        }
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }
}