package com.cursee.ls_addon_support.mixin.client;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityTrackingSoundInstance.class, priority = 1)
public interface EntityTrackingSoundInstanceAccessor {

  @Accessor("entity")
  Entity getEntity();
}