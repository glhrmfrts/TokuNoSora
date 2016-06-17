package com.habboi.tns;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Controls the camera of the ship.
 */
public class ShipCamera {
  static final float DISTANCE_Y = 2;
  static final float DISTANCE_Z = 8;
  static final float LERP_X = 3;
  static final float LERP_Y = 3;
  static final float LERP_Z = 10;
  Ship ship;
  PerspectiveCamera cam;

  public ShipCamera(Ship ship, PerspectiveCamera cam) {
    this.ship = ship;
    this.cam = cam;
    cam.near = 0.1f;
    cam.far = 1000f;
    cam.position.set(ship.pos.x, ship.pos.y + DISTANCE_Y, ship.pos.z + DISTANCE_Z);
  }

  private void setCameraPos(Vector3 pos, float dt) {
    final float max = 80;

    float dx = pos.x - cam.position.x;
    float dy = pos.y + DISTANCE_Y - cam.position.y;
    float dz = pos.z + DISTANCE_Z - cam.position.z;

    dx = Math.min(Math.abs(dx), max) * Math.copySign(1, dx);
    dy = Math.min(Math.abs(dy), max) * Math.copySign(1, dy);
    dz = Math.min(Math.abs(dz), max) * Math.copySign(1, dz);

    cam.translate(dx * LERP_X * dt, dy * LERP_Y * dt, dz * LERP_Z * dt);
  }

  public void update(float dt) {
    setCameraPos(ship.pos, dt);
    cam.lookAt(cam.position.x, cam.position.y - 1, ship.pos.z);
  }

  public PerspectiveCamera getCam() {
    cam.update();
    return cam;
  }
}
