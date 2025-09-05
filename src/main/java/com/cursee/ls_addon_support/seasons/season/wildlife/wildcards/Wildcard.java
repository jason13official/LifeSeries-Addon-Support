package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards;

import static com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager.getSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.wildlife.WildLife;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;

public abstract class Wildcard {

  public boolean active = false;

  public abstract Wildcards getType();

  public void activate() {
    WildLife season = getSeason();
      if (season == null) {
          return;
      }
    active = true;
    LSAddonSupport.LOGGER.info("[WildLife] Activated Wildcard: {}", getType());
    SessionTranscript.activateWildcard(getType());
  }

  public void deactivate() {
    WildLife season = getSeason();
      if (season == null) {
          return;
      }
    active = false;
    LSAddonSupport.LOGGER.info("[WildLife] Dectivated Wildcard: {}", getType());
    SessionTranscript.deactivateWildcard(getType());
  }

  public void tickSessionOn() {
  }

  public void tick() {
  }

  public void softTick() {
  }

}
