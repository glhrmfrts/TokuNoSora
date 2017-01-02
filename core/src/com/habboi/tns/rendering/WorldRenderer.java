package com.habboi.tns.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.habboi.tns.worlds.World;

public class WorldRenderer {
  private PerspectiveCamera camera;

  public WorldRenderer() {
    camera = new PerspectiveCamera(GameRenderer.FOV, GameRenderer.Width, GameRenderer.Height);
    camera.near = 25f;
    camera.far = 1000f;
  }

  public PerspectiveCamera getCamera() {
    camera.update();
    return camera;
  }

  public void render(World world, SpriteBatch sb, ModelBatch batch, Environment environment) {
    if (world.background != null) {
        sb.begin();
        sb.draw(world.background, 0, 0);
        sb.end();
    }

    Gdx.gl.glLineWidth(1);
    for (ModelInstance model : world.getInstances()) {
      batch.render(model, environment);
    }
  }
}
