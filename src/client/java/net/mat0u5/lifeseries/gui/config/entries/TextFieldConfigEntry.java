package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public abstract class TextFieldConfigEntry extends ConfigEntry {
    protected final TextFieldWidget textField;
    private static final int DEFAULT_TEXT_FIELD_WIDTH = 100;
    private static final int DEFAULT_TEXT_FIELD_HEIGHT = 18;
    private int maxTextFieldLength = 32;

    public TextFieldConfigEntry(String fieldName, Text displayName) {
        this(fieldName, displayName, DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT);
    }

    public TextFieldConfigEntry(String fieldName, Text displayName, int textFieldWidth) {
        this(fieldName, displayName, textFieldWidth, DEFAULT_TEXT_FIELD_HEIGHT);
    }

    public TextFieldConfigEntry(String fieldName, Text displayName, int textFieldWidth, int textFieldHeight) {
        super(fieldName, displayName);
        textField = new TextFieldWidget(textRenderer, 0, 0, textFieldWidth, textFieldHeight, Text.empty());
        textField.setChangedListener(this::onChanged);
    }

    protected abstract void initializeTextField();

    private void onChanged(String text) {
        onTextChanged(text);
    }

    protected void onTextChanged(String text) {
        if (text.length() >= maxTextFieldLength) {
            while (text.length() >= maxTextFieldLength && maxTextFieldLength < 1_000_000_000) {
                maxTextFieldLength *= 2;
            }
            textField.setMaxLength(maxTextFieldLength);
        }
    }

    protected void renderAdditionalContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryWidth = getEntryContentWidth(width);

        renderAdditionalContent(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);

        textField.setX(getTextFieldPosX(x, entryWidth));
        textField.setY(getTextFieldPosY(y, height));
        textField.render(context, mouseX, mouseY, tickDelta);

        if (hasError()) {
            textField.setEditableColor(TextColors.GUI_RED);
        }
        else {
            textField.setEditableColor(TextColors.WHITE);
        }
    }

    protected int getTextFieldPosX(int x, int entryWidth) {
        return x + entryWidth - textField.getWidth() - 5;
    }

    protected int getTextFieldPosY(int y, int height) {
        //return y + (height - textField.getHeight()) / 2; CENTER
        return y+1;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        textField.setFocused(focused);
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        textField.mouseClicked(mouseX, mouseY, button);
        return true;
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
    protected void resetToDefault() {
        setText(getDefaultValueAsString());
        clearError();
    }

    public void setText(String text) {
        onTextChanged(text);
        textField.setText(text);
    }
}