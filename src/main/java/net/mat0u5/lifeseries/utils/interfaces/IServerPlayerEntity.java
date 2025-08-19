package net.mat0u5.lifeseries.utils.interfaces;

import org.jetbrains.annotations.Nullable;

public interface IServerPlayerEntity {
    @Nullable Integer ls$getLives();
    boolean ls$hasAssignedLives();
    boolean ls$isAlive();
    void ls$addToLives(int amount);
    void ls$setLives(int lives);
    boolean ls$isOnLastLife(boolean fallback);
    boolean ls$isOnSpecificLives(int check, boolean fallback);
    boolean ls$isOnAtLeastLives(int check, boolean fallback);
}
