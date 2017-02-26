package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Fragment {

  ModelInstance modelInstance;
  boolean glow;
  boolean visible;
  
  public Fragment(ModelInstance modelInstance, boolean glow, boolean visible) {
    this.modelInstance = modelInstance;
    this.glow = glow;
    this.visible = visible;
  }
}