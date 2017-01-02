package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelObject;

public class LevelRenderer {
  private PerspectiveCamera camera;

  public LevelRenderer() {
    camera = new PerspectiveCamera(GameRenderer.FOV, GameRenderer.Width, GameRenderer.Height);
    camera.near = 0.1f;
    camera.far = 600f;
  }

  public PerspectiveCamera getCamera() {
    camera.update();
    return camera;
  }

  public void render(Level level, ModelBatch modelBatch, Environment environment) {
    for (LevelObject object : level.getObjects()) {
      // TODO: check if object is visible
      if (object.renderer != null) {
        object.renderer.render(object, modelBatch, environment);
      } else if (object.modelInstance != null) {
        modelBatch.render(object.modelInstance, environment);
      }

      if (object.effectRenderer != null) {
        object.effectRenderer.render(modelBatch, environment);
      }
    }
  }
}