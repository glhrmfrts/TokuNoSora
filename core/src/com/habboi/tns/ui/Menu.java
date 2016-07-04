package com.habboi.tns.ui;

import com.badlogic.gdx.InputProcessor;
import com.habboi.tns.rendering.GameRenderer;

/**
 * Created by w7 on 03/07/2016.
 */
public interface Menu extends InputProcessor {

  boolean onChange(int delta);
  void render();
}
