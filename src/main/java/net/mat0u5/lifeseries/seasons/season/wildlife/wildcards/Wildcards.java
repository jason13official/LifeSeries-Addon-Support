package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;

import java.util.ArrayList;
import java.util.List;

public enum Wildcards {
    NULL,
    SIZE_SHIFTING,
    HUNGER,
    SNAILS,
    TIME_DILATION,
    TRIVIA,
    MOB_SWAP,
    SUPERPOWERS,
    CALLBACK;

    public static Wildcards getFromString(String wildcard) {
        try {
            return Enum.valueOf(Wildcards.class, wildcard.toUpperCase());
        } catch(Exception e) {}
        return Wildcards.NULL;
    }

    public static Wildcard getInstance(Wildcards wildcard) {
        if (wildcard == Wildcards.SIZE_SHIFTING) return new SizeShifting();
        if (wildcard == Wildcards.HUNGER) return new Hunger();
        if (wildcard == Wildcards.SNAILS) return new Snails();
        if (wildcard == Wildcards.TIME_DILATION) return new TimeDilation();
        if (wildcard == Wildcards.TRIVIA) return new TriviaWildcard();
        if (wildcard == Wildcards.MOB_SWAP) return new MobSwap();
        if (wildcard == Wildcards.SUPERPOWERS) return new SuperpowersWildcard();
        if (wildcard == Wildcards.CALLBACK) return new Callback();
        return null;
    }

    public static List<Wildcards> getWildcards() {
        return List.of(
            SIZE_SHIFTING, HUNGER, SNAILS, TIME_DILATION, TRIVIA, MOB_SWAP, SUPERPOWERS, CALLBACK
        );
    }

    public static List<String> getWildcardsStr() {
        List<String> result = new ArrayList<>();
        for (Wildcards wildcard : getWildcards()) {
            String name = getStringName(wildcard);
            result.add(name);
        }
        return result;
    }

    public static String getStringName(Wildcards wildcard) {
        return wildcard.toString().toLowerCase();
    }

    public static List<Wildcards> getActiveWildcards() {
        return new ArrayList<>(WildcardManager.activeWildcards.keySet());
    }

    public static List<Wildcards> getInactiveWildcards() {
        List<Wildcards> result = new ArrayList<>(getWildcards());
        result.removeAll(getActiveWildcards());
        return result;
    }

    public static List<String> getInactiveWildcardsStr() {
        List<String> result = new ArrayList<>();
        for (Wildcards wildcard : getInactiveWildcards()) {
            String name = getStringName(wildcard);
            result.add(name);
        }
        return result;
    }

    public static List<String> getActiveWildcardsStr() {
        List<String> result = new ArrayList<>();
        for (Wildcards wildcard : getActiveWildcards()) {
            String name = getStringName(wildcard);
            result.add(name);
        }
        return result;
    }

}
