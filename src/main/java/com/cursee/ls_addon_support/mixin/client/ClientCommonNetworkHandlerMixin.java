package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.List;
import java.util.UUID;
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

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {

  @Unique
  private static final List<String> ls$bannedURLs = List.of(
      "github.com/Mat0u5/LifeSeries-Resources"
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
    UUID uuid = packet.id();
    boolean banned = false;
    for (String bannedURL : ls$bannedURLs) {
      if (url.contains(bannedURL)) {
        banned = true;
        break;
      }
    }
      if (!banned) {
          return;
      }
    LSAddonSupport.LOGGER.info("Skipping resourcepack download ({})", url);
    this.connection.send(
        new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.ACCEPTED));
    this.connection.send(
        new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.DOWNLOADED));
    this.connection.send(new ResourcePackStatusC2SPacket(uuid,
        ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
    ci.cancel();
  }
}
