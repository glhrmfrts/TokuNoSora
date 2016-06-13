package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Ship;
import com.habboi.tns.rendering.GameRenderer;

/**
 * Represents a tile instance on the level.
 */
public class Tile extends Cell {
  static final float TILE_WIDTH = 1;
  static final float TILE_HEIGHT = 0.25f;
  static final float TILE_DEPTH = 1;
  static Model outlineModel;

  TouchEffect effect;
  Vector3 half = new Vector3();
  ModelInstance outlineInstance;
  final Color outlineColor = new Color();

  enum TouchEffect {
    None
  }

  private static Model createOutlineModel() {
    if (outlineModel != null) return outlineModel;

    ModelBuilder mb = new ModelBuilder();
    MeshPartBuilder partBuilder;

    mb.begin();

    Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
    // create bottom part
    partBuilder = mb.part("bottom", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(0, 0, -1, 0, 0, 0);
    partBuilder.line(0, 0, 0, 1, 0, 0);
    partBuilder.line(1, 0, 0, 1, 0, -1);
    partBuilder.line(1, 0, -1, 0, 0, -1);

    // create top part
    partBuilder = mb.part("top", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(0, 1, -1, 0, 1, 0);
    partBuilder.line(0, 1, 0, 1, 1, 0);
    partBuilder.line(1, 1, 0, 1, 1, -1);
    partBuilder.line(1, 1, -1, 0, 1, -1);

    // create left part
    partBuilder = mb.part("left", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(0, 0, -1, 0, 0, 0);
    partBuilder.line(0, 0, 0, 0, 1, 0);
    partBuilder.line(0, 1, 0, 0, 1, -1);
    partBuilder.line(0, 1, -1, 0, 0, -1);

    // create right part
    partBuilder = mb.part("right", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(1, 0, -1, 1, 0, 0);
    partBuilder.line(1, 0, 0, 1, 1, 0);
    partBuilder.line(1, 1, 0, 1, 1, -1);
    partBuilder.line(1, 1, -1, 1, 0, -1);

    // create front part
    partBuilder = mb.part("front", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(1, 0, -1, 0, 0, -1);
    partBuilder.line(0, 0, -1, 0, 1, -1);
    partBuilder.line(0, 1, -1, 1, 1, -1);
    partBuilder.line(1, 1, -1, 1, 0, -1);

    // create back part (visible to player)
    partBuilder = mb.part("back", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(1, 0, 0, 0, 0, 0);
    partBuilder.line(0, 0, 0, 0, 1, 0);
    partBuilder.line(0, 1, 0, 1, 1, 0);
    partBuilder.line(1, 1, 0, 1, 0, 0);

    return outlineModel = mb.end();
  }

  public Tile(Vector3 pos, Vector3 size, Color outlineColor, TouchEffect effect, Model model) {
    size.x *= TILE_WIDTH;
    size.y *= TILE_HEIGHT;
    size.z *= TILE_DEPTH;

    half.set(size.x/2, size.y/2, size.z/2);
    float x = pos.x + half.x;
    float y = pos.y + half.y;
    float z = -(pos.z + half.z);

    this.pos.set(x, y, z);
    this.effect = effect;
    this.outlineColor.set(outlineColor);

    modelInstance = new ModelInstance(model);
    modelInstance.transform.setToScaling(size.x, size.y, size.z);

    outlineInstance = new ModelInstance(createOutlineModel());
    outlineInstance.transform.setToScaling(size.x, size.y, size.z);
  }

  @Override
  public void render(GameRenderer renderer) {
    modelInstance.transform.setTranslation(pos.x - half.x, pos.y - half.y, pos.z + half.z);
    renderer.render(modelInstance);

    outlineInstance.transform.setTranslation(pos.x - half.x, pos.y - half.y, pos.z + half.z);
    renderer.renderGlow(outlineInstance, outlineColor);
  }

  @Override
  public boolean checkCollision(Ship ship) {
    Vector3 dif = new Vector3(ship.pos).sub(pos);

    float dx = half.x + ship.half.x - Math.abs(dif.x);
    if (dx <= 0) {
      return false;
    }

    float dy = half.y + ship.half.y - Math.abs(dif.y);
    if (dy <= 0) {
      return false;
    }

    float dz = half.z + ship.half.z - Math.abs(dif.z);
    if (dz <= 0) {
      return false;
    }

    CollisionInfo c = collisionInfo;
    c.clear();
    if (dx < dy && dx < dz && dy > 0.1f) {
      c.normal.x = Math.copySign(1, dif.x);
      c.depth = dx;
    } else if (dy < dz || dy < 0.1f) {
      c.normal.y = Math.copySign(1, dif.y);
      c.depth = dy;

      float slide = Math.abs(dif.x)/half.x;
      if (slide > Ship.SLIDE_DISTANCE_MIN && c.normal.y == 1) {
        c.slide = slide * Math.copySign(1, dif.x) * 2;
      }
    } else {
      c.normal.z = Math.copySign(1, dif.z);
      c.depth = dz;
    }
    return true;
  }
}
