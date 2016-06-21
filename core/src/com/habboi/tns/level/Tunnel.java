package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Models;
import com.habboi.tns.Ship;
import com.habboi.tns.rendering.GameRenderer;

/**
 * Represents a tunnel instance on the level.
 */
public class Tunnel extends Cell {
  static final float TUNNEL_WIDTH = Tile.TILE_WIDTH;
  static final float TUNNEL_HEIGHT = 1;

  boolean shipInside;
  float depth;
  ModelInstance outlineInstance;

  // vectors used in collision detection
  // created here to avoid garbage collection
  Vector3 half = new Vector3();
  Vector3 dif = new Vector3();
  Vector3 closest = new Vector3();
  Vector2 n = new Vector2();

  public Tunnel(Vector3 pos, float depth, int preset) {
    this.depth = depth;

    half.set(TUNNEL_WIDTH/2, TUNNEL_HEIGHT/2, (depth*Tile.TILE_DEPTH)/2);
    float x = pos.x*TUNNEL_WIDTH;
    float y = pos.y*Tile.TILE_HEIGHT;
    float z = -(pos.z*Tile.TILE_DEPTH + half.z);
    this.pos.set(x, y, z);

    modelInstance = new ModelInstance(Models.getTunnelModel(preset));
    modelInstance.transform.setToScaling(TUNNEL_WIDTH, TUNNEL_HEIGHT, depth*Tile.TILE_DEPTH);

    outlineInstance = new ModelInstance(Models.getTunnelOutlineModel());
    outlineInstance.transform.setToScaling(TUNNEL_WIDTH, TUNNEL_HEIGHT, depth*Tile.TILE_DEPTH);
  }

  public boolean isShipInside() {
    return shipInside;
  }

  @Override
  public void render(GameRenderer renderer) {
    modelInstance.transform.setTranslation(pos.x, pos.y + TUNNEL_HEIGHT/4, pos.z);
    renderer.render(modelInstance);

    outlineInstance.transform.setTranslation(pos.x, pos.y + TUNNEL_HEIGHT/4, pos.z);
    renderer.renderGlow(outlineInstance);
  }

  @Override
  public boolean checkCollision(Ship ship) {
    Vector3 spos = ship.pos;
    Vector3 shalf = ship.half;
    final float thickness = 0.1f;
    final float radius = half.x+0.011f;

    float rightEdge = pos.x + radius;
    float leftEdge = pos.x - radius;
    float upperEdge = pos.y + radius;
    float downEdge = pos.y - radius;
    float frontEdge = pos.z - half.z;
    float backEdge = pos.z + half.z;

    boolean insideX = spos.x + (shalf.x) < rightEdge && spos.x - (shalf.x) > leftEdge;
    boolean insideY = spos.y < upperEdge && spos.y > downEdge;
    boolean insideZ = spos.z > frontEdge && spos.z < backEdge;

    Cell.CollisionInfo c = collisionInfo;
    c.clear();
    shipInside = false;
    if (insideY) {
      if (insideZ && insideX) {
        float cos = (spos.x - pos.x) / (rightEdge - thickness - pos.x);
        float sin = (spos.y - pos.y) / (upperEdge - thickness - pos.y);
        shipInside = true;

        if (Math.abs(cos) > 0.12f || sin > 0.4f) {
          c.normal.x = -Math.copySign(1, cos);
          c.normal.y = (sin > 0.4f) ? -1 : 0;

          if (cos < 0)
            c.depth = leftEdge - (spos.x - shalf.x);
          else
            c.depth = spos.x + shalf.x - rightEdge;
          return true;
        }
      } else if (!insideX) {
        // possible frontal collision
        dif.set(spos).sub(pos);
        if (dif.z > half.z) {
          float dx = half.x + shalf.x - Math.abs(dif.x);
          if (dx <= 0.0f) return false;

          float dz = half.z + shalf.z - Math.abs(dif.z);
          if (dz <= 0.0f) return false;

          if (dx < dz) {
            c.normal.x = Math.copySign(1, dif.x);
            c.depth = dx;
          } else {
            c.normal.z = Math.copySign(1, dif.z);
            c.depth = dz;
          }
          return true;
        }
      }
    }

    if (insideZ && !shipInside) {
      // since we know we are inside the tunnel's z range we simply
      // discard the z axis in this collision detection
      dif.set(spos).sub(pos);
      closest.set(dif);
      closest.x = Math.max(-shalf.x, Math.min(closest.x, shalf.x));
      closest.y = Math.max(-shalf.y, Math.min(closest.y, shalf.y));

      boolean intersect = false;
      if (dif == closest) {
        intersect = true;
        if (dif.x < dif.y) {
          if (closest.x > 0)
            closest.x = shalf.x;
          else
            closest.x = -shalf.x;
        } else {
          if (closest.y > 0)
            closest.y = shalf.y;
          else
            closest.y = -shalf.y;
        }
      }

      n.set(dif.x, dif.y).sub(closest.x, closest.y);

      float dsqr = n.len2();
      float rsqr = radius;
      rsqr *= rsqr;

      if (dsqr > rsqr) return false;

      float d = (float)Math.sqrt(dsqr);
      c.depth = radius - d;
      if (intersect)
        c.normal.set(-n.x / d, -n.y / d, 0);
      else
        c.normal.set(n.x / d, n.y / d, 0);

      c.slide = c.normal.x;
      return true;
    }

    return false;
  }
}
