package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.states.MenuState;
import com.habboi.tns.utils.FontManager;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

/**
 * The main menu.
 */
public class MainMenu extends BaseMenu implements Disposable {
  static final float MENU_HEIGHT_PERCENT = 0.33f;

  float top;
  float itemHeight;
  Rect highlight;
  Rect highlightBorder;
  ShapeRenderer sr;
  SpriteBatch sb;
  MenuState menuState;

  public MainMenu(final MenuState state, final Game game) {
    menuState = state;
    sr = game.getShapeRenderer();
    sb = game.getRenderer().getSpriteBatch();

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
    items.add(new MainMenuItem("play", width/2-cor, y-height/2, bounds).setAction(new MenuItemAction() {
      @Override
      public void doAction() {
        state.addMenu(new LevelsMenu(state, game));
      }
    }));
    y -= height;
    bounds.y = y - height - height/4;
    items.add(new MainMenuItem("options", width/2-cor, y-height/2, bounds));
    y -= height;
    bounds.y = y - height - height/4;
    items.add(new MainMenuItem("credits", width/2-cor, y-height/2, bounds));
    y -= height;
    bounds.y = y - height - height/4;
    items.add(new MainMenuItem("quit", width/2-cor, y-height/2, bounds).setAction(new MenuItemAction() {
      @Override
      public void doAction() {
        game.exit();
      }
    }));

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
  }

  @Override
  public boolean onChange(int delta) {
    setHighlight();
    GameTweenManager.get().restart("main_menu_select_menu");
    GameTweenManager.get().restart("main_menu_select_menu_border");
    return true;
  }

  private void setHighlight() {
    MainMenuItem item = (MainMenuItem) items.get(activeIndex);
    highlight.getRectangle().setY(item.text.getPos().y - itemHeight / 4);
    highlightBorder.getRectangle().setY(item.text.getPos().y - itemHeight / 4);
  }

  @Override
  public void update(float dt) {

  }

  @Override
  public void render() {
    highlight.draw(sr, true);
    highlightBorder.draw(sr, ShapeRenderer.ShapeType.Line, true);

    sb.begin();
    for (int i = 0; i < items.size; i++) {
      Text text = ((MainMenuItem) items.get(i)).text;
      text.draw(sb, true);
    }
    sb.end();
  }

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.ESCAPE) {
      menuState.getGame().exit();
    }
    return super.keyDown(keycode);
  }

  public void dispose() {
    GameTweenManager.get().remove("main_menu_select_menu");
    GameTweenManager.get().remove("main_menu_select_menu_border");
  }

  static class MainMenuItem extends MenuItem {
    Text text;

    public MainMenuItem(String textValue, float textX, float textY, Rectangle bounds) {
      super(bounds);
      text = new Text(FontManager.get().getFont("Neon.ttf", 36), textValue, null, Color.WHITE);
      text.getPos().set(textX, textY);
    }
  }
}
