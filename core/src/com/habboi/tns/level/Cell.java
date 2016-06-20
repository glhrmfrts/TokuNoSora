package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.Ship;

/**
 * Represents a cell in the level.
 */
public abstract class Cell {
  public enum TouchEffect {
    None,
    End
  }

  public static class CollisionInfo {
    public Vector3 normal = new Vector3();
    public float depth;
    public float slide;

    void clear() {
      normal.set(0, 0, 0);
      depth = 0;
      slide = 0;
    }

    public String toString() {
      String result = "{" + normal;
      result += ", " + depth + ", " + slide + "}";
      return result;
    }
  }

  public CollisionInfo collisionInfo = new CollisionInfo();
  public TouchEffect effect;

  Vector3 pos = new Vector3();
  ModelInstance modelInstance;

  public abstract void render(GameRenderer renderer);
  public abstract boolean checkCollision(Ship ship);
}
