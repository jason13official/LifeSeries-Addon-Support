package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards;

import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.Callback;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.Hunger;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.MobSwap;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import java.util.ArrayList;
import java.util.List;

public class Wildcards {
  public static final String NULL = "NULL";
  public static final String SIZE_SHIFTING = "SIZE_SHIFTING";
  public static final String HUNGER = "HUNGER";
  public static final String SNAILS = "SNAILS";
  public static final String TIME_DILATION = "TIME_DILATION";
  public static final String TRIVIA = "TRIVIA";
  public static final String MOB_SWAP = "MOB_SWAP";
  public static final String SUPERPOWERS = "SUPERPOWERS";
  public static final String CALLBACK = "CALLBACK";

  static {
    registerBuiltInWildcards();
  }

  private static void registerBuiltInWildcards() {
    WildcardRegistry.register(SIZE_SHIFTING, SizeShifting::new);
    WildcardRegistry.register(HUNGER, Hunger::new);
    WildcardRegistry.register(SNAILS, Snails::new);
    WildcardRegistry.register(TIME_DILATION, TimeDilation::new);
    WildcardRegistry.register(TRIVIA, TriviaWildcard::new);
    WildcardRegistry.register(MOB_SWAP, MobSwap::new);
    WildcardRegistry.register(SUPERPOWERS, SuperpowersWildcard::new);
    WildcardRegistry.register(CALLBACK, Callback::new);
  }


  public static String getFromString(String wildcard) {
    if (WildcardRegistry.isRegistered(wildcard)) {
      return wildcard.toUpperCase();
    }
    return NULL;
  }

  public static List<String> getWildcards() {
    List<String> wildcards = new ArrayList<>(WildcardRegistry.getAllWildcardIds());
    wildcards.remove(NULL);
    return wildcards;
  }

  public static List<String> getWildcardsStr() {
    List<String> result = new ArrayList<>();
    for (String wildcard : getWildcards()) {
      result.add(wildcard.toLowerCase());
    }
    return result;
  }

  public static List<String> getActiveWildcards() {
    return new ArrayList<>(WildcardManager.activeWildcards.keySet());
  }

  public static List<String> getInactiveWildcards() {
    List<String> result = new ArrayList<>(getWildcards());
    result.removeAll(getActiveWildcards());
    return result;
  }

  public static List<String> getInactiveWildcardsStr() {
    List<String> result = new ArrayList<>();
    for (String wildcard : getInactiveWildcards()) {
      result.add(wildcard.toLowerCase());
    }
    return result;
  }

  public static List<String> getActiveWildcardsStr() {
    List<String> result = new ArrayList<>();
    for (String wildcard : getActiveWildcards()) {
      result.add(wildcard.toLowerCase());
    }
    return result;
  }

  public static Wildcard getInstance(String wildcardId) {
    return WildcardRegistry.createInstance(wildcardId);
  }

}
