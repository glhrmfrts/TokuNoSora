package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
public class LevelsMenu implements Menu, Disposable {
  static final float MENU_HEIGHT_PERCENT = 0.33f;

  int activeItemIndex;
  int activeWorldIndex;
  int prevActiveWorldIndex;
  float top;
  float itemHeight;
  Rect highlight;
  Rect highlightBorder;
  Rect background;
  Text worldText;
  ShapeRenderer sr;
  SpriteBatch sb;
  MenuState menuState;
  HashMap<World, ArrayList<LevelMenuItem>> itemGroups = new HashMap<>();

  public LevelsMenu(final MenuState state, final Game game) {
    menuState = state;
    sr = game.getShapeRenderer();
    sb = game.getRenderer().getSpriteBatch();
    background = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
    background.getColor().a = 0;
    final GameTweenManager gtm = GameTweenManager.get();
    gtm.register("main_menu_select_menu", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        highlight.getRectangle().height = 0;
        return Tween.to(highlight, Rect.Accessor.TWEEN_HEIGHT, 0.25f)
                .target(itemHeight)
                .ease(TweenEquations.easeOutQuad);
      }

      @Override
      public void onComplete() {

      }
    }).register("main_menu_select_menu_border", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        highlightBorder.getRectangle().height = 0;
        return Tween.to(highlightBorder, Rect.Accessor.TWEEN_HEIGHT, 0.25f)
                .target(itemHeight)
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
    float y = top = game.getHeight()/2 + (game.getHeight() * MENU_HEIGHT_PERCENT)/2;
    float width = game.getWidth()*1.1f;
    float height = itemHeight = (game.getHeight() * MENU_HEIGHT_PERCENT) / 4;
    float cor = (width - game.getWidth())/2;
    Rectangle bounds = new Rectangle(width/2-cor, 0, width, height);
    highlight = new Rect(bounds);
    highlight.getColor().set(0, 0, 0, 0.75f);
    highlightBorder = new Rect(bounds, Color.WHITE);
    bounds.x -= width/2;
    bounds.y = top - height - height/4;
    World lastWorld = null;
    for (final String str : l.getLevelsNames()) {
      Level level = am.get(str);
      World world = level.getWorld();
      if (world != lastWorld) {
        y = top;
        lastWorld = world;
      }
      LevelMenuItem item = new LevelMenuItem(level.getName(), width/2-cor, y-height/2, bounds);
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
      y = top - (height * itemGroup.size());
      bounds.y = y - height - height/4;
    }
    activeWorldIndex = 0;
    World activeWorld = Universe.get().worlds.get(activeWorldIndex);
    worldText = new Text(FontManager.get().getFont(Game.MAIN_FONT, 36), "", null, Color.WHITE);
    worldText.setValue(activeWorld.name, true);
    worldText.getPos().set(game.getWidth()*0.05f, top + 36/1.5f);
  }

  @Override
  public boolean onChange(int delta) {
    setHighlight();
    GameTweenManager.get().restart("main_menu_select_menu");
    GameTweenManager.get().restart("main_menu_select_menu_border");
    return true;
  }

  private void setHighlight() {
    World world = Universe.get().worlds.get(activeWorldIndex);
    LevelMenuItem item = itemGroups.get(world).get(activeItemIndex);
    highlight.getRectangle().setY(item.text.getPos().y - itemHeight / 4);
    highlightBorder.getRectangle().setY(item.text.getPos().y - itemHeight / 4);
  }

  private void changeGroup() {
    World world = Universe.get().worlds.get(activeWorldIndex);
    worldText.setValue(world.name, true);
    GameTweenManager.get().start("change_world_in");
  }

  @Override
  public void render() {
    background.draw(sr, false);
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
    GameTweenManager.get().remove("main_menu_select_menu");
    GameTweenManager.get().remove("main_menu_select_menu_border");
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
      text = new Text(FontManager.get().getFont("Neon.ttf", 24), textValue, null, Color.WHITE);
      text.getPos().set(textX - quarter, textY);
      completedText = new Text(FontManager.get().getFont("Neon.ttf", 24), "completed: 2", null, Color.WHITE);
      completedText.getPos().set(textX + (quarter - completedText.getBounds().x), textY);
      timeText = new Text(FontManager.get().getFont("Neon.ttf", 24), "time: 25.04", null, Color.WHITE);
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
