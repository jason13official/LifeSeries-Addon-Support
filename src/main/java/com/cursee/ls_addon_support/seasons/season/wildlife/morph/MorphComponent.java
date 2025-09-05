package com.cursee.ls_addon_support.seasons.season.wildlife.morph;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import com.cursee.ls_addon_support.utils.interfaces.IMorph;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class MorphComponent {

  private static final String MORPH_NBT_KEY = "morph";
  private static final String TYPE_KEY = "type";

  public UUID playerUUID;
  @Nullable
  public EntityType<?> morph = null;
  public LivingEntity dummy = null;

  public MorphComponent(UUID playerUUID) {
    this.playerUUID = playerUUID;
  }

  public void setMorph(EntityType<?> morph) {
    this.morph = morph;

    if (LSAddonSupport.isLogicalSide()) {
      ServerPlayerEntity serverPlayer = PlayerUtils.getPlayer(playerUUID);
      if (serverPlayer != null) {
        if (morph == null) {
          SizeShifting.setPlayerSizeUnchecked(serverPlayer, 1);
        } else {
          Entity entity = morph.create(serverPlayer.getWorld(), SpawnReason.COMMAND);
          if (entity != null) {
            ((IMorph) entity).setFromMorph(true);
            EntityDimensions dimensions = entity.getDimensions(EntityPose.STANDING);
            double scaleY = dimensions.height() / PlayerEntity.STANDING_DIMENSIONS.height();
            double scaleX = dimensions.width() / PlayerEntity.STANDING_DIMENSIONS.width();
            double scale = Math.clamp(Math.min(scaleX, scaleY), 0.1, 1.0);
              if (scale != serverPlayer.getScale()) {
                  SizeShifting.setPlayerSizeUnchecked(serverPlayer, 0.1);
              }
          }
        }
      }
    }
  }

  public boolean isMorphed() {
    return morph != null;
  }

  @Nullable
  public LivingEntity getDummy() {
    return dummy;
  }

  @Nullable
  public EntityType<?> getType() {
    return morph;
  }

  public String getTypeAsString() {
      if (morph == null) {
          return "null";
      }
    return Registries.ENTITY_TYPE.getId(morph).toString();
  }
}

