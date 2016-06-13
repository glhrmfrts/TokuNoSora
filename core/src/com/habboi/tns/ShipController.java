package com.habboi.tns;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Controls the ship, also record input.
 */
public class ShipController implements InputProcessor {
  int[] keys = new int[Key.NUM_KEYS.ordinal()];
  int[] prevKeys = new int[Key.NUM_KEYS.ordinal()];
  boolean recording;
  float time;

  enum Key {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    JUMP,
    NUM_KEYS
  }

  public ShipController(boolean record) {
    recording = record;
  }

  public boolean isDown(Key key) {
    return keys[key.ordinal()] > 0;
  }

  public boolean isJustDown(Key key) {
    return isDown(key) && !(prevKeys[key.ordinal()] > 0);
  }

  public void update(float dt) {
    if (recording) {
      time += dt;
    }

    System.arraycopy(keys, 0, prevKeys, 0, keys.length);
  }

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
      case Input.Keys.LEFT:
        keys[Key.LEFT.ordinal()]++;
        return true;

      case Input.Keys.RIGHT:
        keys[Key.RIGHT.ordinal()]++;
        return true;

      case Input.Keys.UP:
        keys[Key.UP.ordinal()]++;
        return true;

      case Input.Keys.DOWN:
        keys[Key.DOWN.ordinal()]++;
        return true;

      case Input.Keys.SPACE:
        keys[Key.JUMP.ordinal()]++;
        return true;
    }
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    switch (keycode) {
      case Input.Keys.LEFT:
        keys[Key.LEFT.ordinal()] = 0;
        return true;

      case Input.Keys.RIGHT:
        keys[Key.RIGHT.ordinal()] = 0;
        return true;

      case Input.Keys.UP:
        keys[Key.UP.ordinal()] = 0;
        return true;

      case Input.Keys.DOWN:
        keys[Key.DOWN.ordinal()] = 0;
        return true;

      case Input.Keys.SPACE:
        keys[Key.JUMP.ordinal()] = 0;
        return true;
    }
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
