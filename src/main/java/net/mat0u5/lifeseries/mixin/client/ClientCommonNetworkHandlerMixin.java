package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {
    @Unique
    private static final List<String> bannedURLs = List.of(
            ""
    );

    @Shadow
    @Final
    protected ClientConnection connection;

    @Inject(
            method = "onResourcePackSend",
            at = @At(
                    target = "Lnet/minecraft/client/network/ClientCommonNetworkHandler;getParsedResourcePackUrl(Ljava/lang/String;)Ljava/net/URL;",
                    shift = At.Shift.AFTER,
                    value = "INVOKE"
            ),
            cancellable = true
    )
    public void onResourcePackSend(ResourcePackSendS2CPacket packet, CallbackInfo ci) {
        String url = packet.url();
        String hash = packet.hash();
        UUID uuid = packet.id();
        /*
        OtherUtils.log("-----");
        OtherUtils.log("RP url: " + url);
        OtherUtils.log("RP uuid: " + uuid);
        OtherUtils.log("RP hash: " + hash);
        */
        boolean banned = false;
        for (String bannedURL : bannedURLs) {
            if (url.contains(bannedURL)) {
                banned = true;
                break;
            }
        }
        if (!banned) return;
        Main.LOGGER.info("Skipping resourcepack download (" + url + ")");
        this.connection.send(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.ACCEPTED));
        this.connection.send(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.DOWNLOADED));
        this.connection.send(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
        ci.cancel();
    }
}
