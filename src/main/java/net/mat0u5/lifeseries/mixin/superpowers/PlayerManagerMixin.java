package net.mat0u5.lifeseries.mixin.superpowers;

import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/*
 * This file includes code from the Fabric Carpet project: https://github.com/gnembon/fabric-carpet
 *
 * Used and modified under the MIT License.
 */
@Mixin(value = PlayerManager.class, priority = 1)
public class PlayerManagerMixin {
    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), cancellable = true)
    public void broadcast(Text message, boolean overlay, CallbackInfo ci) {
        if (message.getString().contains("`")) ci.cancel();//TODO
    }

    @Inject(method = "loadPlayerData", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    public void loadPlayerData(ServerPlayerEntity player, CallbackInfoReturnable<Optional<NbtCompound>> cir) {
        if (player instanceof FakePlayer fakePlayer) {
            fakePlayer.fixStartingPosition.run();
        }
    }
}
