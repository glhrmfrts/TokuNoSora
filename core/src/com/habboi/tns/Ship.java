package com.habboi.tns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
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
  public Vector3 pos = new Vector3();
  public Vector3 vel = new Vector3();
  public Vector3 half = new Vector3();

  static final float BODY_WIDTH = 1;
  static final float BODY_HEIGHT = 0.5f;
  static final float BODY_DEPTH = 2.5f;
  static final float MAX_VEL = 50;
  static final float STEER_VEL = 12;
  static final float STEER_ACCELERATION = 40;
  static final float JUMP_VEL = 8;
  static final float MIN_BOUNCE_VEL = 0.4f;
  static final float BOUNCE_FACTOR = 0.35f;
  static final Color COLOR = new Color(0xff);
  static final Color OUTLINE_COLOR = new Color(0x1A0E74<<8 | 0xFF);
  static Model bodyModel;
  static Model outlineModel;

  ModelInstance bodyInstance;
  ModelInstance outlineInstance;
  ShipController controller;
  int floorCollisions;
  float dSlide;

  public Ship(Vector3 pos, ShipController controller) {
    half.set(BODY_WIDTH / 2f, BODY_HEIGHT / 2f, BODY_DEPTH / 2f);

    this.pos.set(pos);
    this.pos.z -= half.z;

    this.controller = controller;

    bodyInstance = new ModelInstance(createBodyModel());
    bodyInstance.transform.setToScaling(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH);

    outlineInstance = new ModelInstance(createOutlineModel());
    outlineInstance.transform.setToScaling(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH);

    Renderable r = new Renderable();
    outlineInstance.getRenderable(r);
    ColorAttribute attr = (ColorAttribute) r.material.get(ColorAttribute.Diffuse);
    attr.color.set(OUTLINE_COLOR);
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
      vel.x = steer(vel.x, target, STEER_ACCELERATION, dt);

      if (controller.isJustDown(Key.JUMP)) {
        vel.y = JUMP_VEL;
      } else if (dSlide != 0) {
        vel.y = Math.min(vel.y, 0);
      }
    }

    floorCollisions = 0;
    dSlide = 0;
  }

  public void render(GameRenderer renderer) {
    bodyInstance.transform.setTranslation(pos);
    renderer.render(bodyInstance);

    outlineInstance.transform.setTranslation(pos);
    renderer.renderGlow(outlineInstance);
  }

  public boolean handleCollision(Cell cell) {
    Cell.CollisionInfo c = cell.collisionInfo;
    //System.out.println(c);

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

  private static Model createBodyModel() {
    if (bodyModel != null) return bodyModel;

    MeshPartBuilder partBuilder;
    VertexInfo v1, v2, v3, v4;
    Vector3 normal;

    final float front = 0.125f;
    final Material material = new Material(new BlendingAttribute(0.75f));

    ModelBuilder mb = new ModelBuilder();
    mb.begin();

    // create bottom part
    partBuilder = mb.part("bottom", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(0.125f, -0.5f, -0.5f);
    v2 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f);
    v3 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f);
    v4 = new VertexInfo().setPos(-0.125f, -0.5f, -0.5f);

    normal = Util.calculateNormal(v1.position, v2.position, v3.position);
    v1.setNor(normal);
    v2.setNor(normal);
    v3.setNor(normal);
    v4.setNor(normal);

    partBuilder.rect(v1, v2, v3, v4);

    // create top part
    partBuilder = mb.part("top", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(-0.125f, front, -0.5f);
    v2 = new VertexInfo().setPos(-0.25f, 0.5f, 0.5f);
    v3 = new VertexInfo().setPos(0.25f, 0.5f, 0.5f);
    v4 = new VertexInfo().setPos(0.125f, front, -0.5f);

    normal = Util.calculateNormal(v1.position, v2.position, v3.position);
    v1.setNor(normal);
    v2.setNor(normal);
    v3.setNor(normal);
    v4.setNor(normal);

    partBuilder.rect(v1, v2, v3, v4);

    // create left part
    partBuilder = mb.part("left", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(-0.125f, -0.5f, -0.5f).setNor(-1, 0, 0);
    v2 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f).setNor(-1, 0, 0);
    v3 = new VertexInfo().setPos(-0.25f, 0.5f, 0.5f).setNor(-1, 0, 0);
    v4 = new VertexInfo().setPos(-0.125f, front, -0.5f).setNor(-1, 0, 0);
    partBuilder.rect(v1, v2, v3, v4);

    // create right part
    partBuilder = mb.part("right", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f).setNor(1, 0, 0);
    v2 = new VertexInfo().setPos(0.125f, -0.5f, -0.5f).setNor(1, 0, 0);
    v3 = new VertexInfo().setPos(0.125f, front, -0.5f).setNor(1, 0, 0);
    v4 = new VertexInfo().setPos(0.25f, 0.5f, 0.5f).setNor(1, 0, 0);
    partBuilder.rect(v1, v2, v3, v4);

    // create front part
    partBuilder = mb.part("front", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(0.125f, -0.5f, -0.5f).setNor(0, 0, -1);
    v2 = new VertexInfo().setPos(-0.125f, -0.5f, -0.5f).setNor(0, 0, -1);
    v3 = new VertexInfo().setPos(-0.125f, front, -0.5f).setNor(0, 0, -1);
    v4 = new VertexInfo().setPos(0.125f, front, -0.5f).setNor(0, 0, -1);
    partBuilder.rect(v1, v2, v3, v4);

    // create back part (visible to player)
    partBuilder = mb.part("back", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    partBuilder.setColor(COLOR);
    v1 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f).setNor(0, 0, 1);
    v2 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f).setNor(0, 0, 1);
    v3 = new VertexInfo().setPos(0.25f, 0.5f, 0.5f).setNor(0, 0, 1);
    v4 = new VertexInfo().setPos(-0.25f, 0.5f, 0.5f).setNor(0, 0, 1);
    partBuilder.rect(v1, v2, v3, v4);

    return bodyModel = mb.end();
  }

  private static Model createOutlineModel() {
    if (outlineModel != null) return outlineModel;

    MeshPartBuilder partBuilder;
    final float front = 0.125f;
    final Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));

    ModelBuilder mb = new ModelBuilder();
    mb.begin();

    // create bottom part
    partBuilder = mb.part("bottom", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(0.125f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f);
    partBuilder.line(0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f);
    partBuilder.line(-0.5f, -0.5f, 0.5f, -0.125f, -0.5f, -0.5f);
    partBuilder.line(-0.125f, -0.5f, -0.5f, 0.125f, -0.5f, -0.5f);

    // create top part
    partBuilder = mb.part("top", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(-0.125f, front, -0.5f, -0.25f, 0.5f, 0.5f);
    partBuilder.line(-0.25f, 0.5f, 0.5f, 0.25f, 0.5f, 0.5f);
    partBuilder.line(0.25f, 0.5f, 0.5f, 0.125f, front, -0.5f);
    partBuilder.line(0.125f, front, -0.5f, -0.125f, front, -0.5f);

    // create left part
    partBuilder = mb.part("left", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(-0.125f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f);
    partBuilder.line(-0.5f, -0.5f, 0.5f, -0.25f, 0.5f, 0.5f);
    partBuilder.line(-0.25f, 0.5f, 0.5f, -0.125f, front, -0.5f);
    partBuilder.line(-0.125f, front, -0.5f, -0.125f, -0.5f, -0.5f);

    // create right part
    partBuilder = mb.part("right", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(0.5f, -0.5f, 0.5f, 0.125f, -0.5f, -0.5f);
    partBuilder.line(0.125f, -0.5f, -0.5f, 0.125f, front, -0.5f);
    partBuilder.line(0.125f, front, -0.5f, 0.25f, 0.5f, 0.5f);
    partBuilder.line(0.25f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f);

    // create front part
    partBuilder = mb.part("front", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(0.125f, -0.5f, -0.5f, -0.125f, -0.5f, -0.5f);
    partBuilder.line(-0.125f, -0.5f, -0.5f, -0.125f, front, -0.5f);
    partBuilder.line(-0.125f, front, -0.5f, 0.125f, front, -0.5f);
    partBuilder.line(0.125f, front, -0.5f, 0.125f, -0.5f, -0.5f);

    // create back part (visible to player)
    partBuilder = mb.part("back", GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            material);
    partBuilder.setColor(Color.WHITE);
    partBuilder.line(-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f);
    partBuilder.line(0.5f, -0.5f, 0.5f, 0.25f, 0.5f, 0.5f);
    partBuilder.line(0.25f, 0.5f, 0.5f, -0.25f, 0.5f, 0.5f);
    partBuilder.line(-0.25f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f);

    return outlineModel = mb.end();
  }
}
