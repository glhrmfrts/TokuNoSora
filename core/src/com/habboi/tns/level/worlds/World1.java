package com.habboi.tns.level.worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.level.Models;
import com.habboi.tns.rendering.GameRenderer;

import java.util.ArrayList;

/**
 * Created by w7 on 03/07/2016.
 */
public class World1 extends World {
  static class Line {
    float zOffset;
    ModelInstance instance;
  }

  static final float DEPTH = 800f;
  static final float WIDTH = 400f;
  static final float DISTANCE_Y = -10;

  float offset;
  float spread;
  int count;
  Texture backgroundTexture;
  Vector3 center = new Vector3();
  ModelInstance planeInstance;
  ArrayList<Line> lines = new ArrayList<>();

  public World1() {
    super(2, 2, "The Beginning of An End", "nice.ogg");
    addColor(0x370737);
    addColor(0xf540ef);
    addColor(0x5678e3);
    addColor(0x1a37f5);
    addColor(0x5a67c7);
    addTileModel(new int[][]{
            new int[]{0, 0, 0, 0}, new int[]{1, 0, 0, 1}, new int[]{0, 0, 0, 0},
            new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}
    });
    addTileModel(new int[][]{
            new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4},
            new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4}
    });
    addTileModel(new int[][]{
            new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3},
            new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3}
    });
    addTunnelModel(new int[]{1, 4});

    // create the landscape
    count = 40;
    spread = DEPTH / count;
    planeInstance = new ModelInstance(Models.createPlaneModel(colors.get(0)));
    planeInstance.transform.setToScaling(WIDTH, 1, DEPTH);
    Model lineModel = Models.createLineModel(colors.get(1), new int[]{-1, 0, 0, 1, 0, 0});
    for (int i = 0; i < count; i++) {
      Line line = new Line();
      line.zOffset = spread * i;
      line.instance = new ModelInstance(lineModel);
      line.instance.transform.setToScaling(WIDTH, 1, 1);
      lines.add(line);
    }

    // the texture would be loaded synchronously anyway, so :)
    backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
  }

  @Override
  public void reset() {
    offset = 0;
  }

  @Override
  public void update(Vector3 shipPos, float vel, float dt) {
    center.set(shipPos);
    offset = vel * dt * 2;
  }

  @Override
  public void render(GameRenderer renderer) {
    renderer.clear(Color.BLACK);
    renderBackground(renderer);
    planeInstance.transform.setTranslation(center.x, center.y + DISTANCE_Y - 0.5f, center.z);
    renderer.render(planeInstance);
    final float base = center.z - DEPTH/1.1f;
    for (int i = 0; i < count; i++) {
      Line line = lines.get(i);
      line.zOffset += offset;
      line.zOffset %= DEPTH;
      line.instance.transform.setTranslation(center.x, center.y + DISTANCE_Y, base + line.zOffset);
      renderer.renderGlow(line.instance);
    }
    offset = 0;
  }

  private void renderBackground(GameRenderer renderer) {
    renderer.beginOrtho();
    renderer.getSpriteBatch().draw(backgroundTexture, 0, 0);
    renderer.endOrtho();
  }

  @Override
  public void dispose() {
    lines.get(0).instance.model.dispose();
    lines.clear();
  }
}
