package com.habboi.tns.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;

/**
 * Handles a generic menu interaction.
 */
public abstract class BaseMenu implements Menu {
  int activeIndex;
  Array<MenuItem> items = new Array<>();

  public abstract boolean onChange(int delta);
  public abstract void update(float dt);
  public abstract void render();

  private boolean checkHover(int screenX, int screenY) {
    float x = (float)screenX;
    float y = Gdx.graphics.getHeight() - (float)screenY;
    int prevActiveIndex = activeIndex;
    for (int i = 0; i < items.size; i++) {
      if (items.get(i).bounds.contains(x, y)) {
        activeIndex = i;
        break;
      }
    }
    if (activeIndex != prevActiveIndex) {
      return onChange(activeIndex - prevActiveIndex);
    }
    return false;
  }

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.DOWN && activeIndex < items.size-1) {
      activeIndex++;
      return onChange(1);
    } else if (keycode == Input.Keys.UP && activeIndex > 0) {
      activeIndex--;
      return onChange(-1);
    } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
      items.get(activeIndex).action.doAction();
      return true;
    }
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
    return checkHover(screenX, screenY);
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    float x = screenX;
    float y = Gdx.graphics.getHeight() - screenY;
    boolean contains = false;
    for (int i = 0; i < items.size; i++) {
      if (items.get(i).bounds.contains(x, y)) {
        activeIndex = i;
        onChange(i - activeIndex);
        contains = true;
      }
    }
    if (contains) {
      items.get(activeIndex).action.doAction();
    }
    return contains;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return checkHover(screenX, screenY);
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
