package com.habboi.tns;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;
import com.habboi.tns.level.Models;
import com.habboi.tns.states.*;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.utils.FontFileHandleResolver;
import com.habboi.tns.utils.FontLoader;
import com.habboi.tns.utils.FontManager;

public class Game extends ApplicationAdapter {
  static final float STEP_SECONDS = 0.016f;
  static final float STEP	  = STEP_SECONDS * 1000f;

  long accumUpdateTime;
  long lastUpdateTime = -1;

  InputMultiplexer inputMul;
  GameRenderer renderer;
  ShapeRenderer sr;
  GameState currentState;
  boolean stateChanged;

  AssetManager am;
  Application.ApplicationType appType;
  int width;
  int height;
	
  @Override
  public void create () {
    appType = Gdx.app.getType();
    width = Gdx.graphics.getWidth();
    height = Gdx.graphics.getHeight();

    inputMul = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMul);
    
    renderer = new GameRenderer(this);
    sr = new ShapeRenderer();
    am = new AssetManager();
    am.setLoader(Level.class, new LevelLoader(new InternalFileHandleResolver()));
    am.setLoader(BitmapFont.class, new FontLoader(new FontFileHandleResolver()));
    FontManager.get().setAssetManager(am);
    
    setCurrentState(new LoadingState(this));
  }

  public float getDensity() {
    if (isAndroid()) {
      return Gdx.graphics.getDensity();
    }
    return 1;
  }

  public boolean isAndroid() {
    return appType == Application.ApplicationType.Android;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public GameRenderer getRenderer() {
    return renderer;
  }

  public ShapeRenderer getShapeRenderer() {
    return sr;
  }

  public AssetManager getAssetManager() {
    return am;
  }

  public void setCurrentState(GameState state) {
    if (currentState != null) {
      currentState.dispose();
      stateChanged = true;
    }
    currentState = state;
    currentState.create();
  }

  public void addInput(InputProcessor input) {
    inputMul.addProcessor(input);
  }

  public void update() {
    currentState.update(STEP_SECONDS);
    GameTweenManager.get().update(STEP_SECONDS);
  }

  @Override
  public void render () {
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
      Gdx.app.exit();
      return;
    }

    long start = TimeUtils.millis();
    if (lastUpdateTime == -1) {
      lastUpdateTime = start;
      return;
    }

    float delta = (start - lastUpdateTime);
    lastUpdateTime = start;

    accumUpdateTime += delta;
    while (accumUpdateTime >= STEP) {
      accumUpdateTime -= delta;
      update();
      if (stateChanged) {
        update();
        stateChanged = false;
      }
    }

    currentState.render();
  }

  @Override
  public void dispose() {
    currentState.dispose();
    renderer.dispose();
    Models.dispose();
  }
}
