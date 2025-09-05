package com.cursee.ls_addon_support.gui.seasons;

import com.cursee.ls_addon_support.gui.DefaultScreen;
import com.cursee.ls_addon_support.network.NetworkHandlerClient;
import com.cursee.ls_addon_support.render.RenderUtils;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.TextColors;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ChooseSeasonScreen extends DefaultScreen {

  public static final int PADDING = 8;
  private static final int ROWS = 2;
  private static final int LOGO_TEXTURE_SIZE = 256;
  private static final float LOGO_SCALE = 0.25f;
  private static final int LOGO_SIZE = (int) (LOGO_TEXTURE_SIZE * LOGO_SCALE);
  public static boolean hasSelectedBefore = false;
  private final List<SeasonRegion> seasonRegions = new ArrayList<>();

  public ChooseSeasonScreen(boolean hasSelectedBefore) {
    super(Text.literal("Choose Season Screen"), 1f, 1.03f);
    ChooseSeasonScreen.hasSelectedBefore = hasSelectedBefore;
  }

  public static List<List<Seasons>> splitIntoRows(List<Seasons> seasons, int rows) {
    List<List<Seasons>> result = new ArrayList<>();

    int seasonsAdded = 0;
    for (int i = 0; i < rows; i++) {
      List<Seasons> row = new ArrayList<>();
      int columns = seasons.size() / rows;
        if (i < seasons.size() % rows) {
            columns++;
        }
      for (int j = 0; j < columns; j++) {
        row.add(seasons.get(j + seasonsAdded));
      }
      seasonsAdded += columns;
      result.add(row);
    }

    return result;
  }

  public static SeasonRegion getSeasonRegion(int regionIndex, Seasons season, int x, int y,
      int width, int height) {
    Rectangle rect = new Rectangle(x, y, width, height);
    return new SeasonRegion(regionIndex, rect, season);
  }

  public static void renderSeasonRegion(DrawContext context, SeasonRegion seasonRegion,
      int currentRegion, int textureSize, float scale) {
    Rectangle rect = seasonRegion.bounds;
    if (seasonRegion.id == currentRegion) {
      context.fill(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
          TextColors.LIGHT_GRAY);
    }
    RenderUtils.drawTextureScaled(context, seasonRegion.season.getLogo(), rect.x, rect.y, 0, 0,
        textureSize, textureSize, scale, scale);
  }

  @Override
  public void init() {
    super.init();
    addSeasonRegions();
  }

  public void addSeasonRegions() {
    seasonRegions.clear();
    List<Seasons> seasons = Seasons.getSeasons();
    seasons.removeAll(Seasons.getAprilFoolsSeasons());

    List<List<Seasons>> rows = splitIntoRows(seasons, ROWS);
    int currentRegionIndex = 1;
    int currentY = startY + 32;
    for (List<Seasons> row : rows) {
      int columns = row.size();
      int currentX = startX + (BG_WIDTH - (LOGO_SIZE * columns + PADDING * (columns - 1))) / 2;
      for (Seasons season : row) {
        seasonRegions.add(
            getSeasonRegion(currentRegionIndex, season, currentX, currentY, LOGO_SIZE, LOGO_SIZE));
        currentRegionIndex++;
        currentX += LOGO_SIZE + PADDING;
      }
      currentY += LOGO_SIZE;// Don't add padding (the logos are usually wider than taller anyways)
    }
  }

  public int getRegion(int x, int y) {
    for (SeasonRegion region : seasonRegions) {
      if (x >= region.bounds().x && x <= region.bounds().x + region.bounds().width &&
          y >= region.bounds().y && y <= region.bounds().y + region.bounds().height) {
        return region.id();
      }
    }

    Text aprilFools = Text.of("April Fools Seasons");
    int textWidth = textRenderer.getWidth(aprilFools);
    int textHeight = textRenderer.fontHeight;
    Rectangle rect = new Rectangle(endX - 9 - textWidth, endY - 9 - textHeight, textWidth + 1,
        textHeight + 1);

    if (x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height) {
      return -1;
    }

    return 0;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button == 0) { // Left-click
      int region = getRegion((int) mouseX, (int) mouseY);
      if (region == -1 && this.client != null) {
        this.client.setScreen(new ChooseExtraSeasonScreen(hasSelectedBefore));
        return true;
      } else if (region != 0) {
        handleSeasonRegionClick(region);
        return true;
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  public void handleSeasonRegionClick(int region) {
    for (SeasonRegion seasonRegion : seasonRegions) {
      if (seasonRegion.id() == region) {
        if (hasSelectedBefore && this.client != null) {
          this.client.setScreen(new ConfirmSeasonAnswerScreen(this, seasonRegion.season()));
        } else {
          NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON,
              seasonRegion.season().getName());
          this.close();
        }
      }
    }
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY) {
    int currentRegion = getRegion(mouseX, mouseY);

    // Background + images
    for (SeasonRegion seasonRegion : seasonRegions) {
      renderSeasonRegion(context, seasonRegion, currentRegion, LOGO_TEXTURE_SIZE, LOGO_SCALE);
    }

    String prompt = "Select the season you want to play.";
    RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(prompt), centerX, startY + 20);

    Text aprilFools = Text.of("April Fools Seasons");
    int textWidth = textRenderer.getWidth(aprilFools);
    int textHeight = textRenderer.fontHeight;

    Rectangle rect = new Rectangle(endX - 9 - textWidth, endY - 9 - textHeight, textWidth + 1,
        textHeight + 1);

    context.fill(rect.x - 1, rect.y - 1, rect.x + rect.width + 1, rect.y,
        DEFAULT_TEXT_COLOR); // top border
    context.fill(rect.x - 1, rect.y + rect.height, rect.x + rect.width + 2,
        rect.y + rect.height + 2, DEFAULT_TEXT_COLOR); // bottom
    context.fill(rect.x - 1, rect.y, rect.x, rect.y + rect.height, DEFAULT_TEXT_COLOR); // left
    context.fill(rect.x + rect.width, rect.y - 1, rect.x + rect.width + 2, rect.y + rect.height,
        DEFAULT_TEXT_COLOR); // right

    if (currentRegion == -1) {
      RenderUtils.drawTextLeft(context, this.textRenderer, TextColors.PURE_WHITE, aprilFools,
          rect.x + 1, rect.y + 1);
    } else {
      RenderUtils.drawTextLeft(context, this.textRenderer, DEFAULT_TEXT_COLOR, aprilFools,
          rect.x + 1, rect.y + 1);
    }
  }

  public record SeasonRegion(int id, Rectangle bounds, Seasons season) {

  }
}

