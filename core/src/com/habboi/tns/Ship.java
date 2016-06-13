package com.habboi.tns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.level.Cell;
import com.habboi.tns.ShipController.Key;
import com.habboi.tns.rendering.GameRenderer;

/**
 * My ship :).
 */
public class Ship {
  public static final float SLIDE_DISTANCE_MIN = 1.15f;

  public Vector3 pos = new Vector3();
  public Vector3 vel = new Vector3();
  public Vector3 half = new Vector3();

  static final float BODY_WIDTH = 1;
  static final float BODY_HEIGHT = 0.5f;
  static final float BODY_DEPTH = 2.5f;
  static final float ENGINE_WIDTH = 0.5f;
  static final float ENGINE_HEIGHT = 0.5f;
  static final float ENGINE_DEPTH = 1;
  static final float MAX_VEL = 50;
  static final float STEER_VEL = 12;
  static final float JUMP_VEL = 8;
  static final float MIN_BOUNCE_VEL = 0.4f;
  static final float BOUNCE_FACTOR = 0.35f;
  static final Color COLOR = new Color(0x7F0000<<8);

  static Model bodyModel;
  static Model engineModel;

  ModelInstance bodyInstance;
  ModelInstance[] engineInstances;
  Vector3[] enginePositions;
  ShipController controller;
  int floorCollisions;
  float dSlide;

  private static Model createBodyModel() {
    if (bodyModel != null) return bodyModel;

    MeshPartBuilder partBuilder;
    VertexInfo v1, v2, v3, v4;
    Vector3 normal;

    float front = 0.125f;

    ModelBuilder mb = new ModelBuilder();
    mb.begin();

    // create bottom part
    partBuilder = mb.part("bottom", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(0.3f, -front, -0.5f);
    v2 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f);
    v3 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f);
    v4 = new VertexInfo().setPos(-0.3f, -front, -0.5f);

    normal = Util.calculateNormal(v1.position, v2.position, v3.position);
    v1.setNor(normal);
    v2.setNor(normal);
    v3.setNor(normal);
    v4.setNor(normal);

    partBuilder.rect(v1, v2, v3, v4);

    // create top part
    partBuilder = mb.part("top", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(-0.3f, front, -0.5f);
    v2 = new VertexInfo().setPos(-0.5f, 0.5f, 0.5f);
    v3 = new VertexInfo().setPos(0.5f, 0.5f, 0.5f);
    v4 = new VertexInfo().setPos(0.3f, front, -0.5f);

    normal = Util.calculateNormal(v1.position, v2.position, v3.position);
    v1.setNor(normal);
    v2.setNor(normal);
    v3.setNor(normal);
    v4.setNor(normal);

    partBuilder.rect(v1, v2, v3, v4);

    // create left part
    partBuilder = mb.part("left", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(-0.3f, -front, -0.5f).setNor(-1, 0, 0);
    v2 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f).setNor(-1, 0, 0);
    v3 = new VertexInfo().setPos(-0.5f, 0.5f, 0.5f).setNor(-1, 0, 0);
    v4 = new VertexInfo().setPos(-0.3f, front, -0.5f).setNor(-1, 0, 0);
    partBuilder.rect(v1, v2, v3, v4);

    // create right part
    partBuilder = mb.part("right", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f).setNor(1, 0, 0);
    v2 = new VertexInfo().setPos(0.3f, -front, -0.5f).setNor(1, 0, 0);
    v3 = new VertexInfo().setPos(0.3f, front, -0.5f).setNor(1, 0, 0);
    v4 = new VertexInfo().setPos(0.5f, 0.5f, 0.5f).setNor(1, 0, 0);
    partBuilder.rect(v1, v2, v3, v4);

    // create front part
    partBuilder = mb.part("front", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(0.3f, -front, -0.5f).setNor(0, 0, -1);
    v2 = new VertexInfo().setPos(-0.3f, -front, -0.5f).setNor(0, 0, -1);
    v3 = new VertexInfo().setPos(-0.3f, front, -0.5f).setNor(0, 0, -1);
    v4 = new VertexInfo().setPos(0.3f, front, -0.5f).setNor(0, 0, -1);
    partBuilder.rect(v1, v2, v3, v4);

    // create back part (visible to player)
    partBuilder = mb.part("back", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f).setNor(0, 0, 1);
    v2 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f).setNor(0, 0, 1);
    v3 = new VertexInfo().setPos(0.5f, 0.5f, 0.5f).setNor(0, 0, 1);
    v4 = new VertexInfo().setPos(-0.5f, 0.5f, 0.5f).setNor(0, 0, 1);
    partBuilder.rect(v1, v2, v3, v4);

    return bodyModel = mb.end();
  }

  private static Model createEngineModel() {
    if (engineModel != null) return engineModel;

    MeshPartBuilder partBuilder;

    ModelBuilder mb = new ModelBuilder();
    mb.begin();

    partBuilder = mb.part("outside", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(COLOR);
    partBuilder.cone(ENGINE_WIDTH, ENGINE_DEPTH, ENGINE_HEIGHT, 8);

    return engineModel = mb.end();
  }

  public Ship(Vector3 pos, ShipController controller) {
    this.pos.set(pos);
    this.pos.z -= half.z;

    this.controller = controller;

    half.set(BODY_WIDTH / 2f, BODY_HEIGHT / 2f, BODY_DEPTH / 2f);

    // add the engines size
    half.x += ENGINE_WIDTH;

    bodyInstance = new ModelInstance(createBodyModel());
    bodyInstance.transform.setToScaling(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH);

    engineInstances = new ModelInstance[2];
    enginePositions = new Vector3[2];
    for (int i = 0; i < engineInstances.length; i++) {
      ModelInstance inst = new ModelInstance(createEngineModel());

      inst.transform.setToRotation(-1, 0, 0, 90);
      engineInstances[i] = inst;
      enginePositions[i] = new Vector3();
    }
  }

  public void update(float dt) {
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
      vel.x = 0;
      if (dSlide <= 0 && controller.isDown(Key.LEFT)) {
        vel.x = -STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
      } else if (dSlide >= 0 && controller.isDown(Key.RIGHT)) {
        vel.x = STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
      }

      if (controller.isJustDown(Key.JUMP)) {
        vel.y = JUMP_VEL;
      } else if (dSlide != 0) {
        vel.y = Math.min(vel.y, 0);
      }
    }

    floorCollisions = 0;
  }

  public void render(GameRenderer renderer) {
    bodyInstance.transform.setTranslation(pos);
    renderer.render(bodyInstance);

    for (int i = 0; i < engineInstances.length; i++) {
      enginePositions[i].set(pos);
      enginePositions[i].x = pos.x + (BODY_WIDTH/2f + 0.2f) * (i == 0 ? -1 : 1);
      enginePositions[i].y = pos.y;
      enginePositions[i].z = pos.z + (BODY_DEPTH/2f) - 0.5f;

      engineInstances[i].transform.setTranslation(enginePositions[i]);
      renderer.render(engineInstances[i]);
    }
  }

  public boolean handleCollision(Cell cell) {
    Cell.CollisionInfo c = cell.collisionInfo;
    if (c.normal.y == 1) {
      floorCollisions++;

      if (vel.y < -MIN_BOUNCE_VEL) {
        vel.y = -vel.y * BOUNCE_FACTOR;
      }
    }

    dSlide = c.slide;
    if (dSlide != 0) {
      float dx = Math.copySign(1, vel.x);
      float ds = Math.copySign(1, dSlide);
      if (dx == ds || vel.x == 0) {
        c.normal.x = dSlide;
      }
    }
    return true;
  }
}
