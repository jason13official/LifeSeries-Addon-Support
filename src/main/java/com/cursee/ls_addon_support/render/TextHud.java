package com.cursee.ls_addon_support.render;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.events.ClientKeybinds;
import com.cursee.ls_addon_support.features.Trivia;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.TextColors;
import com.cursee.ls_addon_support.utils.enums.SessionTimerStates;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextHud {

  private static long lastPressed = 0;

  public static void renderText(DrawContext context) {
    MinecraftClient client = MinecraftClient.getInstance();
      if (client.options.hudHidden) {
          return;
      }
    int yPos = client.getWindow().getScaledHeight() - 5 - client.textRenderer.fontHeight;

    if (LSAddonSupport.DEBUG) {
      yPos += _renderSnailDistance(client, context, yPos);
    }

    yPos += renderGameNotBroken(client, context, yPos);
    yPos += renderSessionTimer(client, context, yPos);
    yPos += renderLimitedLifeTimer(client, context, yPos);
    yPos += renderMimicryTimer(client, context, yPos);
    yPos += renderSuperpowerCooldown(client, context, yPos);
    yPos += renderTriviaTimer(client, context, yPos);
  }

  public static int _renderSnailDistance(MinecraftClient client, DrawContext context, int y) {
    try {
        if (LSAddonSupportClient.snailPos == null) {
            return 0;
        }
        if (System.currentTimeMillis() - LSAddonSupportClient.snailPosTime > 2000) {
            return 0;
        }
        if (client.player == null) {
            return 0;
        }

      float distance = (float) client.player.getPos()
          .distanceTo(LSAddonSupportClient.snailPos.toCenterPos());

      Text timerText = Text.literal(String.valueOf(Math.round(distance)));
        if (distance < 20) {
            timerText = Text.literal("§c" + Math.round(distance));
        }

      int screenWidth = client.getWindow().getScaledWidth();
      int x = screenWidth - 5;

      RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

      return -client.textRenderer.fontHeight - 5;
    } catch (Exception e) {
    }
    return 0;
  }

  public static int renderGameNotBroken(MinecraftClient client, DrawContext context, int y) {
      if (client.player == null) {
          return 0;
      }
      if (!ClientRenderer.isGameFullyFrozen) {
          return 0;
      }
    long currentMillis = System.currentTimeMillis();
    int guiScale = client.options.getGuiScale().getValue();
    if (guiScale <= 3 && guiScale != 0) {

      String textString = "Don't worry, the game is not broken ";
        if (currentMillis % 1500 <= 750) {
            textString = "§7§n" + textString;
        } else {
            textString = "§7" + textString;
        }

        if (currentMillis % 500 <= 250) {
            textString += "/o/";
        } else {
            textString += "\\o\\";
        }

      Text text = Text.literal(textString);

      int screenWidth = client.getWindow().getScaledWidth();
      int x = screenWidth - 5;
      RenderUtils.drawTextRight(context, client.textRenderer, text, x, y);

      return -client.textRenderer.fontHeight - 10;
    } else {
      String textString0 = "Don't worry,";
      String textString1 = "the game isn't broken ";

      if (currentMillis % 1500 <= 750) {
        textString0 = "§7§n" + textString0;
        textString1 = "§7§n" + textString1;
      } else {
        textString0 = "§7" + textString0;
        textString1 = "§7" + textString1;
      }

        if (currentMillis % 500 <= 250) {
            textString1 += "/o/";
        } else {
            textString1 += "\\o\\";
        }

      Text text0 = Text.literal(textString0);
      Text text1 = Text.literal(textString1);

      int screenWidth = client.getWindow().getScaledWidth();
      int x = screenWidth - 5;
      RenderUtils.drawTextRight(context, client.textRenderer, text1, x, y);
      RenderUtils.drawTextRight(context, client.textRenderer, text0,
          x - ((client.textRenderer.getWidth(text1) - client.textRenderer.getWidth(text0)) / 2),
          y - (client.textRenderer.fontHeight + 1));

      return -client.textRenderer.fontHeight * 2 - 15;
    }
  }

  public static int renderSessionTimer(MinecraftClient client, DrawContext context, int y) {
      if (!LSAddonSupportClient.SESSION_TIMER) {
          return 0;
      }
      if (System.currentTimeMillis() - LSAddonSupportClient.sessionTimeLastUpdated > 15000) {
          return 0;
      }
      if (LSAddonSupportClient.sessionTime == SessionTimerStates.OFF.getValue()) {
          return 0;
      }

    MutableText timerText = Text.empty();
      if (LSAddonSupportClient.sessionTime == SessionTimerStates.ENDED.getValue()) {
          timerText = timerText.append(Text.of("§7Session has ended"));
      } else if (LSAddonSupportClient.sessionTime == SessionTimerStates.PAUSED.getValue()) {
          timerText = timerText.append(Text.of("§7Session has been paused"));
      } else if (LSAddonSupportClient.sessionTime == SessionTimerStates.NOT_STARTED.getValue()) {
          timerText = timerText.append(Text.of("§7Session has not started"));
      } else {
          long remainingTime =
              roundTime(LSAddonSupportClient.sessionTime) - System.currentTimeMillis();
          if (remainingTime < 0) {
              timerText = timerText.append(Text.of("§7Session has ended"));
          } else {
              timerText = timerText.append(
                  TextUtils.formatLoosely("§7Session {}",
                      OtherUtils.formatTimeMillis(remainingTime)));
          }
      }

    int screenWidth = client.getWindow().getScaledWidth();
    int x = screenWidth - 5;

    RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

    return -client.textRenderer.fontHeight - 5;
  }

  public static int renderLimitedLifeTimer(MinecraftClient client, DrawContext context, int y) {
      if (LSAddonSupportClient.clientCurrentSeason != Seasons.LIMITED_LIFE) {
          return 0;
      }
      if (System.currentTimeMillis() - LSAddonSupportClient.limitedLifeTimeLastUpdated > 15000) {
          return 0;
      }

    MutableText timerText = Text.empty();
      if (LSAddonSupportClient.limitedLifeLives == -1) {
          timerText = timerText.append(
              TextUtils.formatLoosely("{}0:00:00", LSAddonSupportClient.limitedLifeTimerColor));
      } else {
          long remainingTime = roundTime(LSAddonSupportClient.limitedLifeLives * 1000);

          if (remainingTime < 0) {
              timerText = timerText.append(
                  TextUtils.formatLoosely("{}0:00:00", LSAddonSupportClient.limitedLifeTimerColor));
          } else {
              timerText = timerText.append(Text.of(
                  LSAddonSupportClient.limitedLifeTimerColor + OtherUtils.formatTimeMillis(
                      remainingTime)));
          }
      }

    int screenWidth = client.getWindow().getScaledWidth();
    int x = screenWidth - 5;

    RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

    return -client.textRenderer.fontHeight - 5;
  }

  public static int renderTriviaTimer(MinecraftClient client, DrawContext context, int y) {
      if (!Trivia.isDoingTrivia()) {
          return 0;
      }
      if (client.currentScreen != null) {
          return 0;
      }

    long millisLeft = roundTime(Trivia.getEndTimestamp()) - System.currentTimeMillis();

    Text actualTimer = Text.of(OtherUtils.formatTimeMillis(millisLeft));
    Text timerText = Text.of("§7Trivia timer: ");

    int screenWidth = client.getWindow().getScaledWidth();
    int x = screenWidth - 5;

      if (millisLeft <= 5_000) {
          RenderUtils.drawTextRight(context, client.textRenderer, TextColors.RED, actualTimer, x,
              y);
      } else if (millisLeft <= 30_000) {
          RenderUtils.drawTextRight(context, client.textRenderer, TextColors.ORANGE, actualTimer, x,
              y);
      } else {
          RenderUtils.drawTextRight(context, client.textRenderer, TextColors.WHITE, actualTimer, x,
              y);
      }

    RenderUtils.drawTextRight(context, client.textRenderer, timerText,
        x - client.textRenderer.getWidth(actualTimer), y);

    return -client.textRenderer.fontHeight - 5;
  }

  public static int renderSuperpowerCooldown(MinecraftClient client, DrawContext context, int y) {
      if (ClientKeybinds.superpower != null && ClientKeybinds.superpower.isPressed()) {
          lastPressed = System.currentTimeMillis();
      }

      if (LSAddonSupportClient.SUPERPOWER_COOLDOWN_TIMESTAMP == 0) {
          return 0;
      }
    long currentMillis = System.currentTimeMillis();
      if (currentMillis >= LSAddonSupportClient.SUPERPOWER_COOLDOWN_TIMESTAMP) {
          return 0;
      }
    long millisLeft = roundTime(LSAddonSupportClient.SUPERPOWER_COOLDOWN_TIMESTAMP) - currentMillis;
      if (millisLeft > 10000000) {
          return 0;
      }

    long pressedAgo = System.currentTimeMillis() - lastPressed;
    boolean keyPressed = pressedAgo < 500;
      if (pressedAgo > 6000) {
          return 0;
      }

    Text timerText = TextUtils.formatLoosely("{}Superpower cooldown:§f {}",
        (keyPressed ? "§c§n" : "§7"), OtherUtils.formatTimeMillis(millisLeft));

    int screenWidth = client.getWindow().getScaledWidth();
    int x = screenWidth - 5;
    RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

    return -client.textRenderer.fontHeight - 5;
  }

  public static int renderMimicryTimer(MinecraftClient client, DrawContext context, int y) {
      if (LSAddonSupportClient.MIMICRY_COOLDOWN_TIMESTAMP == 0) {
          return 0;
      }
    long currentMillis = System.currentTimeMillis();
      if (currentMillis >= LSAddonSupportClient.MIMICRY_COOLDOWN_TIMESTAMP) {
          return 0;
      }
    long millisLeft = roundTime(LSAddonSupportClient.MIMICRY_COOLDOWN_TIMESTAMP) - currentMillis;
      if (millisLeft > 10000000) {
          return 0;
      }

    Text timerText = TextUtils.formatLoosely("§7Mimic power cooldown: §f{}",
        OtherUtils.formatTimeMillis(millisLeft));

    int screenWidth = client.getWindow().getScaledWidth();
    int x = screenWidth - 5;
    RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

    return -client.textRenderer.fontHeight - 5;
  }

  public static long roundTime(long time) {
    return time - (time % 1000);
  }
}
