package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers;

import com.cursee.ls_addon_support.dependencies.DependencyManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AnimalDisguise;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Invisibility;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Listening;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.PlayerDisguise;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.ShadowPlay;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.SuperPunch;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Superspeed;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Teleportation;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.TimeControl;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.TripleJump;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.WindCharge;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public enum Superpowers {
  NULL,

  TIME_CONTROL,
  CREAKING,
  WIND_CHARGE,
  ASTRAL_PROJECTION,
  SUPER_PUNCH,
  MIMICRY,
  TELEPORTATION,
  LISTENING,
  SHADOW_PLAY,
  FLIGHT,
  PLAYER_DISGUISE,
  ANIMAL_DISGUISE,
  TRIPLE_JUMP,
  INVISIBILITY,
  SUPERSPEED,
  NECROMANCY;

  public static List<Superpowers> getImplemented() {
    List<Superpowers> result = new ArrayList<>(List.of(Superpowers.values()));
    result.remove(NULL);
    //? if <= 1.21 {
    result.remove(CREAKING);
    result.remove(FLIGHT);
    //?}
    if (!DependencyManager.voicechatLoaded()) {
      result.remove(LISTENING);
    }
    return result;
  }

  public static List<String> getImplementedStr() {
    List<String> result = new ArrayList<>();
    for (Superpowers superpower : getImplemented()) {
      result.add(superpower.getString());
    }
    return result;
  }

  public static List<Superpowers> getAll() {
    List<Superpowers> result = new ArrayList<>(List.of(Superpowers.values()));
    result.remove(NULL);
    return result;
  }

  public static List<String> getAllStr() {
    List<String> result = new ArrayList<>();
    for (Superpowers superpower : getAll()) {
      result.add(superpower.getString());
    }
    return result;
  }

  public static Superpowers fromString(String superpower) {
    try {
      return Enum.valueOf(Superpowers.class, superpower.toUpperCase());
    } catch (Exception e) {
    }
    return Superpowers.NULL;
  }

  @Nullable
  public Superpower getInstance(ServerPlayerEntity player) {
      if (this == TIME_CONTROL) {
          return new TimeControl(player);
      }
      if (this == WIND_CHARGE) {
          return new WindCharge(player);
      }
      if (this == ASTRAL_PROJECTION) {
          return new AstralProjection(player);
      }
      if (this == PLAYER_DISGUISE) {
          return new PlayerDisguise(player);
      }
      if (this == ANIMAL_DISGUISE) {
          return new AnimalDisguise(player);
      }
      if (this == SUPER_PUNCH) {
          return new SuperPunch(player);
      }
      if (this == MIMICRY) {
          return new Mimicry(player);
      }
      if (this == TELEPORTATION) {
          return new Teleportation(player);
      }
      if (this == SHADOW_PLAY) {
          return new ShadowPlay(player);
      }
      if (this == INVISIBILITY) {
          return new Invisibility(player);
      }
      if (this == TRIPLE_JUMP) {
          return new TripleJump(player);
      }
      if (this == SUPERSPEED) {
          return new Superspeed(player);
      }
      if (this == NECROMANCY) {
          return new Necromancy(player);
      }
    //? if >= 1.21.2 {
        /*if (this == CREAKING) return new Creaking(player);
        if (this == FLIGHT) return new Flight(player);
        *///?}
    if (DependencyManager.voicechatLoaded()) {
        if (this == LISTENING) {
            return new Listening(player);
        }
    }
    return null;
  }

  public String getString() {
    return this.toString().toLowerCase();
  }
}
