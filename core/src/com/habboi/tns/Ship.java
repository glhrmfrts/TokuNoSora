package com.habboi.tns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.level.Cell;
import com.habboi.tns.ShipController.Key;
import com.habboi.tns.level.Sun;
import com.habboi.tns.level.Tile;
import com.habboi.tns.rendering.GameRenderer;

/**
 * My ship :).
 */
public class Ship {
  public enum State {
    ALIVE, EXPLODED, ENDED
  }
  public State state = State.ALIVE;
  public Vector3 pos = new Vector3();
  public Vector3 vel = new Vector3();
  public Vector3 half = new Vector3();
  public boolean readyToEnd;

  static final float BODY_WIDTH = 0.75f;
  static final float BODY_HEIGHT = 0.3f;
  static final float BODY_DEPTH = 2.5f;
  static final float MAX_VEL = 50;
  static final float STEER_VEL = 12;
  static final float STEER_ACCELERATION = 50;
  static final float MAX_STEER_ACCUL = 300;
  static final float JUMP_VEL = 8;
  static final float MIN_BOUNCE_VEL = 0.4f;
  static final float BOUNCE_FACTOR = 0.35f;
  static final Color COLOR = new Color(0xff);
  //static final Color OUTLINE_COLOR = new Color(0x1A0E74<<8 | 0xFF);
  static final Color OUTLINE_COLOR = new Color(0x00ff00<<8 | 0xFF);

  ModelInstance bodyInstance;
  ModelInstance outlineInstance;
  ShipController controller;
  Sun sun;
  int floorCollisions;
  float dSlide;
  float steerAccul;

  public Ship(Vector3 pos, ShipController controller) {
    half.set(BODY_WIDTH / 2f, BODY_HEIGHT / 2f, BODY_DEPTH / 2f);

    this.pos.set(pos.x*Tile.TILE_WIDTH, pos.y*Tile.TILE_HEIGHT, -pos.z*Tile.TILE_DEPTH);
    this.pos.z -= half.z;

    this.controller = controller;

    bodyInstance = new ModelInstance(Models.getShipModel());
    bodyInstance.transform.setToScaling(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH);

    outlineInstance = new ModelInstance(Models.getShipOutlineModel());
    outlineInstance.transform.setToScaling(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH);

    Renderable r = new Renderable();
    ColorAttribute attr;

    // set the body color
    bodyInstance.getRenderable(r);
    attr = (ColorAttribute) r.material.get(ColorAttribute.Diffuse);
    attr.color.set(COLOR);

    // set the outline color
    outlineInstance.getRenderable(r);
    attr = (ColorAttribute) r.material.get(ColorAttribute.Diffuse);
    attr.color.set(OUTLINE_COLOR);
  }

  public Sun getSun() {
    return sun;
  }

  private static float steer(float c, float t, float a, float dt) {
    if (c == t) {
      return t;
    }
    float dir = Math.signum(t - c);
    c += a * dir * dt;
    return (dir == Math.signum(t - c)) ? c : t;
  }

  public void update(float dt) {
    if (state == State.ENDED) {
      vel.set(0, 0, 0);
      return;
    }
    if (controller.isDown(Key.UP)) {
      if (vel.z > -MAX_VEL) {
        vel.z -= 1;
      } else {
        vel.z = -MAX_VEL;
      }
    } else if (controller.isDown(Key.DOWN)) {
      if (vel.z < 0) {
        vel.z += 1;
      } else {
        vel.z = 0;
      }
    }

    if (floorCollisions > 0) {
      // only steer and jump when ship is on the floor
      float target = 0;
      if ((dSlide <= 0 || floorCollisions > 1) && controller.isDown(Key.LEFT)) {
        target = -STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
      } else if ((dSlide >= 0 || floorCollisions > 1) && controller.isDown(Key.RIGHT)) {
        target = STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
      }
      float accel = STEER_ACCELERATION + steerAccul;
      if (target == 0) {
        accel *= 4;
      }
      vel.x = steer(vel.x, target, accel, dt);
      steerAccul = 0;

      if (controller.isJustDown(Key.JUMP)) {
        vel.y = JUMP_VEL;
      } else if (dSlide != 0) {
        vel.y = Math.min(vel.y, 0);
      }
    } else {
      if (controller.isDown(Key.LEFT) || controller.isDown(Key.RIGHT)) {
        steerAccul = Math.min(steerAccul + STEER_VEL, MAX_STEER_ACCUL);
      }
    }

    floorCollisions = 0;
    dSlide = 0;
    controller.update(dt);
  }

  public void render(GameRenderer renderer) {
    if (state == State.ENDED) {
      return;
    }
    bodyInstance.transform.setTranslation(pos);
    renderer.render(bodyInstance);
    outlineInstance.transform.setTranslation(pos);
    renderer.renderGlow(outlineInstance);
  }

  public boolean handleCollision(Cell cell) {
    Cell.CollisionInfo c = cell.collisionInfo;

    if (cell.effect == Cell.TouchEffect.END) {
      if (readyToEnd) {
        state = State.ENDED;
        sun = (Sun) cell;
      }
      return false;
    }

    if (c.normal.z == 1 && -vel.z > MAX_VEL/2) {
      state = State.EXPLODED;
      return true;
    }

    if (c.normal.y == 1) {
      floorCollisions++;

      if (vel.y < -MIN_BOUNCE_VEL) {
        vel.y = -vel.y * BOUNCE_FACTOR;
      }
    }

    if (c.slide != 0) {
      float dx = Math.copySign(1, vel.x);
      float ds = Math.copySign(1, c.slide);
      if (dx == ds || vel.x == 0) {
        c.normal.x = c.slide;
        dSlide = c.slide;
      }
    }
    return true;
  }
}
