package net.mat0u5.lifeseries.seasons.season;

import java.util.ArrayList;
import java.util.List;

public enum Seasons {
    UNASSIGNED("Unassigned", "unassigned"),

    THIRD_LIFE("Third Life", "thirdlife"),
    LAST_LIFE("Last Life", "lastlife"),
    DOUBLE_LIFE("Double Life", "doublelife"),
    LIMITED_LIFE("Limited Life", "limitedlife"),
    SECRET_LIFE("Secret Life", "secretlife"),
    WILD_LIFE("Wild Life", "wildlife"),

    SIMPLE_LIFE("Simple Life", "simplelife"),
    REAL_LIFE("Real Life", "reallife");

    private String name;
    private String id;

    Seasons(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public static Seasons getSeasonFromStringName(String name) {
        for (Seasons season : Seasons.values()) {
            if (season.getName().equalsIgnoreCase(name) || season.getId().equalsIgnoreCase(name)) {
                return season;
            }
        }
        return UNASSIGNED;
    }

    public static List<Seasons> getSeasons() {
        List<Seasons> allSeasons = new ArrayList<>(List.of(Seasons.values()));
        allSeasons.remove(UNASSIGNED);
        return allSeasons;
    }

    public static List<String> getSeasonIds() {
        List<String> seasonNames = new ArrayList<>();
        for (Seasons season : getSeasons()) {
            seasonNames.add(season.getId());
        }
        return seasonNames;
    }

    public static String getDatapackName(Seasons season) {
        if (season == THIRD_LIFE) return "Third Life Recipe Datapack.zip";
        if (season == LAST_LIFE) return "Last Life Recipe Datapack.zip";
        if (season == DOUBLE_LIFE) return "Double Life Recipe Datapack.zip";
        if (season == LIMITED_LIFE) return "Limited Life Recipe Datapack.zip";
        if (season == SECRET_LIFE) return "Secret Life Recipe Datapack.zip";
        if (season == WILD_LIFE) return "Wild Life Recipe Datapack.zip";
        return null;
    }
}
