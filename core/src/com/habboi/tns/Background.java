package com.habboi.tns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.rendering.GameRenderer;

import java.util.ArrayList;

/**
 * Created by w7 on 10/06/2016.
 */
public class Background implements Disposable {
  static class Line {
    float zOffset;
    ModelInstance instance;
  }

  static final float DEPTH = 800f;
  static final float WIDTH = 200f;
  static final float DISTANCE_Y = -5;
  static Model lineModel;

  ArrayList<Line> lines = new ArrayList<>();
  float offset;
  float spread;
  int count;
  Vector3 center = new Vector3();

  private static Model createLineModel() {
    if (lineModel != null) return lineModel;

    ModelBuilder mb = new ModelBuilder();
    mb.begin();

    final Material material = new Material(ColorAttribute.createDiffuse(Color.RED));
    MeshPartBuilder partBuilder = mb.part("line", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);

    partBuilder.setColor(Color.WHITE);
    partBuilder.line(-1, 0, 0, 1, 0, 0);

    return lineModel = mb.end();
  }

  public Background(int count) {
    this.count = count;
    spread = DEPTH / count;
    for (int i = 0; i < count; i++) {
      Line line = new Line();
      line.zOffset = spread * i;
      line.instance = new ModelInstance(createLineModel());
      line.instance.transform.setToScaling(WIDTH, 1, 1);
      lines.add(line);
    }
  }

  public void update(Ship ship, float dt) {
    center.set(ship.pos);
    offset = -ship.vel.z * dt * 2;
  }

  public void render(GameRenderer renderer) {
    final float base = center.z - DEPTH/1.1f;
    for (int i = 0; i < count; i++) {
      Line line = lines.get(i);
      line.zOffset += offset;
      line.zOffset %= DEPTH;
      line.instance.transform.setTranslation(center.x, center.y + DISTANCE_Y, base + line.zOffset);
      renderer.renderGlow(line.instance);
    }
  }

  @Override
  public void dispose() {
    lines.clear();
  }
}
