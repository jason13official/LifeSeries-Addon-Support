package net.mat0u5.lifeseries.seasons.secretsociety;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SocietyCommands {

    public static SecretSociety get() {
        return currentSeason.secretSociety;
    }

    public static boolean isAllowed() {
        return get().SOCIETY_ENABLED;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when the Secret Society has been enabled in the Life Series config."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                literal("initiate")
                        .executes(context -> initiate(context.getSource()))
        );
        dispatcher.register(
            literal("society")
                .then(literal("success")
                    .executes(context -> societySuccess(context.getSource()))
                )
                .then(literal("fail")
                    .executes(context -> societyFail(context.getSource()))
                )
                .then(literal("begin")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("secret_word", StringArgumentType.string())
                        .executes(context -> societyBegin(context.getSource(), StringArgumentType.getString(context, "secret_word")))
                    )
                    .executes(context -> societyBegin(context.getSource(), null))
                )
        );
    }

    public static int societyBegin(ServerCommandSource source, String word) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("Starting the Secret Society..."));
        society.startSociety(word);
        return 1;
    }

    public static int societyFail(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;

        if (!society.isMember(self)) {
            source.sendError(Text.of("You are not a member of the Secret Society"));
            return -1;
        }
        SocietyMember member = society.getMember(self);
        if (!member.initialized) {
            source.sendError(Text.of("You have not been initialized"));
            return -1;
        }

        society.endFail();
        SessionTranscript.societyEndFail(self);
        return 1;
    }

    public static int societySuccess(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;

        if (!society.isMember(self)) {
            source.sendError(Text.of("You are not a member of the Secret Society"));
            return -1;
        }
        SocietyMember member = society.getMember(self);
        if (!member.initialized) {
            source.sendError(Text.of("You have not been initialized"));
            return -1;
        }

        society.endSuccess();
        SessionTranscript.societyEndSuccess(self);
        return 1;
    }

    public static int initiate(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;

        if (!society.isMember(self)) {
            source.sendError(Text.of("You are not a member of the Secret Society"));
            return -1;
        }
        SocietyMember member = society.getMember(self);
        if (member.initialized) {
            source.sendError(Text.of("You have already been initialized"));
            return -1;
        }

        society.initializeMember(self);
        return 1;
    }
}
