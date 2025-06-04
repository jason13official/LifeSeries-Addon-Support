package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.entity.fakeplayer.FakeClientConnection;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.Main.server;

@Mixin(value = ServerCommonNetworkHandler.class, priority = 1)
public class ServerCommonNetworkHandlerMixin {

    @Final
    @Shadow
    protected ClientConnection connection;

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        if (connection instanceof FakeClientConnection) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "send",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V")
    )
    public void send(ClientConnection instance, Packet<?> packet, PacketCallbacks callbacks, boolean flush, Operation<Void> original) {
        if (connection instanceof FakeClientConnection) return;
        /*
        if (packet instanceof PlayerListS2CPacket playerListPacket && currentSeries != null && !currentSeries.TAB_LIST_SHOW_DEAD_PLAYERS) {
            if (!playerListPacket.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                original.call(instance, packet, callbacks, flush);
                return;
            }

            ServerPlayerEntity receivingPlayer = null;
            ServerCommonNetworkHandler handler = (ServerCommonNetworkHandler) (Object) this;
            if (handler instanceof ServerPlayNetworkHandler playHandler) {
                receivingPlayer = playHandler.getPlayer();
            }

            if (receivingPlayer != null && !currentSeries.isAlive(receivingPlayer)) {
                original.call(instance, packet, callbacks, flush);
                return;
            }

            List<ServerPlayerEntity> modifiedEntries = new ArrayList<>();
            int visible = 0;
            for (PlayerListS2CPacket.Entry playerUpdate : playerListPacket.getEntries()) {
                ServerPlayerEntity player = PlayerUtils.getPlayer(playerUpdate.profileId());
                if (currentSeries.isAlive(player)) {
                    visible++;
                    modifiedEntries.add(player);
                }
            }
            if (visible != playerListPacket.getEntries().size()) {
                if (!modifiedEntries.isEmpty()) {
                    original.call(instance, new PlayerListS2CPacket(playerListPacket.getActions(), modifiedEntries), callbacks, flush);
                }
                return;
            }
        }
        */
        original.call(instance, packet, callbacks, flush);
    }

    @Inject(method = "disconnect(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    public void disconnect(Text reason, CallbackInfo ci) {
        if (connection instanceof FakeClientConnection) {
            ci.cancel();
        }
    }
}
