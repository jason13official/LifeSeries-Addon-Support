package net.mat0u5.lifeseries.seasons.season.secretlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SecretLifeCommands {

    public static boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.SECRET_LIFE;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when playing Secret Life."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {

        dispatcher.register(
            literal("health")
                .executes(context -> showHealth(context.getSource()))
                .then(literal("sync")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .executes(context -> syncHealth(
                        context.getSource())
                    )
                )
                .then(literal("add")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> healthManager(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"), 1, false)
                        )
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgumentType.getPlayer(context, "player"), DoubleArgumentType.getDouble(context, "amount"), false)
                            )
                        )
                    )
                )
                .then(literal("remove")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> healthManager(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"), -1, false)
                        )
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgumentType.getPlayer(context, "player"), -DoubleArgumentType.getDouble(context, "amount"), false)
                            )
                        )
                    )
                )
                .then(literal("set")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgumentType.getPlayer(context, "player"), DoubleArgumentType.getDouble(context, "amount"), true)
                            )
                        )
                    )
                )
                .then(literal("get")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> getHealthFor(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                        )
                    )
                )
                .then(literal("reset")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> resetHealth(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                        )
                    )
                )
                .then(literal("resetAll")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .executes(context -> resetAllHealth(
                        context.getSource())
                    )
                )
        );
        dispatcher.register(
            literal("task")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(literal("succeed")
                            .then(argument("player", EntityArgumentType.player())
                                    .executes(context -> succeedTask(
                                            context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                                    )
                            )
                    )
                    .then(literal("fail")
                            .then(argument("player", EntityArgumentType.player())
                                    .executes(context -> failTask(
                                            context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                                    )
                            )
                    )
                    .then(literal("reroll")
                            .then(argument("player", EntityArgumentType.player())
                                    .executes(context -> rerollTask(
                                            context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                                    )
                            )
                    )
                    .then(literal("assignRandom")
                            .then(argument("player", EntityArgumentType.players())
                                    .executes(context -> assignTask(
                                            context.getSource(), EntityArgumentType.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("clearTask")
                            .then(argument("player", EntityArgumentType.players())
                                    .executes(context -> clearTask(
                                            context.getSource(), EntityArgumentType.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("set")
                            .then(argument("player", EntityArgumentType.player())
                                    .then(argument("type", StringArgumentType.string())
                                            .suggests((context, builder) -> CommandSource.suggestMatching(List.of("easy","hard","red"), builder))
                                            .then(argument("task", StringArgumentType.greedyString())
                                                    .executes(context -> setTask(
                                                            context.getSource(),
                                                            EntityArgumentType.getPlayer(context, "player"),
                                                            StringArgumentType.getString(context, "type"),
                                                            StringArgumentType.getString(context, "task")
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
        );
        dispatcher.register(
            literal("gift")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> gift(
                        context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                    )
                )
        );
        dispatcher.register(
            literal("secretlife")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(literal("changeLocations")
                    .executes(context -> changeLocations(
                        context.getSource())
                    )
                )
        );
    }

    public static int setTask(ServerCommandSource source, ServerPlayerEntity target, String type, String task) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        TaskType taskType = TaskType.EASY;

        if (type.equalsIgnoreCase("hard")) taskType = TaskType.HARD;
        if (type.equalsIgnoreCase("red")) taskType = TaskType.RED;

        TaskManager.preAssignedTasks.put(target.getUuid(), new Task(task, taskType));

        if (TaskManager.removePlayersTaskBook(target)) {
            TaskManager.assignRandomTaskToPlayer(target, taskType);
            AnimationUtils.playTotemAnimation(target);
            OtherUtils.sendCommandFeedback(source, Text.of("Changed "+target.getNameForScoreboard()+"'s task."));
        }
        else {
            OtherUtils.sendCommandFeedback(source, Text.of("Pre-assigned "+target.getNameForScoreboard()+"'s task for randomization."));
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("They will be given the task book once you / the game rolls the tasks."));
        }

        return 1;
    }

    public static int changeLocations(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, Text.of("Changing Secret Life locations..."));
        TaskManager.deleteLocations();
        TaskManager.checkSecretLifePositions();
        return 1;
    }

    public static int clearTask(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        int removedFrom = 0;
        for (ServerPlayerEntity player : targets) {
            if (TaskManager.removePlayersTaskBook(player)) removedFrom++;
        }

        OtherUtils.sendCommandFeedback(source, Text.of("Removed task book from " + removedFrom + " target"+(targets.size()==1?".":"s.")));
        return 1;
    }

    public static int assignTask(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("Assigning random tasks to " + targets.size() + " target"+(Math.abs(targets.size())!=1?"s":"")+"..."));
        TaskManager.chooseTasks(targets.stream().toList(), null);
        return 1;
    }

    public static int succeedTask(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("Succeeding task for " + target.getNameForScoreboard() + "..."));
        TaskManager.succeedTask(target);
        return 1;
    }

    public static int failTask(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("Failing task for " + target.getNameForScoreboard() + "..."));
        TaskManager.failTask(target);
        return 1;
    }

    public static int rerollTask(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("Rerolling task for " + target.getNameForScoreboard() + "..."));
        TaskManager.rerollTask(target);
        return 1;
    }

    public static final List<UUID> playersGiven = new ArrayList<>();
    public static int gift(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (target == null) return -1;
        SecretLife secretLife = (SecretLife) currentSeason;

        if (target == self) {
            source.sendError(Text.of("Nice Try."));
            return -1;
        }
        if (playersGiven.contains(self.getUuid())) {
            source.sendError(Text.of("You have already gifted a heart this session."));
            return -1;
        }
        if (!secretLife.isAlive(target)) {
            source.sendError(Text.of("That player is not alive."));
            return -1;
        }
        if (!secretLife.statusStarted()) {
            source.sendError(Text.of("The session has not started."));
            return -1;
        }
        playersGiven.add(self.getUuid());
        secretLife.addPlayerHealth(target, 2);
        Text senderMessage = Text.literal("You have gifted a heart to ").append(target.getStyledDisplayName()).append(Text.of("."));
        Text recipientMessage = Text.literal("").append(self.getStyledDisplayName()).append(Text.of("§a gave you a heart."));

        self.sendMessage(senderMessage);
        PlayerUtils.sendTitle(target, recipientMessage, 20, 20, 20);
        target.sendMessage(recipientMessage);
        AnimationUtils.createSpiral(target, 40);

        PlayerUtils.playSoundToPlayers(List.of(self,target), SoundEvent.of(Identifier.of("minecraft","secretlife_life")));

        return 1;
    }

    public static int showHealth(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        final ServerPlayerEntity self = source.getPlayer();

        if (self == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;

        if (!secretLife.isAlive(self)) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("You're dead..."));
            return -1;
        }

        double playerHealth = secretLife.getRoundedHealth(self);
        OtherUtils.sendCommandFeedbackQuiet(source, Text.literal("You have ").append(Text.of(String.valueOf(playerHealth))).append(Text.of(" health.")));

        return 1;
    }

    public static int getHealthFor(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;
        if (!secretLife.isAlive(target)) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.literal("").append(target.getStyledDisplayName()).append(Text.literal(" is dead.")));
            return -1;
        }

        MutableText pt1 = Text.literal("").append(target.getStyledDisplayName()).append(Text.literal(" has "));
        Text pt2 = Text.of(secretLife.getRoundedHealth(target)+" health.");
        OtherUtils.sendCommandFeedbackQuiet(source, pt1.append(pt2));
        return 1;
    }

    public static int syncHealth(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        SecretLife secretLife = (SecretLife) currentSeason;
        secretLife.syncAllPlayerHealth();
        return 1;
    }

    public static int healthManager(ServerCommandSource source, ServerPlayerEntity target, double amount, boolean setNotGive) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;
        if (setNotGive) {
            secretLife.setPlayerHealth(target,amount);
            OtherUtils.sendCommandFeedback(source, Text.literal("Set ").append(target.getStyledDisplayName()).append(Text.of("'s health to " + amount + ".")));
        }
        else {
            secretLife.addPlayerHealth(target,amount);
            String pt1 = amount >= 0 ? "Added" : "Removed";
            String pt2 = " "+Math.abs(amount)+" health";
            String pt3 = amount >= 0 ? " to " : " from ";
            OtherUtils.sendCommandFeedback(source, Text.of(pt1+pt2+pt3).copy().append(target.getStyledDisplayName()).append("."));
        }

        return 1;
    }

    public static int resetHealth(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;
        secretLife.resetPlayerHealth(target);

        OtherUtils.sendCommandFeedback(source, Text.literal("Reset ").append(target.getStyledDisplayName()).append(Text.of("'s health to 30.")));
        return 1;
    }

    public static int resetAllHealth(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;
        secretLife.resetAllPlayerHealth();

        OtherUtils.sendCommandFeedback(source, Text.literal("Reset everyone's health to 30."));
        return 1;
    }
}
