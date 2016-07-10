package com.habboi.tns.ui;

import com.badlogic.gdx.math.Rectangle;

/**
 * A generic menu item.
 */
public abstract class MenuItem {
  Rectangle bounds = new Rectangle();
  MenuItemAction action;

  public MenuItem(Rectangle bounds) {
    this.bounds.set(bounds);
  }

  public MenuItem(float x, float y, float w, float h) {
    this.bounds.set(x, y, w, h);
  }

  public MenuItem setAction(MenuItemAction action) {
    this.action = action;
    return this;
  }
}
