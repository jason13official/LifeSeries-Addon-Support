package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public abstract class ButtonConfigEntry extends ConfigEntry {
    protected final ButtonWidget button;

    public ButtonConfigEntry(String fieldName, Text displayName, int buttonWidth, int buttonHeight) {
        super(fieldName, displayName);
        button = createButton(buttonWidth, buttonHeight);
    }

    protected abstract ButtonWidget createButton(int width, int height);

    protected abstract void onButtonClick(ButtonWidget button);

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryWidth = getEntryContentWidth(width);
        button.setX(x + entryWidth - button.getWidth() - 5);
        button.setY(y + (height - button.getHeight()) / 2);
        button.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        return this.button.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        return false;
    }
}