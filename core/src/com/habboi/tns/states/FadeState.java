package com.habboi.tns.states;

import com.badlogic.gdx.math.Rectangle;
import com.habboi.tns.Game;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.Rect;

import aurelienribon.tweenengine.Tween;

/**
 * A generic state with fade in and out transitions.
 */
public class FadeState extends GameState {
  GameTweenManager gtm;
  Rect screenRect;

  public FadeState(Game g) {
    super(g);
    gtm = GameTweenManager.get();
  }

  public void fadeIn() {
    gtm.start("fadeIn");
  }

  public void fadeOut() {
    gtm.start("fadeOut");
  }

  public boolean isFadingIn() {
    return gtm.isActive("fadeIn");
  }

  public boolean isFadingOut() {
    return gtm.isActive("fadeOut");
  }

  @Override
  public void create() {
    screenRect = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
    gtm.register("fadeIn", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        return screenRect.getFadeTween(1, 0, 2);
      }

      @Override
      public void onComplete() {

      }
    }).register("fadeOut", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        return screenRect.getFadeTween(0, 1, 2);
      }

      @Override
      public void onComplete() {

      }
    });
  }

  @Override
  public void update(float dt) {

  }

  @Override
  public void render() {
    screenRect.draw(game.getShapeRenderer());
  }

  @Override
  public void dispose() {

  }
}
