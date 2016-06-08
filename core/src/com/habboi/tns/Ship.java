package com.habboi.tns;

import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.level.Cell;

/**
 * Created by w7 on 08/06/2016.
 */
public class Ship {
  public static final float SLIDE_DISTANCE_MIN = 1.15f;

  public Vector3 pos = new Vector3();
  public Vector3 vel = new Vector3();
  public Vector3 half = new Vector3();

  public Ship(Vector3 pos) {
    this.pos.set(pos);
  }

  public void update(float dt) {

  }

  public void render() {

  }

  public boolean handleCollision(Cell cell) {
    return false;
  }
}
