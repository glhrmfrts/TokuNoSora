package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;

public class Scene {

  private PerspectiveCamera camera;
  private Array<Fragment> fragments = new Array<>();

  public Scene(float width, float height) {
    camera = new PerspectiveCamera(45, width, height);
    camera.near = 0.1f;
    camera.far = 1000f;
  }

  public void add(Fragment fragment) {
    fragments.add(fragment);
  }

  public PerspectiveCamera getCamera() {
    camera.update();
    return camera;
  }

  public Array<Fragment> getFragments() {
    return fragments;
  }
}