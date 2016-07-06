package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.habboi.tns.states.MenuState;

import java.util.ArrayList;

/**
 * Handles a generic menu interaction.
 */
public abstract class BaseMenu implements Menu {
  int activeIndex;
  ArrayList<MenuItem> items = new ArrayList<>();
  float top;
  Rect highlight;
  Rect highlightBorder;
  ShapeRenderer sr;
  SpriteBatch sb;
  MenuState menuState;
  Rectangle bounds = new Rectangle();
  Rectangle itemBounds = new Rectangle();

  public void setSize(float x, float y) {
    bounds.setSize(x, y);
  }

  public void setItemSize(float x, float y) {
    itemBounds.setSize(x, y);
  }

  public void setCenter(float x, float y) {
    bounds.x = x;
    bounds.y = y;
    itemBounds.x = x;
    itemBounds.y = y;
  }

  public void createHighlight() {
    highlight = new Rect(itemBounds);
    highlightBorder = new Rect(itemBounds, Color.WHITE);
  }

  public void setHighlight() {
    MenuItem item = items.get(activeIndex);
    highlight.getRectangle().setY(item.bounds.y - item.bounds.height/2);
    highlightBorder.getRectangle().setY(item.bounds.y - item.bounds.height/2);
  }

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.DOWN && activeIndex < items.size()-1) {
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
