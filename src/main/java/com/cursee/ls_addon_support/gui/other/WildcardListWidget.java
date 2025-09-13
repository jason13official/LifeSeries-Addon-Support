package com.cursee.ls_addon_support.gui.other;

import com.google.common.collect.ImmutableList;
import com.cursee.ls_addon_support.network.NetworkHandlerClient;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class WildcardListWidget extends ElementListWidget<WildcardListWidget.Entry> {
    private static final int ENTRY_HEIGHT = 25;
    final ChooseWildcardScreen parent;

    public WildcardListWidget(ChooseWildcardScreen parent, MinecraftClient client) {
        super(client, parent.width, parent.height - 80, 40, ENTRY_HEIGHT);
        this.parent = parent;
        
        this.addEntry(new WildcardEntry("Size Shifting", "size_shifting"));
        this.addEntry(new WildcardEntry("Hunger", "hunger"));
        this.addEntry(new WildcardEntry("Snails", "snails"));
        this.addEntry(new WildcardEntry("Time Dilation", "time_dilation"));
        this.addEntry(new WildcardEntry("Trivia", "trivia"));
        this.addEntry(new WildcardEntry("Mob Swap", "mob_swap"));
        this.addEntry(new WildcardEntry("Superpowers", "superpowers"));
        this.addEntry(new WildcardEntry("Callback", "callback"));
    }

    public int getRowWidth() {
        return 300;
    }

    public abstract static class Entry extends ElementListWidget.Entry<Entry> {
    }

    public class WildcardEntry extends Entry {
        private final String displayName;
        private final String wildcardId;
        private final ButtonWidget selectButton;

        public WildcardEntry(String displayName, String wildcardId) {
            this.displayName = displayName;
            this.wildcardId = wildcardId;
            this.selectButton = ButtonWidget.builder(Text.literal(displayName), button -> {
                WildcardListWidget.this.parent.close();
                NetworkHandlerClient.sendStringPacket(PacketNames.SELECTED_WILDCARD, wildcardId);
            }).dimensions(0, 0, 200, 20).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int buttonX = x + (entryWidth - this.selectButton.getWidth()) / 2;
            this.selectButton.setPosition(buttonX, y + 2);
            this.selectButton.render(context, mouseX, mouseY, tickProgress);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.selectButton);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(this.selectButton);
        }
    }
}