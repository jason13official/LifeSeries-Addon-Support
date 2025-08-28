package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = ServerWorld.class, priority = 1)
public class ServerWorldMixin {

    @Inject(method = "sendEntityStatus", at = @At("HEAD"))
    public void broadcast(Entity entity, byte status, CallbackInfo ci) {
        if (status != (byte) 35 || currentSeason.getSeason() != Seasons.SECRET_LIFE) {
            return;
        }
        // This sound doesnt exist client-side, so it won't double
        PlayerUtils.playSoundWithSourceToPlayers(entity, SoundEvent.of(Identifier.of("secretlife_normal_totem")), entity.getSoundCategory(), 1, 1);
    }
}
