package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//? if >= 1.21.2 {
/*import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.particle.TrailParticleEffect;
*///?}

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.Main.server;

public class Creaking extends ToggleableSuperpower {

    private final List<String> createdTeams = new ArrayList<>();
    //? if >= 1.21.2 {
    /*private final List<CreakingEntity> createdEntities = new ArrayList<>();
    *///?}

    public Creaking(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.CREAKING;
    }

    @Override
    public void tick() {
        if (!active) return;
        //? if >= 1.21.2 {
        /*spawnTrailParticles();
        *///?}
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;

        Team playerTeam = TeamUtils.getPlayerTeam(player);
        if (playerTeam == null) return;
        String newTeamName = "creaking_"+player.getNameForScoreboard();
        TeamUtils.deleteTeam(newTeamName);
        TeamUtils.createTeam(newTeamName, playerTeam.getColor());
        createdTeams.add(newTeamName);

        //? if >= 1.21.2 {
        /*for (int i = 0; i < 3; i++) {
            BlockPos spawnPos =  getCloseBlockPos(player.getServerWorld(), player.getBlockPos(), 6);
            CreakingEntity creaking = EntityType.CREAKING.spawn(player.getServerWorld(), spawnPos, SpawnReason.COMMAND);
            if (creaking != null) {
                creaking.setInvulnerable(true);
                createdEntities.add(creaking);
                makeFriendly(newTeamName, creaking, player);
            }
        }
        *///?}
    }

    @Override
    public void deactivate() {
        // Also gets triggered when the players team is changed.
        super.deactivate();
        //? if >= 1.21.2 {
        /*if (server != null) {
            createdEntities.forEach(Entity::discard);
            createdEntities.clear();
        }
        for (String teamAdded : createdTeams) {
            TeamUtils.deleteTeam(teamAdded);
        }
        createdTeams.clear();
        if (getPlayer() != null) {
            if (TeamUtils.getPlayerTeam(getPlayer()) == null) {
                currentSeries.reloadPlayerTeam(getPlayer());
            }
        }
        *///?}
    }

    @Override
    public int deactivateCooldownMillis() {
        return 10000;
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
            if (world.getBlockState(newPos).isAir() && world.getBlockState(pos.up(yOffset+1)).isAir() && world.getBlockState(pos.up(yOffset+2)).isAir()) {
                return newPos;
            }
        }
        return null;
    }

    private static void makeFriendly(String teamName, Entity entity, ServerPlayerEntity player) {
        TeamUtils.addEntityToTeam(teamName, player);
        TeamUtils.addEntityToTeam(teamName, entity);
        player.getServerWorld().spawnParticles(
                ParticleTypes.EXPLOSION,
                entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(),
                1, 0, 0, 0, 0
        );
    }
    //? if >= 1.21.2 {
    /*public void spawnTrailParticles() {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        ServerWorld world = player.getServerWorld();
        if (world == null) return;
        for (CreakingEntity creakingEntity : createdEntities) {
            if (creakingEntity.getRandom().nextInt(50)==0) {
                spawnTrailParticles(creakingEntity, 1, false);
            }
            if (creakingEntity.hurtTime > 0) {
                spawnTrailParticles(creakingEntity, 4, true);
            }
        }
    }

    public void spawnTrailParticles(CreakingEntity creaking, int count, boolean towardsPlayer) {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        ServerWorld world = player.getServerWorld();
        if (world == null) return;

        int i = towardsPlayer ? 16545810 : 6250335;
        Random random = world.random;

        for(double d = 0.0; d < count; d++) {
            Box box = creaking.getBoundingBox();
            Vec3d vec3d = box.getMinPos().add(random.nextDouble() * box.getLengthX(), random.nextDouble() * box.getLengthY(), random.nextDouble() * box.getLengthZ());
            Vec3d vec3d2 = player.getPos().add(random.nextDouble() - 0.5, random.nextDouble(), random.nextDouble() - 0.5);

            if (!towardsPlayer) {
                Vec3d vec3d3 = vec3d;
                vec3d = vec3d2;
                vec3d2 = vec3d3;
            }

            //? if = 1.21.2 {
            /^TrailParticleEffect trailParticleEffect2 = new TrailParticleEffect(vec3d2, i);
            world.spawnParticles(trailParticleEffect2, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            ^///?} else if >= 1.21.4 {
            /^TrailParticleEffect trailParticleEffect2 = new TrailParticleEffect(vec3d2, i, random.nextInt(40) + 10);
            world.spawnParticles(trailParticleEffect2, true, true, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            ^///?}
        }
    }
    *///?}
}
