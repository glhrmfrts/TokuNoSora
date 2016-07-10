package com.habboi.tns.states;

import com.badlogic.gdx.InputProcessor;
import com.habboi.tns.Game;

/**
 * An abstract game state.
 */
public abstract class GameState implements InputProcessor {
  Game game;

  public GameState(Game g) {
    game = g;
  }

  public Game getGame() {
    return game;
  }

  public abstract void create();
  public abstract void resume();
  public abstract void update(float dt);
  public abstract void render();
  public abstract void dispose();

  @Override
  public boolean keyDown(int keycode) {
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
