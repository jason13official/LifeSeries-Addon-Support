package com.cursee.ls_addon_support.mixin;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.util.function.Supplier;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerCommandSource.class, priority = 1)
public class ServerCommandSourceMixin {

  @Inject(method = "sendFeedback", at = @At("HEAD"))
  public void sendFeedback(Supplier<Text> feedbackSupplier, boolean broadcastToOps,
      CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
      if (!broadcastToOps) {
          return;
      }
    Text text = feedbackSupplier.get();
    String sourceStr = "null";
    ServerCommandSource source = (ServerCommandSource) (Object) this;
      if (source.isSilent()) {
          return;
      }
    if (!source.isExecutedByPlayer()) {
      sourceStr = "console";
    }
    if (source.getPlayer() != null) {
      sourceStr = source.getPlayer().getName().getString();
    }
    SessionTranscript.addMessageWithTime(
        TextUtils.formatString("[COMMAND (source: {})] {}", sourceStr, text.getString()));
  }
}
