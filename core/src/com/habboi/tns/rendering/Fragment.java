package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;

public class Fragment {

  public ModelInstance modelInstance;
  public PointLight light;
  public Array<Fragment> children = new Array<>();
  private boolean glow;
  private boolean visible = true;

  public Fragment(ModelInstance modelInstance) {
    this.modelInstance = modelInstance;
  }

  public boolean glow() {
    return glow;
  }

  public Fragment glow(boolean glow) {
    this.glow = glow;
    return this;
  }

  public boolean visible() {
    return visible;
  }

  public Fragment visible(boolean visible) {
    this.visible = visible;
    return this;
  }
}