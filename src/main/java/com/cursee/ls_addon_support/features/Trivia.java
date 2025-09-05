package com.cursee.ls_addon_support.features;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.gui.trivia.ConfirmQuizAnswerScreen;
import com.cursee.ls_addon_support.gui.trivia.QuizScreen;
import com.cursee.ls_addon_support.network.NetworkHandlerClient;
import com.cursee.ls_addon_support.network.packets.TriviaQuestionPayload;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.versions.VersionControl;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public class Trivia {

  public static String question = "";
  public static List<String> answers = new ArrayList<>();
  public static int difficulty = 0;
  public static int secondsToComplete = 0;
  public static long timestamp = 0;

  public static void receiveTrivia(TriviaQuestionPayload payload) {
    question = payload.question();
    answers = payload.answers();
    difficulty = payload.difficulty();
    timestamp = payload.timestamp();
    secondsToComplete = payload.timeToComplete();
      if (VersionControl.isDevVersion()) {
          LSAddonSupport.LOGGER.info(
              TextUtils.formatString("[PACKET_CLIENT] Received trivia question: {{}, {}, {}}",
                  question,
                  difficulty, answers));
      }
    openGui();
  }

  public static long getRemainingTime() {
    long timeSinceStart = (int) Math.ceil((System.currentTimeMillis() - timestamp) / 1000.0);
    return secondsToComplete - timeSinceStart;
  }

  public static long getEndTimestamp() {
    return (timestamp + secondsToComplete * 1000L);
  }

  public static boolean isDoingTrivia() {
      if (Trivia.secondsToComplete == 0) {
          return false;
      }
    long remaining = Trivia.getRemainingTime();
      if (remaining <= 0) {
          return false;
      }
    return remaining <= 1000000;
  }

  public static void openGui() {
      if (question.isEmpty() || answers.isEmpty()) {
          return;
      }
    MinecraftClient.getInstance().setScreen(new QuizScreen());
  }

  public static void closeGui() {
      if (MinecraftClient.getInstance().currentScreen == null) {
          return;
      }
    if (MinecraftClient.getInstance().currentScreen instanceof QuizScreen
        || MinecraftClient.getInstance().currentScreen instanceof ConfirmQuizAnswerScreen) {
      MinecraftClient.getInstance().currentScreen.close();
    }
  }

  public static void resetTrivia() {
    question = "";
    answers = new ArrayList<>();
    difficulty = 0;
    secondsToComplete = 0;
    timestamp = 0;
    closeGui();
  }

  public static void sendAnswer(int answer) {
    resetTrivia();
    NetworkHandlerClient.sendTriviaAnswer(answer);
  }
}
