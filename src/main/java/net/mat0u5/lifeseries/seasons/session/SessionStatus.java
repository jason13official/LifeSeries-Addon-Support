package net.mat0u5.lifeseries.seasons.session;

public enum SessionStatus {
    NOT_STARTED,
    STARTED,
    PAUSED,
    FINISHED,
    UNASSIGNED;

    public static String getStringName(SessionStatus status) {
        if (status == NOT_STARTED) return "Not Started";
        if (status == STARTED) return "Started";
        if (status == PAUSED) return "Paused";
        if (status == FINISHED) return "Finished";
        return "Unassigned";
    }

    public static SessionStatus getSessionName(String name) {
        if (name.equalsIgnoreCase("Not Started")) return NOT_STARTED;
        if (name.equalsIgnoreCase("Started")) return STARTED;
        if (name.equalsIgnoreCase("Paused")) return PAUSED;
        if (name.equalsIgnoreCase("Finished")) return FINISHED;
        return UNASSIGNED;
    }
}
