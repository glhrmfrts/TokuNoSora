package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;
import com.habboi.tns.level.worlds.Universe;
import com.habboi.tns.level.worlds.World;
import com.habboi.tns.states.InGameState;
import com.habboi.tns.states.MenuState;
import com.habboi.tns.utils.FontManager;

import java.util.ArrayList;
import java.util.HashMap;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

/**
 * Created by w7 on 02/07/2016.
 */
public class LevelsMenu extends BaseMenu implements Disposable {
  static final float MENU_HEIGHT_PERCENT = 0.33f;
  static final int FONT_SIZE = Game.MAIN_FONT_SIZE;
  static final int WORLD_FONT_SIZE = Game.MEDIUM_FONT_SIZE;

  Rect container;
  Rect background;
  Text worldText;
  int activeItemIndex;
  int activeWorldIndex;
  int prevActiveWorldIndex;
  HashMap<World, ArrayList<LevelMenuItem>> itemGroups = new HashMap<>();

  public LevelsMenu(final MenuState state, final Game game) {
    menuState = state;
    sr = game.getShapeRenderer();
    sb = game.getRenderer().getSpriteBatch();
    background = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
    background.getColor().a = 0;
    final GameTweenManager gtm = GameTweenManager.get();
    gtm.register("levels_menu_select_menu", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        highlight.getRectangle().height = 0;
        return Tween.to(highlight, Rect.Accessor.TWEEN_HEIGHT, 0.25f)
                .target(itemBounds.height)
                .ease(TweenEquations.easeOutQuad);
      }

      @Override
      public void onComplete() {

      }
    }).register("levels_menu_select_menu_border", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        highlightBorder.getRectangle().height = 0;
        return Tween.to(highlightBorder, Rect.Accessor.TWEEN_HEIGHT, 0.25f)
                .target(itemBounds.height)
                .ease(TweenEquations.easeOutQuad);
      }

      @Override
      public void onComplete() {

      }
    }).register("change_world_in", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        return background.getFadeTween(0, 1, 0.15f);
      }

      @Override
      public void onComplete() {
        World world = Universe.get().worlds.get(activeWorldIndex);
        menuState.setWorld(world);
        gtm.start("change_world_out");
      }
    }).register("change_world_out", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        return background.getFadeTween(1, 0, 0.15f);
      }

      @Override
      public void onComplete() {

      }
    });
    createItems(game);
    setHighlight();
  }

  private void createItems(final Game game) {
    AssetManager am = game.getAssetManager();
    LevelLoader l = (LevelLoader) am.getLoader(Level.class);
    l.sortLevelsNames(am);
    setSize(game.getWidth() * 1.1f, FONT_SIZE * 4 + 20 * 4);
    setItemSize(game.getWidth()*1.1f, FONT_SIZE + 20);
    setCenter(game.getWidth() / 2, game.getHeight() / 2);
    createHighlight();
    float y = bounds.y + bounds.height/2 - itemBounds.height/2;
    float x = bounds.x;
    Rectangle bnds = new Rectangle(x, y, itemBounds.width, itemBounds.height);
    World lastWorld = null;
    for (final String str : l.getLevelsNames()) {
      Level level = am.get(str);
      World world = level.getWorld();
      if (world != lastWorld) {
        y = bnds.y = bounds.y + bounds.height/2 - itemBounds.height/2;
        lastWorld = world;
      }
      LevelMenuItem item = new LevelMenuItem(level.getName(), x, y, bnds);
      item.setAction(new MenuItemAction() {
        @Override
        public void doAction() {
          game.addState(new InGameState(game, str));
        }
      });
      ArrayList<LevelMenuItem> itemGroup = itemGroups.get(world);
      if (itemGroup == null) {
        itemGroup = new ArrayList<>();
        itemGroups.put(world, itemGroup);
      }
      itemGroup.add(item);
      y -= (bnds.height * itemGroup.size());
      bnds.y -= (bnds.height * itemGroup.size());
    }
    activeWorldIndex = 0;
    World activeWorld = Universe.get().worlds.get(activeWorldIndex);
    worldText = new Text(FontManager.get().getFont(Game.MAIN_FONT, WORLD_FONT_SIZE), "", null, Color.WHITE);
    worldText.setValue(activeWorld.name, true);
    float worldPadding = 20 * ((float) WORLD_FONT_SIZE / (float) FONT_SIZE);
    worldText.getPos().set(game.getWidth() * 0.05f, bounds.y + bounds.height / 2 + (WORLD_FONT_SIZE / 2) + worldPadding);
    container = new Rect(new Rectangle(bounds.x, bounds.y + worldText.getBounds().y/2, bounds.width, bounds.height+worldText.getBounds().y + worldPadding * 2));
    container.getColor().a = 0.5f;
  }

  @Override
  public boolean onChange(int delta) {
    setHighlight();
    GameTweenManager.get().restart("levels_menu_select_menu");
    GameTweenManager.get().restart("levels_menu_select_menu_border");
    return true;
  }

  private void changeGroup() {
    World world = Universe.get().worlds.get(activeWorldIndex);
    worldText.setValue(world.name, true);
    GameTweenManager.get().start("change_world_in");
  }

  @Override
  public void setHighlight() {
    World world = Universe.get().worlds.get(activeWorldIndex);
    ArrayList<LevelMenuItem> items = itemGroups.get(world);
    MenuItem item = items.get(activeItemIndex);
    highlight.getRectangle().setY(item.bounds.y - item.bounds.height/4);
    highlightBorder.getRectangle().setY(item.bounds.y - item.bounds.height/4);
  }

  @Override
  public void render() {
    background.draw(sr, false);
    container.draw(sr, true);
    highlight.draw(sr, true);
    highlightBorder.draw(sr, ShapeRenderer.ShapeType.Line, true);
    sb.begin();
    worldText.draw(sb, false);
    World world = Universe.get().worlds.get(activeWorldIndex);
    ArrayList<LevelMenuItem> group = itemGroups.get(world);
    renderGroup(group);
    sb.end();
  }

  private void renderGroup(ArrayList<LevelMenuItem> group) {
    for (int i = 0; i < group.size(); i++) {
      LevelMenuItem item = group.get(i);
      item.text.draw(sb, false);
      if (activeItemIndex == i) {
        item.completedText.draw(sb, false);
        item.timeText.draw(sb, false);
      }
    }
  }

  public void dispose() {
    GameTweenManager.get().remove("levels_menu_select_menu");
    GameTweenManager.get().remove("levels_menu_select_menu_border");
    GameTweenManager.get().remove("change_world_in");
    GameTweenManager.get().remove("change_world_out");
  }

  static class LevelMenuItem extends MenuItem {
    Text text;
    Text completedText;
    Text timeText;

    public LevelMenuItem(String textValue, float textX, float textY, Rectangle bounds) {
      super(bounds);
      float quarter = bounds.width * .40f;
      text = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), textValue, null, Color.WHITE);
      text.getPos().set(textX - quarter, textY);
      completedText = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), "completed: 2", null, Color.WHITE);
      completedText.getPos().set(textX + (quarter - completedText.getBounds().x), textY);
      timeText = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), "time: 25.04", null, Color.WHITE);
      timeText.getPos().set(textX + (quarter - completedText.getBounds().x - timeText.getBounds().x - 20), textY);
    }
  }

  @Override
  public boolean keyDown(int keycode) {
    World world = Universe.get().worlds.get(activeWorldIndex);
    ArrayList<LevelMenuItem> currentGroup = itemGroups.get(world);
    boolean ret = false;
    if (keycode == Input.Keys.RIGHT) {
      if (activeWorldIndex < Universe.get().worlds.size() - 1) {
        prevActiveWorldIndex = activeWorldIndex;
        activeWorldIndex++;
        changeGroup();
        activeItemIndex = 0;
        ret = onChange(0);
      }
    } else if (keycode == Input.Keys.LEFT) {
      if (activeWorldIndex > 0) {
        prevActiveWorldIndex = activeWorldIndex;
        activeWorldIndex--;
        changeGroup();
        activeItemIndex = 0;
        ret = onChange(0);
      }
    } else if (keycode == Input.Keys.UP) {
      if (activeItemIndex > 0) {
        activeItemIndex--;
        ret = onChange(-1);
      } else if (activeWorldIndex > 0) {
        activeWorldIndex--;
        activeItemIndex = currentGroup.size() - 1;
        changeGroup();
        ret = onChange(0);
      }
    } else if (keycode == Input.Keys.DOWN) {
      if (activeItemIndex < currentGroup.size() - 1) {
        activeItemIndex++;
        ret = onChange(1);
      } else if (activeWorldIndex < Universe.get().worlds.size() - 1) {
        activeWorldIndex++;
        activeItemIndex = 0;
        changeGroup();
        ret = onChange(0);
      }
    } else if (keycode == Input.Keys.ENTER) {
      currentGroup.get(activeItemIndex).action.doAction();
    } else if (keycode == Input.Keys.ESCAPE) {
      menuState.popMenu();
    }
    return ret;
  }
}
