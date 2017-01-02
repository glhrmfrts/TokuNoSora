package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.habboi.tns.worlds.World;

public class WorldRenderer {
  private PerspectiveCamera camera;

  public WorldRenderer() {
    this(20f, 1000f);
  }

  public WorldRenderer(float near, float far) {
    camera = new PerspectiveCamera(GameRenderer.FOV, GameRenderer.Width, GameRenderer.Height);
    camera.near = near;
    camera.far = far;
  }

  public PerspectiveCamera getCamera() {
    return camera;
  }

  public void render(World world, SpriteBatch sb, ModelBatch batch, Environment environment) {
    if (world.background != null) {
        sb.begin();
        sb.draw(world.background, 0, 0);
        sb.end();
    }

    for (ModelInstance model : world.getInstances()) {
      batch.render(model, environment);
    }
  }
}
