package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public abstract class TextFieldConfigEntry extends ConfigEntry {
    protected final TextFieldWidget textField;

    public TextFieldConfigEntry(String fieldName, Text displayName, int textFieldWidth, int textFieldHeight) {
        super(fieldName, displayName);
        textField = new TextFieldWidget(textRenderer, 0, 0, textFieldWidth, textFieldHeight, Text.empty());
        textField.setChangedListener(this::onTextChanged);
    }

    protected abstract void initializeTextField();

    protected abstract void onTextChanged(String text);

    protected void renderAdditionalContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryWidth = getEntryContentWidth(width);

        renderAdditionalContent(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);

        textField.setX(x + entryWidth - textField.getWidth() - 5);
        textField.setY(y + (height - textField.getHeight()) / 2);
        textField.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        return textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        return textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        return textField.charTyped(chr, modifiers);
    }

    @Override
    protected boolean canReset() {
        return !textField.getText().equals(getDefaultValueAsString());
    }

    @Override
    protected void resetToDefault() {
        textField.setText(getDefaultValueAsString());
        clearError();
    }

    protected abstract String getDefaultValueAsString();
}