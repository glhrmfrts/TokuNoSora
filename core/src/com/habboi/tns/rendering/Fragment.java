package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Fragment {

  public ModelInstance modelInstance;
  public boolean glow;
  public boolean visible = true;

  public Fragment(ModelInstance modelInstance) {
    this.modelInstance = modelInstance;
  }

  public Fragment glow(boolean glow) {
    this.glow = glow;
    return this;
  }

  public Fragment visible(boolean visible) {
    this.visible = visible;
    return this;
  }
}