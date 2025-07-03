package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.blacklist;
import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = ServerPlayerEntity.class, priority = 1)
public class ServerPlayerEntityMixin {
    @Inject(method = "getRespawnTarget", at = @At("HEAD"))
    private void getRespawnTarget(boolean alive, TeleportTarget.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<TeleportTarget> cir) {
        if (!Main.isLogicalSide()) return;
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        UUID uuid = player.getUuid();
        TaskScheduler.scheduleTask(1, () -> currentSeason.onPlayerRespawn(Objects.requireNonNull(Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayer(uuid))));
    }

    @Inject(method = "openHandledScreen", at = @At("HEAD"))
    private void onInventoryOpen(@Nullable NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir) {
        if (!Main.isLogicalSide()) return;
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (blacklist != null) {
            TaskScheduler.scheduleTask(1, () -> {
                player.currentScreenHandler.getStacks().forEach(itemStack -> blacklist.processItemStack(player, itemStack));
                PlayerUtils.updatePlayerInventory(player);
            });
        }
    }

    @Inject(method = "sendMessageToClient", at = @At("HEAD"), cancellable = true)
    private void sendMessageToClient(Text message, boolean overlay, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player instanceof FakePlayer) {
            ci.cancel();
        }
    }

    @Inject(method = "acceptsMessage", at = @At("HEAD"), cancellable = true)
    private void acceptsMessage(boolean overlay, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player instanceof FakePlayer) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "acceptsChatMessage", at = @At("HEAD"), cancellable = true)
    private void acceptsChatMessage(CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player instanceof FakePlayer) {
            cir.setReturnValue(false);
        }
    }

}
