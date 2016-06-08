package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Ship;

/**
 * Represents a cell in the level.
 */
public abstract class Cell {
  Vector3 pos = new Vector3();
  ModelInstance modelInstance;
  CollisionInfo collisionInfo;

  static class CollisionInfo {
    Vector3 normal;
    float depth;
    float slide;

    void clear() {
      normal.set(0, 0, 0);
      depth = 0;
      slide = 0;
    }
  }

  public abstract void render(ModelBatch batch, Environment environment);
  public abstract boolean checkCollision(Ship ship);
}
