package com.habboi.tns.level.worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.level.Models;
import com.habboi.tns.rendering.GameRenderer;

import java.util.ArrayList;

/**
 * Created by w7 on 03/07/2016.
 */
public class World2 extends World {
  static class Rect {
    float zOffset;
    ModelInstance instance;
  }

  static final float DEPTH = 800f;
  static final float WIDTH = 40f;
  static final float HEIGHT = 20f;
  static final float STEP = 0.05f;

  float time;
  float offset;
  float spread;
  float dt;
  int count;
  int litRect;
  Texture backgroundTexture;
  Renderable tmpRenderable = new Renderable();
  Vector3 center = new Vector3();
  ArrayList<Rect> rects = new ArrayList<>();

  public World2() {
    super(2, 2, "Andromeda", "nice.ogg");
    addColor(0x64f7f7);
    addColor(0x458696);
    addColor(0x989822);
    addColor(0x1a37f5);
    addColor(0x440bad);
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
    Model rectModel = Models.createLineRectModel(colors.get(3));
    for (int i = 0; i < count; i++) {
      Rect rect = new Rect();
      rect.zOffset = spread * i;
      rect.instance = new ModelInstance(rectModel);
      rect.instance.transform.setToScaling(WIDTH, HEIGHT, 1);
      rects.add(rect);
    }

    // the texture would be loaded synchronously anyway, so :)
    backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
  }

  @Override
  public void reset() {
    offset = 0;
    litRect = 0;
    time = 0;
  }

  @Override
  public void update(Vector3 shipPos, float vel, float dt) {
    center.set(shipPos);
    offset = vel * dt * 2;
    this.dt = dt;
  }

  @Override
  public void render(GameRenderer renderer) {
    renderer.clear(Color.BLACK);
    ColorAttribute attr;
    time += dt;
    if (time >= STEP) {
      time = 0;
      litRect = (litRect + 1) % count;
    }
    int rev = count - litRect;
    rects.get(rev % count).instance.getRenderable(tmpRenderable);
    attr = (ColorAttribute) tmpRenderable.material.get(ColorAttribute.Diffuse);
    attr.color.set(colors.get(1));
    rects.get((rev + 1) % count).instance.getRenderable(tmpRenderable);
    attr = (ColorAttribute) tmpRenderable.material.get(ColorAttribute.Diffuse);
    attr.color.set(colors.get(3));
    renderBackground(renderer);
    final float base = center.z - DEPTH/1.1f;
    for (int i = 0; i < count; i++) {
      Rect rect = rects.get(i);
      rect.zOffset += offset;
      rect.zOffset %= DEPTH;
      rect.instance.transform.setTranslation(center.x, center.y, base + rect.zOffset);
      renderer.renderGlow(rect.instance);
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
    rects.get(0).instance.model.dispose();
    rects.clear();
  }
}
