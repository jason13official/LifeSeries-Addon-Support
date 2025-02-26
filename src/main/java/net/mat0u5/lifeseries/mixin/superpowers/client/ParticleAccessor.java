package net.mat0u5.lifeseries.mixin.superpowers.client;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {
    @Accessor("alpha")
    void setAlpha(float alpha);

    @Accessor("x")
    double getXPos();
    @Accessor("y")
    double getYPos();
    @Accessor("z")
    double getZPos();
}