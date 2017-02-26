package com.habboi.tns.rendering;

import com.badlogic.gdx.utils.Array;

public class Scene {

  private Array<Fragment> fragments = new Array<>();

  public Scene() {
  }

  public void add(Fragment fragment) {
    fragments.add(fragment);
  }

  public Array<Fragment> getFragments() {
    return fragments;
  }
}