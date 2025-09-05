package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;

import com.cursee.ls_addon_support.entity.fakeplayer.FakePlayer;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class AstralProjection extends ToggleableSuperpower {

  @Nullable
  public FakePlayer clone;
  @Nullable
  private Vec3d startedPos;
  @Nullable
  private ServerWorld startedWorld;
  private float[] startedLooking = new float[2];
  private GameMode startedGameMode = GameMode.SURVIVAL;

  public AstralProjection(ServerPlayerEntity player) {
    super(player);
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.ASTRAL_PROJECTION;
  }

  @Override
  public void activate() {
    super.activate();
    resetParams();
    startProjection();
  }

  @Override
  public void deactivate() {
    super.deactivate();
    cancelProjection();
    resetParams();
  }

  @Override
  public int deactivateCooldownMillis() {
    return 5000;
  }

  public void resetParams() {
    clone = null;
    startedPos = null;
    startedLooking = new float[2];
    startedWorld = null;
  }

  public void startProjection() {
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
      if (player.isSpectator()) {
          return;
      }
    player.playSoundToPlayer(SoundEvents.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundCategory.MASTER,
        0.3f, 1);

    String fakePlayerName = "`" + player.getNameForScoreboard();

    startedPos = player.getPos();
    startedLooking[0] = player.getYaw();
    startedLooking[1] = player.getPitch();
    startedWorld = PlayerUtils.getServerWorld(player);
    startedGameMode = player.interactionManager.getGameMode();
    player.changeGameMode(GameMode.SPECTATOR);
    PlayerInventory inv = player.getInventory();

    FakePlayer.createFake(fakePlayerName, player.getServer(), startedPos, startedLooking[0],
        startedLooking[1], player.getServer().getOverworld().getRegistryKey(),
        startedGameMode, false, inv, player.getUuid()).thenAccept((fakePlayer) -> {
      clone = fakePlayer;
      String name = TextUtils.textToLegacyString(player.getStyledDisplayName());
      NetworkHandlerServer.sendPlayerDisguise(clone.getUuid().toString(),
          clone.getName().getString(), player.getUuid().toString(), name);
    });
  }

  public void cancelProjection() {
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }

    Vec3d toBackPos = startedPos;
    if (clone != null) {
      toBackPos = clone.getPos();
      clone.networkHandler.onDisconnected(new DisconnectionInfo(Text.empty()));
      NetworkHandlerServer.sendPlayerDisguise(clone.getUuid().toString(),
          clone.getName().getString(), "", "");
    }

      if (!livesManager.isAlive(player)) {
          return;
      }

    if (startedWorld != null && toBackPos != null) {
      PlayerUtils.teleport(player, startedWorld, toBackPos, startedLooking[0], startedLooking[1]);
    }
    player.changeGameMode(startedGameMode);
    player.playSoundToPlayer(SoundEvents.ENTITY_EVOKER_DEATH, SoundCategory.MASTER, 0.3f, 1);
  }


  public void onDamageClone(ServerWorld world, DamageSource source, float amount) {
    deactivate();
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    player.damage(world, source, amount);
  }
}
