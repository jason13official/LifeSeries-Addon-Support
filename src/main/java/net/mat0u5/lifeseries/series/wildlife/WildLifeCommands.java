package net.mat0u5.lifeseries.series.wildlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.series.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.SnailSkinsServer;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.utils.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WildLifeCommands {

    public static boolean isAllowed() {
        return currentSeries.getSeries() == SeriesList.WILD_LIFE;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when playing Wild Life."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("wildcard")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
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
                        .suggests((context, builder) -> CommandSource.suggestMatching(suggestionsActivateWildcard(), builder))
                        .executes(context -> activateWildcard(
                            context.getSource(), StringArgumentType.getString(context, "wildcard"))
                        )
                    )
                )
                .then(literal("deactivate")
                    .then(argument("wildcard", StringArgumentType.greedyString())
                        .suggests((context, builder) -> CommandSource.suggestMatching(suggestionsDeactivateWildcard(), builder))
                        .executes(context -> deactivateWildcard(
                            context.getSource(), StringArgumentType.getString(context, "wildcard"))
                        )
                    )
                )
                .then(literal("choose")
                    .executes(context -> chooseWildcard(
                        context.getSource())
                    )
                )
        );
        dispatcher.register(
            literal("snail")
                .then(literal("names")
                    .then(literal("set")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .then(argument("player", EntityArgumentType.player())
                            .then(argument("name", StringArgumentType.greedyString())
                                .executes(context -> setSnailName(context.getSource(), EntityArgumentType.getPlayer(context, "player"), StringArgumentType.getString(context, "name")))
                            )
                        )
                    )
                    .then(literal("reset")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .then(argument("player", EntityArgumentType.player())
                            .executes(context -> resetSnailName(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                        )
                    )
                    .then(literal("get")
                        .then(argument("player", EntityArgumentType.player())
                            .executes(context -> getSnailName(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                        )
                    )
                    .then(literal("request")
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes(context -> requestSnailName(context.getSource(), StringArgumentType.getString(context, "name")))
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
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(literal("set")
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("superpower", StringArgumentType.string())
                            .suggests((context, builder) -> CommandSource.suggestMatching(Superpowers.getImplementedStr(), builder))
                            .executes(context -> setSuperpower(context.getSource(), EntityArgumentType.getPlayer(context, "player"), StringArgumentType.getString(context, "superpower")))
                        )
                    )
                )
                .then(literal("resetAll")
                    .executes(context -> resetSuperpowers(context.getSource()))
                )
                .then(literal("setRandom")
                    .executes(context -> setRandomSuperpowers(context.getSource()))
                )
                .then(literal("get")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> getSuperpower(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("skipCooldown")
                    .executes(context -> skipSuperpowerCooldown(context.getSource()))
                )
                .then(literal("assignForRandomization")
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("superpower", StringArgumentType.string())
                            .suggests((context, builder) -> CommandSource.suggestMatching(Superpowers.getImplementedStr(), builder))
                            .executes(context -> assignSuperpower(context.getSource(), EntityArgumentType.getPlayer(context, "player"), StringArgumentType.getString(context, "superpower")))
                        )
                    )
                )
        );
    }

    public static int requestSnailName(ServerCommandSource source, String name) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.broadcastMessageToAdmins(Text.of("§7" + player.getNameForScoreboard() + " requests their snail name to be §f\""+name+"\"§7."));
        Text adminText = Text.literal("§7Click ").append(
                Text.literal("here")
                        .styled(style -> style
                                .withColor(Formatting.BLUE)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/snail names set " + player.getNameForScoreboard() + " "+name))
                                .withUnderline(true)
                        )).append(Text.of("§7 to accept."));
        OtherUtils.broadcastMessageToAdmins(adminText);
        return 1;
    }

    public static int getSnailTexturesInfo(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        NetworkHandlerServer.sendStringPacket(player, "snail_textures_info" ,"");

        return 1;
    }

    public static int getSnailTextures(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        List<String> textures = SnailSkinsServer.getAllSkins();
        if (textures.isEmpty()) {
            source.sendMessage(Text.of("§7No snail skins have been added yet. Run §f'/snail textures info'§7 to learn how to add them."));
            return -1;
        }
        source.sendMessage(Text.of("§7The following skins have been found: §f" + String.join(", ", textures)));
        return 1;
    }

    public static int chooseWildcard(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        if (source.getPlayer() == null) return -1;
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            source.sendError(Text.of("§cYou must have the Life Series mod installed §nclient-side§c to open the wildcard GUI."));
            return -1;
        }
        NetworkHandlerServer.sendStringPacket(source.getPlayer(), "select_wildcards", "true");
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

    public static int assignSuperpower(ServerCommandSource source, ServerPlayerEntity player, String name) {
        if (checkBanned(source)) return -1;
        if (!Superpowers.getImplementedStr().contains(name)) {
            source.sendError(Text.of("That superpower doesn't exist."));
            return -1;
        }
        Superpowers superpower = Superpowers.fromString(name);
        if (superpower == Superpowers.NONE) {
            source.sendError(Text.of("That superpower doesn't exist."));
            return -1;
        }
        SuperpowersWildcard.assignedSuperpowers.put(player.getUuid(), superpower);
        source.sendMessage(Text.of("§7Forced " + player.getNameForScoreboard()+"'s superpower to be §f" + name + "§7 when the next superpower randomization happens."));
        return 1;
    }

    public static int skipSuperpowerCooldown(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;
        Superpower superpower = SuperpowersWildcard.getSuperpowerInstance(player);
        if (superpower == null) {
            source.sendError(Text.of("You do not have an active superpower."));
            return -1;
        }
        superpower.cooldown = 0;
        NetworkHandlerServer.sendLongPacket(player, "superpower_cooldown", 0);
        source.sendMessage(Text.of("§7Your superpower cooldown has been skipped."));
        return 1;
    }

    public static int setRandomSuperpowers(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        SuperpowersWildcard.rollRandomSuperpowers();
        source.sendMessage(Text.of("§7Randomized everyone's superpowers."));
        return 1;
    }

    public static int resetSuperpowers(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        SuperpowersWildcard.resetAllSuperpowers();
        source.sendMessage(Text.of("§7Deactivated everyone's superpowers."));
        return 1;
    }

    public static int getSuperpower(ServerCommandSource source, ServerPlayerEntity player) {
        if (checkBanned(source)) return -1;
        Superpowers superpower = SuperpowersWildcard.getSuperpower(player);
        source.sendMessage(Text.of("§7"+player.getNameForScoreboard()+"'s superpower is: §f" + Superpowers.getString(superpower)));
        return 1;
    }

    public static int setSuperpower(ServerCommandSource source, ServerPlayerEntity player, String name) {
        if (checkBanned(source)) return -1;
        if (!Superpowers.getImplementedStr().contains(name)) {
            source.sendError(Text.of("That superpower doesn't exist."));
            return -1;
        }
        Superpowers superpower = Superpowers.fromString(name);
        if (superpower == Superpowers.NONE) {
            source.sendError(Text.of("That superpower doesn't exist."));
            return -1;
        }
        SuperpowersWildcard.setSuperpower(player, superpower);
        source.sendMessage(Text.of("§7Set " + player.getNameForScoreboard()+"'s superpower to: §f" + name));
        return 1;
    }

    public static int setSnailName(ServerCommandSource source, ServerPlayerEntity player, String name) {
        if (checkBanned(source)) return -1;
        Snails.setSnailName(player, name);
        source.sendMessage(Text.of("§7Set " + player.getNameForScoreboard()+"'s snail name to §f§o" + name));
        return 1;
    }

    public static int resetSnailName(ServerCommandSource source, ServerPlayerEntity player) {
        if (checkBanned(source)) return -1;
        Snails.resetSnailName(player);
        source.sendMessage(Text.of("§7Reset " + player.getNameForScoreboard()+"'s snail name to §f§o"+player.getNameForScoreboard()+"'s Snail"));
        return 1;
    }

    public static int getSnailName(ServerCommandSource source, ServerPlayerEntity player) {
        if (checkBanned(source)) return -1;
        source.sendMessage(Text.of("§7"+player.getNameForScoreboard()+"'s snail is called §f§o"+Snails.getSnailName(player)));
        return 1;
    }

    public static int deactivateWildcard(ServerCommandSource source, String wildcardName) {
        if (checkBanned(source)) return -1;
        if (wildcardName.equalsIgnoreCase("*")) {
            WildcardManager.onSessionEnd();
            source.sendMessage(Text.of("§7Deactivated all wildcards."));
            return 1;
        }
        Wildcards wildcard = Wildcards.getFromString(wildcardName);
        if (wildcard == Wildcards.NULL) {
            source.sendError(Text.of("That Wildcard doesn't exist."));
            return -1;
        }
        if (!WildcardManager.isActiveWildcard(wildcard)) {
            source.sendError(Text.of("That Wildcard is not active."));
            return -1;
        }
        WildcardManager.fadedWildcard();
        Wildcard wildcardInstance = WildcardManager.activeWildcards.get(wildcard);
        wildcardInstance.deactivate();
        WildcardManager.activeWildcards.remove(wildcard);

        source.sendMessage(Text.of("§7Deactivated §f" + wildcardName + "."));
        NetworkHandlerServer.sendUpdatePackets();
        return 1;
    }

    public static int activateWildcard(ServerCommandSource source, String wildcardName) {
        if (checkBanned(source)) return -1;
        if (wildcardName.equalsIgnoreCase("*")) {
            List<Wildcards> inactiveWildcards = Wildcards.getInactiveWildcards();
            for (Wildcards wildcard : inactiveWildcards) {
                if (wildcard == Wildcards.CALLBACK) continue;
                Wildcard wildcardInstance = Wildcards.getInstance(wildcard);
                if (wildcardInstance == null) continue;
                WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
            }

            WildcardManager.showDots();
            TaskScheduler.scheduleTask(90, () -> {
                for (Wildcard wildcard : WildcardManager.activeWildcards.values()) {
                    if (wildcard.active) continue;
                    wildcard.activate();
                }
                WildcardManager.showRainbowCryptTitle("All wildcards are active!");
            });
            NetworkHandlerServer.sendUpdatePackets();

            source.sendMessage(Text.of("§7Activated all wildcards (Except Callback)."));
            return 1;
        }
        Wildcards wildcard = Wildcards.getFromString(wildcardName);
        if (wildcard == Wildcards.NULL) {
            source.sendError(Text.of("That Wildcard doesn't exist."));
            return -1;
        }
        if (WildcardManager.isActiveWildcard(wildcard)) {
            source.sendError(Text.of("That Wildcard is already active."));
            return -1;
        }
        Wildcard actualWildcard = Wildcards.getInstance(wildcard);
        if (actualWildcard == null) {
            source.sendError(Text.of("That Wildcard has not been implemented yet."));
            return -1;
        }
        TaskScheduler.scheduleTask(89, () -> WildcardManager.activeWildcards.put(wildcard, actualWildcard));
        WildcardManager.activateWildcards();
        source.sendMessage(Text.of("§7Activated §f" + wildcardName + "."));
        return 1;
    }

    public static int listWildcards(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        source.sendMessage(Text.of("§7Available Wildcards: §f" + String.join(", ", Wildcards.getWildcardsStr())));
        return 1;
    }

    public static int listActiveWildcards(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        if (Wildcards.getActiveWildcardsStr().isEmpty()) {
            source.sendMessage(Text.of("§7There are no active Wildcards right now. \nA Wildcard will be randomly selected when the session starts, or you can use §r'/wildcard activate <wildcard>'§7 to activate a specific Wildcard."));
            return 1;
        }
        source.sendMessage(Text.of("§7Activated Wildcards: " + String.join(", ", Wildcards.getActiveWildcardsStr())));
        return 1;
    }
}
