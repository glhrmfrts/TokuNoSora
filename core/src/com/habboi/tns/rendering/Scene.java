package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;

public class Scene {

  private Texture backgroundTexture;
  private PerspectiveCamera camera;
  private Environment environment;
  private Array<Fragment> fragments = new Array<>();

  public Scene(float width, float height) {
    camera = new PerspectiveCamera(45, width, height);
    camera.near = 0.1f;
    camera.far = 1000f;

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.2f, 0.2f, 0.8f));
  }

  public void add(Fragment fragment) {
    fragments.add(fragment);

    if (fragment.light != null) {
      environment.add(fragment.light);
    }
  }

  public Texture getBackgroundTexture() {
    return backgroundTexture;
  }

  public PerspectiveCamera getCamera() {
    camera.update();
    return camera;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public Array<Fragment> getFragments() {
    return fragments;
  }

  public void setBackgroundTexture(Texture t) {
    backgroundTexture = t;
  }
}