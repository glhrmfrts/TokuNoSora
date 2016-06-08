package com.habboi.tns;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;
import com.habboi.tns.states.*;

public class Game extends ApplicationAdapter {
  static final float STEP_SECONDS = 0.016f;
  static final float STEP         = STEP_SECONDS * 1000f;

  long accumUpdateTime;
  long lastUpdateTime = -1;

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

  public void setCurrentState(GameState state) {
    if (currentState != null) {
      currentState.dispose();
      stateChanged = true;
    }
    currentState = state;
    currentState.create();
  }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
			return;
		}

    long start = TimeUtils.millis();
    if (lastUpdateTime == -1) {
      lastUpdateTime = 1;
      return;
    }

    float delta = (lastUpdateTime - start);
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
  }
}
