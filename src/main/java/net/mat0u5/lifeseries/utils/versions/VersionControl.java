package net.mat0u5.lifeseries.utils.versions;

import net.mat0u5.lifeseries.Main;

import static net.mat0u5.lifeseries.Main.MOD_VERSION;

public class VersionControl {
    public static boolean isDevVersion() {
        return MOD_VERSION.contains("dev") || Main.DEBUG;
    }


    public static int getModVersionInt(String string) {
        if (string.startsWith("v.")) {
            string = string.substring(2);
        }
        string = string.replaceAll("^\\D+", "");

        String[] parts = string.split("\\.");

        int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        int build = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;

        return (major * 100000) + (minor * 10000) + (patch * 1000) + build;
    }

    /*
        *     COMPATIBILITY TABLE
        *   1.3.0
        *   1.3.1       -   1.3.1.2
        *   1.3.1.3     -   1.3.1.4
        *   1.3.2
        *   1.3.2.1     -   1.3.2.2
        *   1.3.2.3
        *   1.3.2.4
        *   1.3.2.5
        *   1.3.2.6
        *   1.3.3       -   1.3.3.2
        *   1.3.4       -   1.3.4.4
        *   1.3.4.5     -   1.3.4.9
        *   1.3.4.10    -   1.3.4.19
        *   1.3.5       -   1.3.5.2
        *   1.3.5.3     -   1.3.5.7
        *   1.3.5.8     -   1.3.5.16
        *   1.3.5.17    -   1.3.5.23
        *   1.3.5.24    -   *
     */

    public static String clientCmpatibilityMin() {
        //This is the version that the SERVER needs to have for the current client.
        return "dev-1.3.5.24";
    }

    public static String serverCompatibilityMin() {
        //This is the version that the CLIENT needs to have for the current server.
        return "dev-1.3.5.24";
    }
}
