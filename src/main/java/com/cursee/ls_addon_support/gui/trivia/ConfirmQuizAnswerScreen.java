package com.cursee.ls_addon_support.gui.trivia;

import com.cursee.ls_addon_support.features.Trivia;
import com.cursee.ls_addon_support.gui.DefaultSmallScreen;
import com.cursee.ls_addon_support.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ConfirmQuizAnswerScreen extends DefaultSmallScreen {

  private final QuizScreen parent;
  private final int answerIndex;

  public ConfirmQuizAnswerScreen(QuizScreen parent, int answerIndex) {
    super(Text.literal("Confirm Answer"));
    this.parent = parent;
    this.answerIndex = answerIndex;
  }

  @Override
  public boolean allowCloseButton() {
    return false;
  }

  @Override
  protected void init() {
    super.init();

    this.addDrawableChild(
        ButtonWidget.builder(Text.literal("Confirm"), btn -> {
              this.close();
              Trivia.sendAnswer(answerIndex);
            })
            .position(startX + 8, endY - 28)
            .size(60, 20)
            .build()
    );

    this.addDrawableChild(
        ButtonWidget.builder(Text.literal("Cancel"), btn -> {
                if (this.client != null) {
                    this.client.setScreen(parent);
                }
            })
            .position(endX - 68, endY - 28)
            .size(60, 20)
            .build()
    );
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY) {
    RenderUtils.drawTextCenter(context, textRenderer, Text.of("Submit answer?"), centerX,
        startY + 20);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }
}