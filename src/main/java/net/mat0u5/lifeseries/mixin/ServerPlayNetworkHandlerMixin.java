package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "handleDecoratedMessage(Lnet/minecraft/network/message/SignedMessage;)V",
            at = @At("HEAD"), cancellable = true)
    private void onHandleDecoratedMessage(SignedMessage message, CallbackInfo ci) {
        if (!Main.isLogicalSide()) return;
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;
        Text originalText = message.getContent();
        String originalContent = originalText.getString();
        if (!originalContent.contains(":")) return;
        String formattedContent = TextUtils.replaceEmotes(originalContent);

        if (!originalContent.equals(formattedContent)) {
            Text playerNameWithFormatting = player.getDisplayName();
            Text formattedContentText = Text.literal(formattedContent).setStyle(originalText.getStyle());
            Text finalMessage = Text.empty().append("<").append(playerNameWithFormatting).append(">").append(formattedContentText);

            OtherUtils.broadcastMessage(finalMessage);
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInteractItem", at = @At("HEAD"))
    private void onPlayerAction(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
        if (!Main.isLogicalSide()) return;
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;
        if (currentSeries instanceof WildLife) {
            WildcardManager.onUseItem(player);
        }
    }

    //? if <= 1.21 {
    @Inject(method = "requestTeleport(DDDFFLjava/util/Set;)V", at = @At("TAIL"))
    public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<PositionFlag> flags, CallbackInfo ci) {
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
}
