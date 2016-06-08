package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Ship;

/**
 * Represents a tile instance on the level.
 */
public class Tile extends Cell {
  TouchEffect effect;
  Vector3 half = new Vector3();

  enum TouchEffect {
    None
  }

  public Tile(Vector3 pos, Vector3 size, TouchEffect effect, Model model) {
    half.set(size.x/2, size.y/2, size.z/2);
    float x = pos.x + half.x;
    float y = pos.y + half.y;
    float z = -(pos.z + half.z);

    this.pos.set(x, y, z);
    this.effect = effect;
    modelInstance = new ModelInstance(model);
  }

  @Override
  public void render(ModelBatch batch, Environment environment) {
    modelInstance.transform.setTranslation(pos.x - half.x, pos.y - half.y, pos.z + half.z);
    batch.render(modelInstance, environment);
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
    return false;
  }
}
