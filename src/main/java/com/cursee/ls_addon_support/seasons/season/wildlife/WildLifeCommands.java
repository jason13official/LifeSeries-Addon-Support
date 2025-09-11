package com.cursee.ls_addon_support.seasons.season.wildlife;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.Hunger;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkinsServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WildLifeCommands {

  public static boolean isAllowed() {
    return currentSeason.getSeason() == Seasons.WILD_LIFE;
  }

  public static boolean checkBanned(ServerCommandSource source) {
      if (isAllowed()) {
          return false;
      }
    source.sendError(Text.of("This command is only available when playing Wild Life."));
    return true;
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
      CommandRegistryAccess commandRegistryAccess,
      CommandManager.RegistrationEnvironment registrationEnvironment) {
    dispatcher.register(
        literal("wildcard")
            .requires(source -> isAllowed() && (isAdmin(source.getPlayer()) || (source.getEntity()
                == null)))
            .then(literal("list")
                .executes(context -> listWildcards(
                    context.getSource())
                )
            )
            .then(literal("listActive")
                .executes(context -> listActiveWildcards(
                    context.getSource())
                )
            )
            .then(literal("activate")
                .then(argument("wildcard", StringArgumentType.greedyString())
                    .suggests((context, builder) -> CommandSource.suggestMatching(
                        suggestionsActivateWildcard(), builder))
                    .executes(context -> activateWildcard(
                        context.getSource(), StringArgumentType.getString(context, "wildcard"))
                    )
                )
            )
            .then(literal("deactivate")
                .then(argument("wildcard", StringArgumentType.greedyString())
                    .suggests((context, builder) -> CommandSource.suggestMatching(
                        suggestionsDeactivateWildcard(), builder))
                    .executes(context -> deactivateWildcard(
                        context.getSource(), StringArgumentType.getString(context, "wildcard"))
                    )
                )
            )
            .then(literal("choose")
                .requires(
                    source -> (NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer()) || (
                        source.getEntity() == null)))
                .executes(context -> chooseWildcard(
                    context.getSource())
                )
            )
    );
    dispatcher.register(
        literal("snail")
            .requires(source -> isAllowed())
            .then(literal("names")
                .then(literal("set")
                    .requires(
                        source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("name", StringArgumentType.greedyString())
                            .executes(context -> setSnailName(context.getSource(),
                                EntityArgumentType.getPlayer(context, "player"),
                                StringArgumentType.getString(context, "name")))
                        )
                    )
                )
                .then(literal("reset")
                    .requires(
                        source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.players())
                        .executes(context -> resetSnailName(context.getSource(),
                            EntityArgumentType.getPlayers(context, "player")))
                    )
                )
                .then(literal("get")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> getSnailName(context.getSource(),
                            EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("request")
                    .then(argument("name", StringArgumentType.greedyString())
                        .executes(context -> requestSnailName(context.getSource(),
                            StringArgumentType.getString(context, "name")))
                    )
                )
            )
            .then(literal("textures")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .executes(context -> getSnailTexturesInfo(context.getSource()))
                .then(literal("list")
                    .executes(context -> getSnailTextures(context.getSource()))
                )
                .then(literal("info")
                    .executes(context -> getSnailTexturesInfo(context.getSource()))
                )
            )
    );
    dispatcher.register(
        literal("superpower")
            .requires(source -> isAllowed() && (isAdmin(source.getPlayer()) || (source.getEntity()
                == null)))
            .then(literal("set")
                .then(argument("player", EntityArgumentType.players())
                    .then(argument("superpower", StringArgumentType.string())
                        .suggests((context, builder) -> CommandSource.suggestMatching(
                            Superpowers.getImplementedStr(), builder))
                        .executes(context -> setSuperpower(context.getSource(),
                            EntityArgumentType.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "superpower")))
                    )
                )
            )
            .then(literal("reset")
                .then(argument("player", EntityArgumentType.players())
                    .executes(context -> resetSuperpowers(context.getSource(),
                        EntityArgumentType.getPlayers(context, "player")))
                )
            )
            .then(literal("setRandom")
                .executes(context -> setRandomSuperpowers(context.getSource()))
            )
            .then(literal("get")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> getSuperpower(context.getSource(),
                        EntityArgumentType.getPlayer(context, "player")))
                )
            )
            .then(literal("skipCooldown")
                .executes(context -> skipSuperpowerCooldown(context.getSource()))
            )
            .then(literal("assignForRandomization")
                .then(argument("player", EntityArgumentType.players())
                    .then(argument("superpower", StringArgumentType.string())
                        .suggests((context, builder) -> CommandSource.suggestMatching(
                            Superpowers.getImplementedStr(), builder))
                        .executes(context -> assignSuperpower(context.getSource(),
                            EntityArgumentType.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "superpower")))
                    )
                )
            )
    );
    dispatcher.register(
        literal("hunger")
            .requires(source -> isAllowed() && (isAdmin(source.getPlayer()) || (source.getEntity()
                == null)))
            .then(literal("randomizeFood")
                .executes(context -> randomizeFood(context.getSource()))
            )
    );
  }

  public static int randomizeFood(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }

    if (!WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
      source.sendError(Text.of("The Hunger wildcard is not active right now."));
      return -1;
    }

    OtherUtils.sendCommandFeedback(source, Text.of("§7Randomizing food..."));

    if (WildcardManager.activeWildcards.get(Wildcards.HUNGER) instanceof Hunger hungerWildcard) {
      hungerWildcard.newFoodRules();
    }

    return 1;
  }

  public static int requestSnailName(ServerCommandSource source, String name) {
      if (checkBanned(source)) {
          return -1;
      }
    ServerPlayerEntity player = source.getPlayer();
      if (player == null) {
          return -1;
      }

    PlayerUtils.broadcastMessageToAdmins(
        TextUtils.format("{}§7 requests their snail name to be §f{}§7", player, name));
    Text adminText = Text.literal("§7Click ").append(
        Text.literal("here")
            .styled(style -> style
                .withColor(Formatting.BLUE)
                .withClickEvent(TextUtils.runCommandClickEvent(
                    TextUtils.formatString("/snail names set {} {}", player, name)))
                .withUnderline(true)
            )).append(Text.of("§7 to accept."));
    PlayerUtils.broadcastMessageToAdmins(adminText);
    return 1;
  }

  public static int getSnailTexturesInfo(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    ServerPlayerEntity player = source.getPlayer();
      if (player == null) {
          return -1;
      }

    NetworkHandlerServer.sendStringPacket(player, PacketNames.SNAIL_TEXTURES_INFO, "");

    return 1;
  }

  public static int getSnailTextures(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    List<String> textures = SnailSkinsServer.getAllSkins();
    if (textures.isEmpty()) {
      OtherUtils.sendCommandFeedbackQuiet(source, Text.of(
          "§7No snail skins have been added yet. Run '§f/snail textures info§7' to learn how to add them."));
      return -1;
    }
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("§7The following skins have been found: §f{}", textures));
    return 1;
  }

  public static int chooseWildcard(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
      if (source.getPlayer() == null) {
          return -1;
      }
    if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
      source.sendError(Text.of(
          "You must have the Life Series mod installed §nclient-side§r to open the wildcard GUI"));
      return -1;
    }

    OtherUtils.sendCommandFeedback(source, Text.of("§7Opening the Wildcard selection GUI..."));
    NetworkHandlerServer.sendStringPacket(source.getPlayer(), PacketNames.SELECT_WILDCARDS, "true");
    return 1;
  }

  public static List<String> suggestionsDeactivateWildcard() {
    List<String> allWildcards = Wildcards.getActiveWildcardsStr();
    allWildcards.add("*");
    return allWildcards;
  }

  public static List<String> suggestionsActivateWildcard() {
    List<String> allWildcards = Wildcards.getInactiveWildcardsStr();
    allWildcards.add("*");
    return allWildcards;
  }

  public static int assignSuperpower(ServerCommandSource source,
      Collection<ServerPlayerEntity> targets, String name) {
      if (checkBanned(source)) {
          return -1;
      }
      if (targets == null || targets.isEmpty()) {
          return -1;
      }
    if (!Superpowers.getImplementedStr().contains(name)) {
      source.sendError(Text.of("That superpower doesn't exist"));
      return -1;
    }
    Superpowers superpower = Superpowers.fromString(name);
    if (superpower == Superpowers.NULL) {
      source.sendError(Text.of("That superpower doesn't exist"));
      return -1;
    }

    for (ServerPlayerEntity player : targets) {
      SuperpowersWildcard.assignedSuperpowers.put(player.getUuid(), superpower);
    }

    if (targets.size() == 1) {
      OtherUtils.sendCommandFeedback(source, TextUtils.format(
          "Forced {}'s superpower to be {} when the next superpower randomization happens",
          targets.iterator().next(), name));
    } else {
      OtherUtils.sendCommandFeedback(source, TextUtils.format(
          "Forced the superpower of {} targets to be {} when the next superpower randomization happens",
          targets.size(), name));
    }
    return 1;
  }

  public static int skipSuperpowerCooldown(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    ServerPlayerEntity player = source.getPlayer();
      if (player == null) {
          return -1;
      }
    Superpower superpower = SuperpowersWildcard.getSuperpowerInstance(player);
    if (superpower == null) {
      source.sendError(Text.of("You do not have an active superpower"));
      return -1;
    }
    superpower.cooldown = 0;
    NetworkHandlerServer.sendLongPacket(player, PacketNames.SUPERPOWER_COOLDOWN, 0);

    OtherUtils.sendCommandFeedback(source, Text.of("Your superpower cooldown has been skipped"));
    return 1;
  }

  public static int setRandomSuperpowers(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    SuperpowersWildcard.rollRandomSuperpowers();
    OtherUtils.sendCommandFeedback(source, Text.of("Randomized everyone's superpowers"));
    return 1;
  }

  public static int resetSuperpowers(ServerCommandSource source,
      Collection<ServerPlayerEntity> targets) {
      if (checkBanned(source)) {
          return -1;
      }
      if (targets == null || targets.isEmpty()) {
          return -1;
      }

    for (ServerPlayerEntity player : targets) {
      SuperpowersWildcard.resetSuperpower(player);
    }

    if (targets.size() == 1) {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Deactivated {}'s superpower", targets.iterator().next()));
    } else {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Deactivated the superpower of {} targets", targets.size()));
    }
    return 1;
  }

  public static int getSuperpower(ServerCommandSource source, ServerPlayerEntity player) {
      if (checkBanned(source)) {
          return -1;
      }
    Superpowers superpower = SuperpowersWildcard.getSuperpower(player);
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("{}'s superpower is: {}", player, superpower.getString()));
    return 1;
  }

  public static int setSuperpower(ServerCommandSource source,
      Collection<ServerPlayerEntity> targets, String name) {
      if (checkBanned(source)) {
          return -1;
      }
      if (targets == null || targets.isEmpty()) {
          return -1;
      }

    if (!Superpowers.getImplementedStr().contains(name)) {
      source.sendError(Text.of("That superpower doesn't exist"));
      return -1;
    }

    Superpowers superpower = Superpowers.fromString(name);
    if (superpower == Superpowers.NULL) {
      source.sendError(Text.of("That superpower doesn't exist"));
      return -1;
    }

    for (ServerPlayerEntity player : targets) {
      SuperpowersWildcard.setSuperpower(player, superpower);
    }
    if (targets.size() == 1) {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Set {}'s superpower to {}", targets.iterator().next(), name));
    } else {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Set the superpower to {} for {} targets", name, targets.size()));
    }
    return 1;
  }

  public static int setSnailName(ServerCommandSource source, ServerPlayerEntity player,
      String name) {
      if (checkBanned(source)) {
          return -1;
      }
    Snails.setSnailName(player, name);
    OtherUtils.sendCommandFeedback(source,
        TextUtils.format("Set {}'s snail name to {}", player, name));
    return 1;
  }

  public static int resetSnailName(ServerCommandSource source,
      Collection<ServerPlayerEntity> targets) {
      if (checkBanned(source)) {
          return -1;
      }
      if (targets == null || targets.isEmpty()) {
          return -1;
      }

    for (ServerPlayerEntity player : targets) {
      Snails.resetSnailName(player);
    }

    if (targets.size() == 1) {
      ServerPlayerEntity player = targets.iterator().next();
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Reset {}'s snail name to {}'s Snail", player,
              player.getNameForScoreboard()));
    } else {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Reset the snail name for {} targets", targets.size()));
    }

    return 1;
  }

  public static int getSnailName(ServerCommandSource source, ServerPlayerEntity player) {
      if (checkBanned(source)) {
          return -1;
      }
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("{}'s snail is called {}", player, Snails.getSnailName(player)));
    return 1;
  }

  public static int deactivateWildcard(ServerCommandSource source, String wildcardName) {
      if (checkBanned(source)) {
          return -1;
      }
    if (wildcardName.equalsIgnoreCase("*")) {
      WildcardManager.onSessionEnd();
      OtherUtils.sendCommandFeedback(source, Text.of("Deactivated all wildcards"));
      return 1;
    }
    String wildcard = Wildcards.getFromString(wildcardName);
    if (wildcard.equals(Wildcards.NULL)) {
      source.sendError(Text.of("That Wildcard doesn't exist"));
      return -1;
    }
    if (!WildcardManager.isActiveWildcard(wildcard)) {
      source.sendError(Text.of("That Wildcard is not active"));
      return -1;
    }
    WildcardManager.fadedWildcard();
    Wildcard wildcardInstance = WildcardManager.activeWildcards.get(wildcard);
    wildcardInstance.deactivate();
    WildcardManager.activeWildcards.remove(wildcard);

    OtherUtils.sendCommandFeedback(source, TextUtils.format("Deactivated {}", wildcardName));
    NetworkHandlerServer.sendUpdatePackets();
    return 1;
  }

  public static int activateWildcard(ServerCommandSource source, String wildcardName) {
      if (checkBanned(source)) {
          return -1;
      }
    if (wildcardName.equalsIgnoreCase("*")) {
      List<String> inactiveWildcards = Wildcards.getInactiveWildcards();
      for (String wildcard : inactiveWildcards) {
          if (wildcard.equals(Wildcards.CALLBACK)) {
              continue;
          }
        Wildcard wildcardInstance = Wildcards.getInstance(wildcard);
          if (wildcardInstance == null) {
              continue;
          }
        WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
      }

      WildcardManager.showDots();
      TaskScheduler.scheduleTask(90, () -> {
        for (Wildcard wildcard : WildcardManager.activeWildcards.values()) {
            if (wildcard.active) {
                continue;
            }
          wildcard.activate();
        }
        WildcardManager.showRainbowCryptTitle("All wildcards are active!");
      });
      NetworkHandlerServer.sendUpdatePackets();

      OtherUtils.sendCommandFeedback(source, Text.of("Activated all wildcards (Except Callback)"));
      return 1;
    }
    String wildcard = Wildcards.getFromString(wildcardName);
    if (wildcard.equals(Wildcards.NULL)) {
      source.sendError(Text.of("That Wildcard doesn't exist"));
      return -1;
    }
    if (WildcardManager.isActiveWildcard(wildcard)) {
      source.sendError(Text.of("That Wildcard is already active"));
      return -1;
    }
    Wildcard actualWildcard = Wildcards.getInstance(wildcard);
    if (actualWildcard == null) {
      source.sendError(Text.of("That Wildcard has not been implemented yet"));
      return -1;
    }
    TaskScheduler.scheduleTask(89,
        () -> WildcardManager.activeWildcards.put(wildcard, actualWildcard));
    WildcardManager.activateWildcards();

    OtherUtils.sendCommandFeedback(source, TextUtils.format("Activated {}", wildcardName));
    return 1;
  }

  public static int listWildcards(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Available Wildcards: {}", Wildcards.getWildcardsStr()));
    return 1;
  }

  public static int listActiveWildcards(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    if (Wildcards.getActiveWildcardsStr().isEmpty()) {
      OtherUtils.sendCommandFeedbackQuiet(source, Text.of(
          "§7There are no active Wildcards right now. \nYou will be able to select a Wildcard when you start a session, or you can use '§f/wildcard activate <wildcard>§7' to activate a specific Wildcard right now."));
      return 1;
    }
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Activated Wildcards: {}", Wildcards.getActiveWildcardsStr()));
    return 1;
  }
}
