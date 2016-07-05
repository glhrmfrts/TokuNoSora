package com.habboi.tns.ui;

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
}
