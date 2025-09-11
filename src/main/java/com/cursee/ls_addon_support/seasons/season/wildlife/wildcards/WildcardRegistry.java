package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WildcardRegistry {
    
    private static final Map<String, WildcardRegistration> wildcards = new HashMap<>();
    
    public static void register(String id, Supplier<Wildcard> factory) {
        wildcards.put(id.toUpperCase(), new WildcardRegistration(id, factory));
    }
    
    public static Wildcard createInstance(String id) {
        WildcardRegistration registration = wildcards.get(id.toUpperCase());
        if (registration != null) {
            return registration.factory().get();
        }
        return null;
    }
    
    public static boolean isRegistered(String id) {
        return wildcards.containsKey(id.toUpperCase());
    }
    
    public static List<String> getAllWildcardIds() {
        return new ArrayList<>(wildcards.keySet());
    }
    
    public static List<String> getAllWildcardNames() {
        List<String> result = new ArrayList<>();
        for (WildcardRegistration registration : wildcards.values()) {
            result.add(registration.id().toLowerCase());
        }
        return result;
    }
    
    private record WildcardRegistration(String id, Supplier<Wildcard> factory) {}
}