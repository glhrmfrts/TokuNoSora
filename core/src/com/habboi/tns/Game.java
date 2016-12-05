package com.habboi.tns;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;
import com.habboi.tns.level.Models;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.states.*;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.utils.FontFileHandleResolver;
import com.habboi.tns.utils.FontLoader;
import com.habboi.tns.utils.FontManager;

import java.util.Stack;

import aurelienribon.tweenengine.Tween;

public class Game extends ApplicationAdapter {
  public static final String MAIN_FONT = "Neon.ttf";
  public static final int HUGE_FONT_SIZE = 64;
  public static final int BIG_FONT_SIZE = 52;
  public static final int MEDIUM_FONT_SIZE = 40;
  public static final int MAIN_FONT_SIZE = 28;
  public static final int SMALL_FONT_SIZE = 16;

  static final float STEP_SECONDS = 0.016f;
  static final float STEP	  = STEP_SECONDS * 1000f;

  long accumUpdateTime;
  long lastUpdateTime = -1;

  InputMultiplexer inputMul;
  GameRenderer renderer;
  ShapeRenderer sr;
  Rect exitingRect;
  GameState currentState;
  Stack<GameState> stateStack;
  boolean stateChanged;
  boolean exiting;

  AssetManager am;
  Application.ApplicationType appType;
  int width;
  int height;

  @Override
  public void create () {
    appType = Gdx.app.getType();
    width = Gdx.graphics.getWidth();
    height = Gdx.graphics.getHeight();
    exitingRect = new Rect(new Rectangle(0, 0, width, height));

    inputMul = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMul);

    renderer = new GameRenderer(this);
    sr = new ShapeRenderer();
    am = new AssetManager();
    am.setLoader(Level.class, new LevelLoader(new InternalFileHandleResolver()));
    am.setLoader(BitmapFont.class, new FontLoader(new FontFileHandleResolver()));
    FontManager.get().setAssetManager(am);
    GameTweenManager.get().register("game_fade_out", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        return exitingRect.getFadeTween(0, 1, 0.5f);
      }

      @Override
      public void onComplete() {
        Gdx.app.exit();
      }
    });

    stateStack = new Stack<>();
    addState(new LoadingState(this));
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

  public GameState setState(GameState state) {
    GameState prev = popState();
    addState(state);
    return prev;
  }

  public void addState(GameState state) {
    if (stateStack.size() > 0) {
      removeInput(stateStack.peek());
    }
    state.create();
    stateStack.add(state);
    currentState = stateStack.peek();
    stateChanged = true;
    addInput(state);
  }

  public GameState popState() {
    GameState state = stateStack.pop();
    removeInput(state);
    if (state != null) {
      state.dispose();
    }
    if (stateStack.size() > 0) {
      currentState = stateStack.peek();
      currentState.resume();
      addInput(currentState);
    } else {
      currentState = null;
    }
    stateChanged = true;
    return state;
  }

  public void addInput(InputProcessor input) {
    inputMul.addProcessor(input);
  }

  public void removeInput(InputProcessor input) { inputMul.removeProcessor(input); }

  public void exit() {
    exiting = true;
    GameTweenManager.get().start("game_fade_out");
  }

  public void update() {
    currentState.update(STEP_SECONDS);
    GameTweenManager.get().update(STEP_SECONDS);
  }

  @Override
  public void render () {
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
    if (exiting) {
      exitingRect.draw(sr, false);
    }
  }

  @Override
  public void dispose() {
    currentState.dispose();
    renderer.dispose();
    Models.dispose();
    Universe.get().dispose();
  }
}
