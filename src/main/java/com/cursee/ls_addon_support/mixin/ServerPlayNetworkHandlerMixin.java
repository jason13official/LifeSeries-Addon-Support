package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.entity.fakeplayer.FakePlayer;
import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import com.cursee.ls_addon_support.seasons.other.WatcherManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.WildLife;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PermissionManager;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.List;
import java.util.Set;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 1.21.2
/*import net.minecraft.entity.player.PlayerPosition;*/

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1)
public class ServerPlayNetworkHandlerMixin {

  @Unique
  private static final List<String> mutedCommands = List.of("msg", "tell", "whisper", "w", "me");

  @Inject(method = "handleDecoratedMessage", at = @At("HEAD"), cancellable = true)
  private void onHandleDecoratedMessage(SignedMessage message, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
    ServerPlayerEntity player = handler.player;

    if (ls$mute(handler.player, ci)) {
      return;
    }

    Text originalText = message.getContent();
    String originalContent = originalText.getString();
      if (!originalContent.contains(":")) {
          return;
      }

    String formattedContent = TextUtils.replaceEmotes(originalContent);

    if (!originalContent.equals(formattedContent)) {
      Text formattedContentText = Text.literal(formattedContent).setStyle(originalText.getStyle());
      Text finalMessage = TextUtils.format("<{}> {}", player, formattedContentText);

      PlayerUtils.broadcastMessage(finalMessage);
      ci.cancel();
    }
  }

  @Inject(method = "onPlayerInteractItem", at = @At("HEAD"))
  private void onPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
    ServerPlayerEntity player = handler.player;
    if (currentSeason instanceof WildLife) {
      WildcardManager.onUseItem(player);
    }
  }

  //? if <= 1.21 {
  @Inject(method = "requestTeleport(DDDFFLjava/util/Set;)V", at = @At("TAIL"))
  public void requestTeleport(double x, double y, double z, float yaw, float pitch,
      Set<PositionFlag> flags, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "requestTeleport(Lnet/minecraft/entity/player/PlayerPosition;Ljava/util/Set;)V", at = @At("TAIL"))
    public void requestTeleport(PlayerPosition pos, Set<PositionFlag> flags, CallbackInfo ci) {
    *///?}
    ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
    ServerPlayerEntity player = handler.getPlayer();
    if (player instanceof FakePlayer) {
      ServerWorld world = PlayerUtils.getServerWorld(player);
      if (world.getPlayerByUuid(player.getUuid()) != null) {
        handler.syncWithPlayerPosition();
        world.getChunkManager().updatePosition(player);
      }
    }
  }

  @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
  private void executeCommand(String command, CallbackInfo ci) {
    ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
    for (String mutedCmd : mutedCommands) {
      if (command.startsWith(mutedCmd + " ")) {
        boolean stoppedCommand = ls$mute(handler.player, ci);
          if (stoppedCommand) {
              return;
          }
      }
    }
  }

  @Inject(method = "handleCommandExecution", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;execute(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"), cancellable = true)
  private void handleCommandExecution(ChatCommandSignedC2SPacket packet,
      LastSeenMessageList lastSeenMessages, CallbackInfo ci) {
    ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
    for (String command : mutedCommands) {
      if (packet.command().startsWith(command + " ")) {
        boolean stoppedCommand = ls$mute(handler.player, ci);
          if (stoppedCommand) {
              return;
          }
      }
    }
  }

  @Unique
  private boolean ls$mute(ServerPlayerEntity player, CallbackInfo ci) {
    if (player == null || PermissionManager.isAdmin(player)) {
      return false;
    }
    if (TriviaWildcard.bots.containsKey(player.getUuid())) {
      TriviaBot bot = TriviaWildcard.bots.get(player.getUuid());
      if (bot.interactedWith && !bot.submittedAnswer) {
        player.sendMessage(Text.of("<Trivia Bot> No phoning a friend allowed!"));
        ci.cancel();
        return true;
      }
    }

    if (currentSeason.WATCHERS_MUTED && WatcherManager.isWatcher(player)) {
      player.sendMessage(Text.of(
          "Watchers aren't allowed to talk in chat! Admins can change this behavior in the config."));
      ci.cancel();
      return true;
    }
    if (currentSeason.MUTE_DEAD_PLAYERS && !livesManager.isAlive(player)
        && !WatcherManager.isWatcher(player)) {
      player.sendMessage(Text.of(
          "Dead players aren't allowed to talk in chat! Admins can change this behavior in the config."));
      ci.cancel();
      return true;
    }
    return false;
  }

  @Inject(method = "onPlayerAction", at = @At("RETURN"))
  public void onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
    ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (packet.getAction() == PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
      currentSeason.onUpdatedInventory(handler.player);
    }
  }
}
