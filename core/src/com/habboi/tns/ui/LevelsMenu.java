package com.habboi.tns.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
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
public class LevelsMenu extends Menu {
  static final float MENU_HEIGHT_PERCENT = 0.33f;

  int activeWorldIndex;
  int prevActiveWorldIndex;
  float top;
  float itemHeight;
  Rect highlight;
  Rect highlightBorder;
  Text worldText;
  ShapeRenderer sr;
  SpriteBatch sb;
  MenuState menuState;
  HashMap<World, ArrayList<LevelMenuItem>> itemGroups = new HashMap<>();

  public LevelsMenu(final MenuState state, final Game game) {
    menuState = state;
    sr = game.getShapeRenderer();
    sb = game.getRenderer().getSpriteBatch();
    createItems(game);

    GameTweenManager.get().register("main_menu_select_menu", new GameTweenManager.GameTween() {
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
    });

    setHighlight();
    game.addInput(this);
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
    for (final String str : l.getLevelsNames()) {
      Level level = am.get(str);
      final String name = level.getName();
      LevelMenuItem item = new LevelMenuItem(name.toUpperCase(), width/2-cor, y-height/2, bounds);
      item.setAction(new MenuItemAction() {
        @Override
        public void doAction() {
          game.removeInput(LevelsMenu.this);
          game.setCurrentState(new InGameState(game, str));
        }
      });
      items.add(item);
      ArrayList<LevelMenuItem> itemGroup = itemGroups.get(level.getWorld());
      if (itemGroup == null) {
        itemGroup = new ArrayList<>();
        itemGroups.put(level.getWorld(), itemGroup);
      }
      itemGroup.add(item);
      y = y - (height * itemGroup.size());
      bounds.y = y - height - height/4;
    }
    activeWorldIndex = 0;
    World activeWorld = Universe.get().worlds.get(activeWorldIndex);
    worldText = new Text(FontManager.get().getFont(Game.MAIN_FONT, 36), "", null, Color.WHITE);
    worldText.setValue(activeWorld.name.toUpperCase(), true);
    worldText.getPos().set(game.getWidth()*0.05f, top + 36/2);
  }

  @Override
  public boolean onChange(int delta) {
    setHighlight();
    GameTweenManager.get().restart("main_menu_select_menu");
    GameTweenManager.get().restart("main_menu_select_menu_border");
    return true;
  }

  private void setHighlight() {
    LevelMenuItem item = (LevelMenuItem) items.get(activeIndex);
    highlight.getRectangle().setY(item.text.getPos().y - itemHeight / 4);
    highlightBorder.getRectangle().setY(item.text.getPos().y - itemHeight / 4);
  }

  @Override
  public void update(float dt) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
      if (activeWorldIndex < Universe.get().worlds.size() - 1) {
        prevActiveWorldIndex = activeWorldIndex;
        activeWorldIndex++;
      }
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
      if (activeWorldIndex > 0) {
        prevActiveWorldIndex = activeWorldIndex;
        activeWorldIndex--;
      }
    }
  }

  @Override
  public void render() {
    highlight.draw(sr, true);
    highlightBorder.draw(sr, ShapeRenderer.ShapeType.Line, true);
    sb.begin();
    worldText.draw(sb, false);
    World world = Universe.get().worlds.get(activeWorldIndex);
    ArrayList<LevelMenuItem> group = itemGroups.get(world);
    renderGroup(group);
    if (activeWorldIndex != prevActiveWorldIndex) {
      world = Universe.get().worlds.get(prevActiveWorldIndex);
      group = itemGroups.get(world);
      renderGroup(group);
    }
    sb.end();
  }

  private void renderGroup(ArrayList<LevelMenuItem> group) {
    for (int i = 0; i < group.size(); i++) {
      LevelMenuItem item = group.get(i);
      item.text.draw(sb, false);
      if (activeIndex == i) {
        item.completedText.draw(sb, false);
        item.timeText.draw(sb, false);
      }
    }
  }

  public void dispose() {
    GameTweenManager.get().remove("main_menu_select_menu");
    GameTweenManager.get().remove("main_menu_select_menu_border");
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
      completedText = new Text(FontManager.get().getFont("Neon.ttf", 24), "COMPLETED: 2", null, Color.WHITE);
      completedText.getPos().set(textX + (quarter - completedText.getBounds().x), textY);
      timeText = new Text(FontManager.get().getFont("Neon.ttf", 24), "TIME: 25.04", null, Color.WHITE);
      timeText.getPos().set(textX + (quarter - completedText.getBounds().x - timeText.getBounds().x - 20), textY);
    }
  }
}
