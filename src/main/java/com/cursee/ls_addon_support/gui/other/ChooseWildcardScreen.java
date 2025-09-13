package com.cursee.ls_addon_support.gui.other;

import com.cursee.ls_addon_support.gui.DefaultScreen;
import com.cursee.ls_addon_support.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ChooseWildcardScreen extends DefaultScreen {
  private WildcardListWidget wildcardList;

  public ChooseWildcardScreen() {
    super(Text.literal("Choose Wildcard Screen"));
  }

  @Override
  protected void init() {
    super.init();
    this.wildcardList = new WildcardListWidget(this, this.client);
    this.addSelectableChild(this.wildcardList);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY) {
    this.wildcardList.render(context, mouseX, mouseY, 0.0f);
    
    String prompt = "Select the Wildcard for this session.";
    RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(prompt), centerX, startY + 20);
  }
}
