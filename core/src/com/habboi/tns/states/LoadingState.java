package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.utils.FontManager;

/**
 * The state for the loading screen.
 */
public class LoadingState extends GameState {
  Rect loadingBar;
  Rect border;
  float percent;
  float loadingBarTotalWidth;

  public LoadingState(Game g) {
    super(g);
  }

  @Override
  public void create() {
    Gdx.gl.glClearColor(0, 0, 0, 0);

    final float padding = game.getWidth() * 0.01f;
    float w = game.getWidth() * 0.5f;
    float h = game.getHeight() * 0.05f;
    float x = game.getWidth()/2 - w/2;
    float y = game.getHeight()/2 - h/2;
    loadingBarTotalWidth = w;

    loadingBar = new Rect(new Rectangle(x, y, 0, h), Color.WHITE);
    border = new Rect(new Rectangle(x - padding, y - padding, w + padding*2, h + padding*2), Color.WHITE);

    AssetManager am = game.getAssetManager();
    FontManager fm = FontManager.get();
    am.load("audio/bounce.wav", Sound.class);
    am.load("audio/explosion.wav", Sound.class);
    am.load("audio/select.wav", Sound.class);
    fm.loadFont("Neon.ttf", (int) (64 * game.getDensity()));
    fm.loadFont("Neon.ttf", (int) (24 * game.getDensity()));
    for (int i = 1; i < 7; i++) {
      am.load("map" + i + ".json", Level.class);
    }
  }

  @Override
  public void update(float dt) {

  }

  @Override
  public void render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    ShapeRenderer sr = game.getShapeRenderer();
    AssetManager am = game.getAssetManager();

    if (am.update()) {
      game.setCurrentState(new MenuState(game));
      //game.setCurrentState(new InGameState(game));
    }

    percent = Interpolation.linear.apply(percent, am.getProgress(), 0.1f);

    float w = loadingBarTotalWidth * percent;
    loadingBar.getRectangle().setWidth(w);
    loadingBar.draw(sr, ShapeRenderer.ShapeType.Filled, false);
    border.draw(sr, ShapeRenderer.ShapeType.Line, false);
  }

  @Override
  public void dispose() {

  }
}
