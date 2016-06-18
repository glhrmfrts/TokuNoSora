package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Util;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.Ship;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Manages a level and it's objects.
 */
public class Level {
  static final float GRAVITY = -9.87f;

  String name;
  String music;
  int gravityLevel;
  int fuelFactor;
  Vector3 shipPos;
  ArrayList<Color> colors;

  ModelBuilder mb;
  ArrayList<Model> presetModels = new ArrayList<>();
  ArrayList<Model> tunnelModels = new ArrayList<>();
  ArrayList<Cell> cells = new ArrayList<>();
  LinkedList<Cell> collisions = new LinkedList<>();

  public Level(String name, String music, int gravityLevel, int fuelFactor,
               Vector3 shipPos, ArrayList<Color> colors, ArrayList<ArrayList<int[]>> presets,
               ArrayList<int[]> tunnelPresets) {
    this.name = name;
    this.music = music;
    this.gravityLevel = gravityLevel;
    this.fuelFactor = fuelFactor;
    this.shipPos = shipPos;
    this.colors = colors;

    mb = new ModelBuilder();
    for (ArrayList<int[]> preset : presets) {
      presetModels.add(createTileModel(preset));
    }
    for (int[] preset : tunnelPresets) {
      tunnelModels.add(createTunnelModel(preset));
    }
  }

  public Vector3 getShipPos() {
    return shipPos;
  }

  public ArrayList<Color> getColors() {
    return colors;
  }

  public void addTile(Vector3 pos, Vector3 size, int outline, Tile.TouchEffect effect, int preset) {
    Model model = presetModels.get(preset);
    Color outlineColor = Color.WHITE;
    if (outline != -1) {
      outlineColor = colors.get(outline);
    }
    Tile tile = new Tile(pos, size, outlineColor, effect, model);
    cells.add(tile);
  }

  public void addTunnel(Vector3 pos, float depth, int preset) {
    Tunnel tunnel = new Tunnel(pos, depth, tunnelModels.get(preset));
    cells.add(tunnel);
  }

  public void addSun(Vector3 pos, Vector3 size) {

  }

  private void updatePhysics(Ship ship, float dt) {
    ship.vel.y += (GRAVITY * gravityLevel) * dt;

    for (Cell cell : cells) {
      if (cell.checkCollision(ship)) {
        collisions.add(cell);
      }
    }

    Cell cell;
    while ((cell = collisions.pollFirst()) != null) {
      if (ship.handleCollision(cell)) {
        Cell.CollisionInfo c = cell.collisionInfo;

        float velNormal = ship.vel.dot(c.normal);
        if (velNormal > 0.0f) continue;

        float j = -1 * velNormal;
        Vector3 impulse = new Vector3(c.normal.x * j, c.normal.y * j, c.normal.z * j);
        ship.vel.add(impulse);

        float s = Math.max(c.depth - 0.05f, 0.0f) * 0.9f;
        Vector3 correction = new Vector3(c.normal.x * s, c.normal.y * s, c.normal.z * s);
        ship.pos.add(correction);
      }
    }

    ship.pos.add(ship.vel.x * dt, ship.vel.y * dt, ship.vel.z * dt);
  }

  public void update(Ship ship, float dt) {
    updatePhysics(ship, dt);
  }

  public void render(GameRenderer renderer) {
    for (Cell cell : cells) {
      cell.render(renderer);
    }
  }

  private Model createTileModel(ArrayList<int[]> colors) {
    MeshPartBuilder partBuilder;
    VertexInfo v1, v2, v3, v4;
    final Material material = new Material(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.75f));

    int[] bottomColors = colors.get(0);
    int[] topColors = colors.get(1);
    int[] leftColors = colors.get(2);
    int[] rightColors = colors.get(3);
    int[] frontColors = colors.get(4);
    int[] backColors = colors.get(5);

    mb.begin();

    // create bottom part
    partBuilder = mb.part("bottom", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    v1 = new VertexInfo().setPos(1, 0, -1).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[0]));
    v2 = new VertexInfo().setPos(1, 0, 0).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[1]));
    v3 = new VertexInfo().setPos(0, 0, 0).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[2]));
    v4 = new VertexInfo().setPos(0, 0, -1).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create top part
    partBuilder = mb.part("top", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    v1 = new VertexInfo().setPos(0, 1, -1).setNor(0, 1, 0).setCol(this.colors.get(topColors[0]));
    v2 = new VertexInfo().setPos(0, 1, 0).setNor(0, 1, 0).setCol(this.colors.get(topColors[1]));
    v3 = new VertexInfo().setPos(1, 1, 0).setNor(0, 1, 0).setCol(this.colors.get(topColors[2]));
    v4 = new VertexInfo().setPos(1, 1, -1).setNor(0, 1, 0).setCol(this.colors.get(topColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create left part
    partBuilder = mb.part("left", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    v1 = new VertexInfo().setPos(0, 0, -1).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[0]));
    v2 = new VertexInfo().setPos(0, 0, 0).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[1]));
    v3 = new VertexInfo().setPos(0, 1, 0).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[2]));
    v4 = new VertexInfo().setPos(0, 1, -1).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create right part
    partBuilder = mb.part("right", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    v1 = new VertexInfo().setPos(1, 0, 0).setNor(1, 0, 0).setCol(this.colors.get(rightColors[0]));
    v2 = new VertexInfo().setPos(1, 0, -1).setNor(1, 0, 0).setCol(this.colors.get(rightColors[1]));
    v3 = new VertexInfo().setPos(1, 1, -1).setNor(1, 0, 0).setCol(this.colors.get(rightColors[2]));
    v4 = new VertexInfo().setPos(1, 1, 0).setNor(1, 0, 0).setCol(this.colors.get(rightColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create front part
    partBuilder = mb.part("front", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    v1 = new VertexInfo().setPos(1, 0, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[0]));
    v2 = new VertexInfo().setPos(0, 0, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[1]));
    v3 = new VertexInfo().setPos(0, 1, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[2]));
    v4 = new VertexInfo().setPos(1, 1, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create back part (visible to player)
    partBuilder = mb.part("back", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            material);

    v1 = new VertexInfo().setPos(0, 0, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[0]));
    v2 = new VertexInfo().setPos(1, 0, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[1]));
    v3 = new VertexInfo().setPos(1, 1, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[2]));
    v4 = new VertexInfo().setPos(0, 1, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    return mb.end();
  }

  private void adjustTunnelVertexScale(VertexInfo v1) {
    v1.position.x *= 0.5f;
    v1.position.y -= 0.5f;
    v1.position.y *= 0.5f;
    v1.position.z *= 0.5f;
  }

  private void adjustTunnelVertexScale(VertexInfo... vs) {
    for (VertexInfo v : vs) {
      adjustTunnelVertexScale(v);
    }
  }

  private Model createTunnelModel(int[] cols) {
    MeshPartBuilder partBuilder;
    VertexInfo v1, v2, v3, v4;
    mb.begin();

    final Material material = new Material(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.75f));
    final int segments = 12;
    final float deltaTheta = (float)Math.PI / segments;
    final float backZ = 1;
    final float frontZ = -1;

    float theta = 0;
    float x = 1;
    float y = -0.5f;
    for (int i = 0; i < segments; i++)
    {
      theta += deltaTheta;
      float nx = (float)Math.cos(theta);
      float ny = (float)Math.sin(theta);
      float ix = x * 0.9f;
      float iy = y * 0.9f;
      float inx = nx * 0.9f;
      float iny = ny * 0.9f;

      // create outside part of this segment
      partBuilder = mb.part("outside" + i, GL20.GL_TRIANGLES,
              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
              material);

      // TODO: temp
      partBuilder.setColor(Color.WHITE);

      v1 = new VertexInfo().setPos(x, y, backZ);
      v2 = new VertexInfo().setPos(x, y, frontZ);
      v3 = new VertexInfo().setPos(nx, ny, frontZ);
      v4 = new VertexInfo().setPos(nx, ny, backZ);
      v1.setNor(v1.position.x, v1.position.y, 0);
      v2.setNor(v2.position.x, v2.position.y, 0);
      v3.setNor(v3.position.x, v3.position.y, 0);
      v4.setNor(v4.position.x, v4.position.y, 0);
      adjustTunnelVertexScale(v1, v2, v3, v4);
      partBuilder.rect(v1, v2, v3, v4);

      // create back part of this segment (visible to player)
      partBuilder = mb.part("back" + i, GL20.GL_TRIANGLES,
              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
              material);

      // TODO: temp
      partBuilder.setColor(Color.WHITE);

      v1 = new VertexInfo().setPos(ix, iy, backZ);
      v2 = new VertexInfo().setPos(inx, iny, backZ);
      v3 = new VertexInfo().setPos(nx, ny, backZ);
      v4 = new VertexInfo().setPos(x, y, backZ);
      v1.setNor(0, 0, 1);
      v2.setNor(0, 0, 1);
      v3.setNor(0, 0, 1);
      v4.setNor(0, 0, 1);
      adjustTunnelVertexScale(v1, v2, v3, v4);
      partBuilder.rect(v1, v2, v3, v4);

      // create inside part of this segment
      partBuilder = mb.part("inside" + i, GL20.GL_TRIANGLES,
              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
              material);

      // TODO: temp
      partBuilder.setColor(Color.WHITE);

      v1 = new VertexInfo().setPos(ix, iy, backZ);
      v2 = new VertexInfo().setPos(ix, iy, frontZ);
      v3 = new VertexInfo().setPos(inx, iny, frontZ);
      v4 = new VertexInfo().setPos(inx, iny, backZ);
      v1.setNor(-v1.position.x, -v1.position.y, 0);
      v2.setNor(-v2.position.x, -v2.position.y, 0);
      v3.setNor(-v3.position.x, -v3.position.y, 0);
      v4.setNor(-v4.position.x, -v4.position.y, 0);
      adjustTunnelVertexScale(v1, v2, v3, v4);
      partBuilder.rect(v1, v2, v3, v4);

      // create front part of this segment
      partBuilder = mb.part("front" + i, GL20.GL_TRIANGLES,
              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
              material);

      // TODO: temp
      partBuilder.setColor(Color.WHITE);

      v1 = new VertexInfo().setPos(ix, iy, frontZ);
      v2 = new VertexInfo().setPos(inx, iny, frontZ);
      v3 = new VertexInfo().setPos(nx, ny, frontZ);
      v4 = new VertexInfo().setPos(x, y, frontZ);
      v1.setNor(0, 0, -1);
      v2.setNor(0, 0, -1);
      v3.setNor(0, 0, -1);
      v4.setNor(0, 0, -1);
      adjustTunnelVertexScale(v1, v2, v3, v4);
      partBuilder.rect(v1, v2, v3, v4);

      x = nx;
      y = ny;
    }

    return mb.end();
  }

  public void dispose() {

  }
}
