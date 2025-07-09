package net.mat0u5.lifeseries.gui.config.entries.simple;

import net.mat0u5.lifeseries.gui.config.entries.EmptyConfigEntry;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.minecraft.text.Text;

public class TextConfigEntry extends EmptyConfigEntry implements IEntryGroupHeader {
    private final boolean clickable;
    public boolean clicked;

    public TextConfigEntry(String fieldName, Text displayName) {
        this(fieldName, displayName, true);
    }

    public TextConfigEntry(String fieldName, Text displayName, boolean clickable) {
        super(fieldName, displayName);
        this.clickable = clickable;
    }

    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (clickable && button == 0) {
            clicked = !clicked;
        }
        return clickable;
    }

    @Override
    public String getValueType() {
        return "text";
    }

    @Override
    public void expand() {
        clicked = true;
    }

    @Override
    public boolean shouldExpand() {
        return clicked;
    }
}