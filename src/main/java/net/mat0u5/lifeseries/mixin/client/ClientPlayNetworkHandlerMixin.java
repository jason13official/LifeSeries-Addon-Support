package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.Trivia;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 1)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (Trivia.isDoingTrivia()) {
            //? if <= 1.21 {
            MinecraftClient.getInstance().player.sendMessage(Text.of("<Trivia Bot> No phoning a friend allowed!"));
            //?} else {
            /*MinecraftClient.getInstance().player.sendMessage(Text.of("<Trivia Bot> No phoning a friend allowed!"), false);
             *///?}
            ci.cancel();
            return;
        }
        if (MainClient.mutedForTicks > 0) {
            //? if <= 1.21 {
            MinecraftClient.getInstance().player.sendMessage(Text.of("Dead players aren't allowed to talk in chat! Admins can change this behavior."));
             //?} else {
            /*MinecraftClient.getInstance().player.sendMessage(Text.of("Dead players aren't allowed to talk in chat! Admins can change this behavior."), false);
            *///?}
            ci.cancel();
            return;
        }
    }

    @Unique
    private static final List<String> notAllowedCommand = List.of("msg", "tell", "whisper", "w", "me");
    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void onSendChatCommand(String command, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!Trivia.isDoingTrivia()) return;
        for (String s : notAllowedCommand) {
            if (command.startsWith(s+" ")) {
                //? if <= 1.21 {
                MinecraftClient.getInstance().player.sendMessage(Text.of("<Trivia Bot> No phoning a friend allowed!"));
                //?} else {
                /*MinecraftClient.getInstance().player.sendMessage(Text.of("<Trivia Bot> No phoning a friend allowed!"), false);
                 *///?}
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "handlePlayerListAction", at = @At("HEAD"), cancellable = true)
    private void handlePlayerListAction(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry, CallbackInfo ci) {
        if (action == PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME) return;
        if (receivedEntry.profile() != null && receivedEntry.profile().getName().startsWith("`")) {
            ci.cancel();
        }
    }
}
