package com.habboi.tns;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;
import com.habboi.tns.states.*;
import com.habboi.tns.rendering.GameRenderer;

public class Game extends ApplicationAdapter {
  static final float STEP_SECONDS = 0.016f;
  static final float STEP         = STEP_SECONDS * 1000f;

  long accumUpdateTime;
  long lastUpdateTime = -1;

  InputMultiplexer inputMul;
  GameRenderer renderer;
  GameState currentState;
  boolean stateChanged;

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
		setCurrentState(new InGameState(this));
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

  public com.habboi.tns.rendering.GameRenderer getRenderer() {
    return renderer;
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
      currentState.update(STEP_SECONDS);
      if (stateChanged) {
        currentState.update(STEP_SECONDS);
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
