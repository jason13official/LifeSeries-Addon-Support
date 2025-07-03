package net.mat0u5.lifeseries.seasons.season;

import java.util.List;

public enum Seasons {
    UNASSIGNED,

    THIRD_LIFE,
    LAST_LIFE,
    DOUBLE_LIFE,
    LIMITED_LIFE,
    SECRET_LIFE,
    WILD_LIFE,

    SIMPLE_LIFE;

    public static String getFormattedStringNameFromSeason(Seasons season) {
        if (season == THIRD_LIFE) return "Third Life";
        if (season == LAST_LIFE) return "Last Life";
        if (season == DOUBLE_LIFE) return "Double Life";
        if (season == LIMITED_LIFE) return "Limited Life";
        if (season == SECRET_LIFE) return "Secret Life";
        if (season == WILD_LIFE) return "Wild Life";
        if (season == SIMPLE_LIFE) return "Simple Life";
        return "unassigned";
    }

    public static String getStringNameFromSeason(Seasons season) {
        if (season == THIRD_LIFE) return "thirdlife";
        if (season == LAST_LIFE) return "lastlife";
        if (season == DOUBLE_LIFE) return "doublelife";
        if (season == LIMITED_LIFE) return "limitedlife";
        if (season == SECRET_LIFE) return "secretlife";
        if (season == WILD_LIFE) return "wildlife";
        if (season == SIMPLE_LIFE) return "simplelife";
        return "unassigned";
    }

    public static Seasons getSeasonFromStringName(String name) {
        if (name.equalsIgnoreCase("thirdlife")) return THIRD_LIFE;
        if (name.equalsIgnoreCase("lastlife")) return LAST_LIFE;
        if (name.equalsIgnoreCase("doublelife")) return DOUBLE_LIFE;
        if (name.equalsIgnoreCase("limitedlife")) return LIMITED_LIFE;
        if (name.equalsIgnoreCase("secretlife")) return SECRET_LIFE;
        if (name.equalsIgnoreCase("wildlife")) return WILD_LIFE;
        if (name.equalsIgnoreCase("simplelife")) return SIMPLE_LIFE;

        if (name.equalsIgnoreCase("Third Life")) return THIRD_LIFE;
        if (name.equalsIgnoreCase("Last Life")) return LAST_LIFE;
        if (name.equalsIgnoreCase("Double Life")) return DOUBLE_LIFE;
        if (name.equalsIgnoreCase("Limited Life")) return LIMITED_LIFE;
        if (name.equalsIgnoreCase("Secret Life")) return SECRET_LIFE;
        if (name.equalsIgnoreCase("Wild Life")) return WILD_LIFE;
        if (name.equalsIgnoreCase("Simple Life")) return SIMPLE_LIFE;
        return UNASSIGNED;
    }

    public static List<Seasons> getAllImplemented() {
        return List.of(THIRD_LIFE,LAST_LIFE,DOUBLE_LIFE,LIMITED_LIFE,SECRET_LIFE,WILD_LIFE,SIMPLE_LIFE);
    }

    public static List<String> getImplementedSeasonNames() {
        return List.of("thirdlife", "lastlife", "doublelife", "limitedlife", "secretlife", "wildlife", "simplelife");
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
