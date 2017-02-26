package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;

public class Scene {

  private PerspectiveCamera camera;
  private Array<Fragment> fragments = new Array<>();

  public Scene(float width, float height) {
    camera = new PerspectiveCamera(45, width, height);
  }

  public void add(Fragment fragment) {
    fragments.add(fragment);
  }

  public PerspectiveCamera getCamera() {
    return camera;
  }

  public Array<Fragment> getFragments() {
    return fragments;
  }
}