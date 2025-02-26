package net.mat0u5.lifeseries.mixin.superpowers.client;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ParticleManager.class, priority = 1)
public class ParticleManagerMixin {

    @ModifyArg(
            method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/client/particle/Particle;)V"),
            index = 0
    )
    private Particle modifyInvisibilityParticleAfterCreation(Particle particle) {
        if (particle == null) return particle;
        if (particle instanceof SpellParticle effectParticle) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null) return particle;

            ParticleAccessor accessor = (ParticleAccessor) particle;
            for (PlayerEntity player : client.world.getPlayers()) {
                if (player.squaredDistanceTo(accessor.getXPos(), accessor.getYPos(), accessor.getZPos()) <= 49) {
                    if (MainClient.invisiblePlayers.containsKey(player.getUuid())) {
                        long time = MainClient.invisiblePlayers.get(player.getUuid());
                        if (time > System.currentTimeMillis() || time == -1) {
                            accessor.setAlpha(0.11f);
                            return particle;
                        }
                    }
                }
            }
        }
        return particle;
    }
}
