package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public abstract class TextFieldConfigEntry extends ConfigEntry {
    protected final TextFieldWidget textField;
    private static final int DEFAULT_TEXT_FIELD_WIDTH = 100;
    private static final int DEFAULT_TEXT_FIELD_HEIGHT = 18;

    public TextFieldConfigEntry(String fieldName, Text displayName) {
        this(fieldName, displayName, DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT);
    }

    public TextFieldConfigEntry(String fieldName, Text displayName, int textFieldWidth) {
        this(fieldName, displayName, textFieldWidth, DEFAULT_TEXT_FIELD_HEIGHT);
    }

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

        textField.setX(getTextFieldPosX(x, entryWidth));
        textField.setY(getTextFieldPosY(y, height));
        textField.render(context, mouseX, mouseY, tickDelta);
    }

    protected int getTextFieldPosX(int x, int entryWidth) {
        return x + entryWidth - textField.getWidth() - 5;
    }

    protected int getTextFieldPosY(int y, int height) {
        //return y + (height - textField.getHeight()) / 2; CENTER
        return y+1;
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