package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlayerListHud.class, priority = 1)
public class PlayerListHudMixin {

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ReadableScoreboardScore;getFormattedScore(Lnet/minecraft/scoreboard/ReadableScoreboardScore;Lnet/minecraft/scoreboard/number/NumberFormat;)Lnet/minecraft/text/MutableText;"))
    private MutableText modifyFormattedScore(ReadableScoreboardScore readableScoreboardScore, NumberFormat numberFormat) {
        ScoreboardObjective objective = getDisplayedObjective();
        MutableText originalText = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, numberFormat);
        if (readableScoreboardScore == null || originalText == null) return originalText;

        if (objective != null && objective.getName().equals("Lives")) {
            int score = readableScoreboardScore.getScore();
            if (MainClient.clientCurrentSeason != Seasons.LIMITED_LIFE) {
                if (score >= 4) {
                    return Text.literal("4+").setStyle(originalText.getStyle());
                }
            }
            else {
                return Text.literal(OtherUtils.formatTime(score*20)).setStyle(originalText.getStyle());
            }
        }

        return originalText;
    }


    private ScoreboardObjective getDisplayedObjective() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return null;

        Scoreboard scoreboard = client.world.getScoreboard();
        return scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
    }

}
