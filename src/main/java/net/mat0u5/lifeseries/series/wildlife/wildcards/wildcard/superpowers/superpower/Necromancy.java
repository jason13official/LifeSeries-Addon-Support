package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeries;

public class Necromancy extends Superpower {
    public static final List<UUID> ressurectedPlayers = new ArrayList<>();
    private List<UUID> perPlayerRessurections = new ArrayList<>();

    public Necromancy(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.NECROMANCY;
    }

    @Override
    public int getCooldownMillis() {
        return 300000;
    }

    @Override
    public void activate() {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1, 1);

        List<ServerPlayerEntity> affectedPlayers = player.getServerWorld().getEntitiesByClass(ServerPlayerEntity.class, player.getBoundingBox().expand(10), playerEntity -> playerEntity.distanceTo(player) <= 10);
        StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 115, 0);
        for (ServerPlayerEntity affectedPlayer : affectedPlayers) {
            affectedPlayer.addStatusEffect(blindness);
        }

        TaskScheduler.scheduleTask(100, () -> {
            ServerPlayerEntity updatedPlayer = getPlayer();
            if (updatedPlayer != null) {
                List<ServerPlayerEntity> deadPlayers = getDeadPlayers();
                for (ServerPlayerEntity deadPlayer : deadPlayers) {
                    BlockPos tpTo = getCloseBlockPos(updatedPlayer.getServerWorld(), updatedPlayer.getBlockPos(), 3);
                    //? if <= 1.21 {
                    deadPlayer.teleport(updatedPlayer.getServerWorld(), tpTo.getX(), tpTo.getY(), tpTo.getZ(), EnumSet.noneOf(PositionFlag.class), deadPlayer.getYaw(), deadPlayer.getPitch());
                    //?} else {
                    /*deadPlayer.teleport(player.getServerWorld(), tpTo.getX(), tpTo.getY(), tpTo.getZ(), EnumSet.noneOf(PositionFlag.class), deadPlayer.getYaw(), deadPlayer.getPitch(), true);
                    *///?}
                    deadPlayer.changeGameMode(GameMode.SURVIVAL);
                    AttributeUtils.setMaxPlayerHealth(deadPlayer, 8);
                    WorldUitls.summonHarmlessLightning(deadPlayer.getServerWorld(), deadPlayer.getPos());
                    ressurectedPlayers.add(deadPlayer.getUuid());
                    perPlayerRessurections.add(deadPlayer.getUuid());
                }
            }
        });
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        for (ServerPlayerEntity player : getDeadPlayers()) {
            if (player.isSpectator()) continue;
            if (perPlayerRessurections.contains(player.getUuid())) {
                WorldUitls.summonHarmlessLightning(player.getServerWorld(), player.getPos());
                player.changeGameMode(GameMode.SPECTATOR);
            }
        }
        ressurectedPlayers.clear();
        perPlayerRessurections.clear();
    }

    public BlockPos getCloseBlockPos(ServerWorld world, BlockPos targetPos, double minDistanceFromTarget) {
        for (int attempts = 0; attempts < 20; attempts++) {
            Vec3d offset = new Vec3d(
                    world.random.nextDouble() * 2 - 1,
                    1,
                    world.random.nextDouble() * 2 - 1
            ).normalize().multiply(minDistanceFromTarget);

            BlockPos pos = targetPos.add((int) offset.getX(), 0, (int) offset.getZ());

            BlockPos validPos = findNearestAirBlock(pos, world);
            if (validPos != null) {
                return validPos;
            }
        }

        return targetPos;
    }

    private BlockPos findNearestAirBlock(BlockPos pos, World world) {
        for (int yOffset = -5; yOffset <= 5; yOffset++) {
            BlockPos newPos = pos.up(yOffset);
            if (world.getBlockState(newPos).isAir() && world.getBlockState(pos.up(yOffset + 1)).isAir()) {
                return newPos;
            }
        }
        return null;
    }

    public static List<ServerPlayerEntity> getDeadPlayers() {
        List<ServerPlayerEntity> deadPlayers = new ArrayList<>();
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (currentSeries.isAlive(player)) continue;
            deadPlayers.add(player);
        }
        return deadPlayers;
    }

    public static boolean shouldBeIncluded() {
        return !getDeadPlayers().isEmpty();
    }
}
