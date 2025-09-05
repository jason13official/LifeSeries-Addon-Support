package com.cursee.ls_addon_support.utils.other;

import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.events.Events;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class OtherUtils {

  private static final Random rnd = new Random();
  private static final Pattern TIME_PATTERN = Pattern.compile(
      "(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");
  private static final long[] reloads = {System.currentTimeMillis(), System.currentTimeMillis(),
      System.currentTimeMillis()};

  public static void log(Text message) {
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      player.sendMessage(message, false);
    }
    LSAddonSupport.LOGGER.info(message.getString());
  }

  public static void log(String string) {
    Text message = Text.of(string);
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      player.sendMessage(message, false);
    }
    LSAddonSupport.LOGGER.info(string);
  }

  public static void logConsole(String string) {
    LSAddonSupport.LOGGER.info(string);
  }

  public static void logIfClient(String string) {
    if (LSAddonSupport.isClient()) {
      LSAddonSupport.LOGGER.info(string);
    }
  }

  public static void debugString(String str) {
    LSAddonSupport.LOGGER.info(TextUtils.formatString("String length: {}", str.length()));

    // Print each character as its code point
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      LSAddonSupport.LOGGER.info("Character at %d: '%c' (Unicode: U+%04X)%n", i, c, (int) c);
    }
  }

  public static String formatTime(int totalTicks) {
    int hours = totalTicks / 72000;
    int minutes = (totalTicks % 72000) / 1200;
    int seconds = (totalTicks % 1200) / 20;
    return TextUtils.formatString("{}:{}:{}", hours, formatTimeNumber(minutes),
        formatTimeNumber(seconds));
  }

  public static String formatTimeMillis(long millis) {
    long totalSeconds = (long) Math.ceil(millis / 1000.0);
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = (totalSeconds % 60);
    if (hours == 0) {
      return TextUtils.formatString("{}:{}", formatTimeNumber(minutes), formatTimeNumber(seconds));
    }

    return TextUtils.formatString("{}:{}:{}", hours, formatTimeNumber(minutes),
        formatTimeNumber(seconds));
  }

  public static String formatTimeNumber(int time) {
    String value = String.valueOf(time);
      while (value.length() < 2) {
          value = "0" + value;
      }
    return value;
  }

  public static String formatTimeNumber(long time) {
    String value = String.valueOf(time);
      while (value.length() < 2) {
          value = "0" + value;
      }
    return value;
  }

  public static String formatSecondsToReadable(int seconds) {
    boolean isNegative = seconds < 0;
    seconds = Math.abs(seconds);

    int hours = seconds / 3600;
    int remainingSeconds = seconds % 3600;
    int minutes = remainingSeconds / 60;
    int secs = remainingSeconds % 60;

    if (hours > 0 && minutes == 0 && secs == 0) {
      return (isNegative ? "-" : "+") + hours + (hours == 1 ? " hour" : " hours");
    } else if (hours == 0 && minutes > 0 && secs == 0) {
      return (isNegative ? "-" : "+") + minutes + (minutes == 1 ? " minute" : " minutes");
    } else if (hours == 0 && minutes == 0 && secs > 0) {
      return (isNegative ? "-" : "+") + secs + (secs == 1 ? " second" : " seconds");
    } else {
      return String.format("%s%d:%02d:%02d", isNegative ? "-" : "+", hours, minutes, secs);
    }
  }

  public static int minutesToTicks(int mins) {
    return mins * 60 * 20;
  }

  public static int minutesToTicks(double mins) {
    double ticks = mins * 60 * 20;
    return (int) ticks;
  }

  public static int secondsToTicks(int secs) {
    return secs * 20;
  }

  public static Integer parseTimeFromArgument(String time) {
    time = time.replaceAll(" ", "").replaceAll("\"", "");
    Matcher matcher = TIME_PATTERN.matcher(time);
    if (!matcher.matches()) {
      return null; // Invalid time format
    }

    int hours = parseInt(matcher.group(1));
    int minutes = parseInt(matcher.group(2));
    int seconds = parseInt(matcher.group(3));

    return (hours * 3600 + minutes * 60 + seconds) * 20;
  }

  public static Integer parseTimeSecondsFromArgument(String time) {
    time = time.replaceAll(" ", "").replaceAll("\"", "");
    Matcher matcher = TIME_PATTERN.matcher(time);
    if (!matcher.matches()) {
      return null; // Invalid time format
    }

    int hours = parseInt(matcher.group(1));
    int minutes = parseInt(matcher.group(2));
    int seconds = parseInt(matcher.group(3));

    return (hours * 3600 + minutes * 60 + seconds);
  }

  private static int parseInt(String value) {
    return value == null ? 0 : Integer.parseInt(value);
  }

  public static void executeCommand(String command) {
    try {
        if (server == null) {
            return;
        }
      CommandManager manager = server.getCommandManager();
      ServerCommandSource commandSource = server.getCommandSource().withSilent();
      manager.executeWithPrefix(commandSource, command);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error executing command: " + command, e);
    }
  }

  public static void throwError(String error) {
    PlayerUtils.broadcastMessageToAdmins(Text.of("§c" + error));
    LSAddonSupport.LOGGER.error(error);
  }

  public static SoundEvent getRandomSound(String name, int from, int to) {
    if (to > from) {
      int index = rnd.nextInt(from, to + 1);
      name += index;
    }
    return SoundEvent.of(Identifier.of("minecraft", name));
  }

  public static String getTimeAndDate() {
    LocalDateTime time = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return time.format(formatter);
  }

  public static void reloadServerNoUpdate() {
    Events.skipNextTickReload = true;
    reloadServer();
  }

  public static void reloadServer() {
    try {
      Arrays.sort(reloads);
      int inInterval = 0;
      for (int i = 0; i < 3; i++) {
        if (System.currentTimeMillis() - OtherUtils.reloads[i] < 5000) {
          inInterval++;
        }
      }

      if (inInterval >= 3) {
        LSAddonSupport.LOGGER.error("Detected and prevented possible reload loop!");
        return;
      }
      reloads[0] = System.currentTimeMillis();
      OtherUtils.executeCommand("reload");
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error reloading server", e);
    }
  }

  public static void sendCommandFeedback(ServerCommandSource source, Text text) {
      if (source == null || text == null) {
          return;
      }
    source.sendFeedback(() -> text, true);
  }

  public static void sendCommandFeedbackQuiet(ServerCommandSource source, Text text) {
      if (source == null || text == null) {
          return;
      }
    source.sendFeedback(() -> text, false);
  }
}
