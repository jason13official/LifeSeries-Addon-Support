package net.mat0u5.lifeseries.series.wildlife.morph;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.client.ClientUtils;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MorphComponent {
    private static final String MORPH_NBT_KEY = "morph";
    private static final String TYPE_KEY = "type";

    private UUID playerUUID;
    @Nullable
    private EntityType<?> morph = null;
    private LivingEntity dummy = null;

    public MorphComponent(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setMorph(EntityType<?> morph) {
        this.morph = morph;

        if (Main.isLogicalSide()) {
            ServerPlayerEntity serverPlayer = PlayerUtils.getPlayer(playerUUID);
            if (serverPlayer != null) {
                if (morph == null) {
                    SizeShifting.setPlayerSizeUnchecked(serverPlayer, 1);
                }
                else {
                    //? if <= 1.21 {
                    Entity entity = morph.create(serverPlayer.getWorld());
                    //?} else {
                    /*Entity entity = morph.create(serverPlayer.getWorld(), SpawnReason.COMMAND);
                     *///?}
                    if (entity != null) {
                        ((IMorph) entity).setFromMorph(true);
                        EntityDimensions dimensions = entity.getDimensions(EntityPose.STANDING);
                        double scaleY = dimensions.height() / PlayerEntity.STANDING_DIMENSIONS.height();
                        double scaleX = dimensions.width() / PlayerEntity.STANDING_DIMENSIONS.width();
                        double scale = Math.clamp(Math.min(scaleX, scaleY), 0.1, 1.0);
                        if (scale != serverPlayer.getScale()) SizeShifting.setPlayerSizeUnchecked(serverPlayer, 0.1);
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
        if (morph == null) return "null";
        return Registries.ENTITY_TYPE.getId(morph).toString();
    }

    public void clientTick() {
        if(isMorphed() && morph != null){
            PlayerEntity player = ClientUtils.getPlayer(playerUUID);
            if (player == null) return;

            boolean isHorse = morph == EntityType.HORSE || morph == EntityType.SKELETON_HORSE || morph == EntityType.ZOMBIE_HORSE;
            boolean fixedHead = isHorse || morph == EntityType.GOAT;
            boolean clampedPitch = isHorse || morph == EntityType.GOAT;
            boolean reversePitch = morph == EntityType.PHANTOM;

            if (dummy == null || dummy.getType() != morph) {
                //? if <= 1.21 {
                Entity entity = morph.create(player.getWorld());
                //?} else {
                /*Entity entity = morph.create(player.getWorld(), SpawnReason.COMMAND);
                 *///?}
                if (entity != null) ((IMorph) entity).setFromMorph(true);
                if(!(entity instanceof LivingEntity)){
                    morph = null;
                    return;
                }
                dummy = (LivingEntity) entity;
            }
            //? if <= 1.21.4 {
            dummy.prevX = player.prevX;
            dummy.prevY = player.prevY;
            dummy.prevZ = player.prevZ;
            dummy.prevBodyYaw = player.prevBodyYaw;
            if (!fixedHead) {
                dummy.prevHeadYaw = player.prevHeadYaw;
            }
            else {
                dummy.prevHeadYaw = player.prevBodyYaw;
            }

            if (!clampedPitch) {
                dummy.prevPitch = player.prevPitch;
            }
            else {
                dummy.prevPitch = Math.clamp(player.prevPitch, -28, 28);
            }
            if (reversePitch) dummy.prevPitch *= -1;
            //?} else {
            /*dummy.lastX = player.lastX;
            dummy.lastY = player.lastY;
            dummy.lastZ = player.lastZ;
            dummy.lastBodyYaw = player.lastBodyYaw;
            if (!fixedHead) {
                dummy.lastHeadYaw = player.lastHeadYaw;
            }
            else {
                dummy.lastHeadYaw = player.lastBodyYaw;
            }

            if (!clampedPitch) {
                dummy.lastPitch = player.lastPitch;
            }
            else {
                dummy.lastPitch = Math.clamp(player.lastPitch, -28, 28);
            }
            if (reversePitch) dummy.lastPitch *= -1;
            *///?}

            //Some math to synchronize the morph limbs with the player limbs
            //? if <= 1.21.4 {
            float prevPlayerSpeed = (player.limbAnimator.getSpeed(-1)+player.limbAnimator.getSpeed())/2;
            //?} else {
            /*float prevPlayerSpeed = (player.limbAnimator.getAmplitude(-1)+player.limbAnimator.getSpeed())/2;
             *///?}
            dummy.limbAnimator.setSpeed(prevPlayerSpeed);
            //? if <= 1.21 {
            dummy.limbAnimator.updateLimbs(player.limbAnimator.getPos() - dummy.limbAnimator.getPos(), 1);
            //?} else if <= 1.21.4 {
            /*dummy.limbAnimator.updateLimbs(player.limbAnimator.getPos() - dummy.limbAnimator.getPos(), 1, 1);
             *///?} else {
            /*dummy.limbAnimator.updateLimbs(player.limbAnimator.getAnimationProgress() - dummy.limbAnimator.getAnimationProgress(), 1, 1);
             *///?}
            dummy.limbAnimator.setSpeed(player.limbAnimator.getSpeed());

            dummy.lastHandSwingProgress = player.lastHandSwingProgress;
            dummy.handSwingProgress = player.handSwingProgress;
            dummy.handSwinging = player.handSwinging;
            dummy.handSwingTicks = player.handSwingTicks;

            dummy.lastRenderX = player.lastRenderX;
            dummy.lastRenderY = player.lastRenderY;
            dummy.lastRenderZ = player.lastRenderZ;

            dummy.setPosition(player.getPos());
            dummy.setBodyYaw(player.bodyYaw);
            if (!fixedHead) {
                dummy.setHeadYaw(player.headYaw);
            }
            else {
                dummy.setHeadYaw(player.bodyYaw);
            }

            if (!clampedPitch) {
                dummy.setPitch(player.getPitch());
            }
            else {
                dummy.setPitch(Math.clamp(player.getPitch(), -28, 28));
            }
            if (reversePitch) dummy.setPitch(dummy.getPitch() * -1);

            dummy.setSneaking(player.isSneaking());
            dummy.age = player.age;
        }
    }
}

