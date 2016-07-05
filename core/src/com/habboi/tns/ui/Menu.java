package com.habboi.tns.ui;

/**
 * Created by w7 on 03/07/2016.
 */
public interface Menu {

  boolean onChange(int delta);
  boolean keyDown(int keycode);
  void render();
}
