package com.cursee.ls_addon_support.mixin;

import com.cursee.ls_addon_support.entity.fakeplayer.FakePlayer;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerManager.class, priority = 1)
public class PlayerManagerMixin {

  @Inject(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Z)V", at = @At("HEAD"), cancellable = true)
  public void broadcast(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory,
      boolean overlay, CallbackInfo ci) {
      if (message.getString().contains("`")) {
          ci.cancel();
      }
  }

  @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At("HEAD"), cancellable = true)
  public void broadcast(SignedMessage message, Predicate<ServerPlayerEntity> shouldSendFiltered,
      ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
      if (message.getContent().getString().contains("`")) {
          ci.cancel();
      }
  }

  @Inject(method = "loadPlayerData", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
  public void loadPlayerData(ServerPlayerEntity player, ErrorReporter errorReporter, CallbackInfoReturnable<Optional<ReadView>> cir) {
    if (player instanceof FakePlayer fakePlayer) {
      fakePlayer.fixStartingPosition.run();
    }
  }
}
