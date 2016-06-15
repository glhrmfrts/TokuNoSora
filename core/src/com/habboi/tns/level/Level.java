package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.Ship;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Manages a level and it's objects.
 */
public class Level {
  static final float GRAVITY = -9.87f;
  static final float BLENDING = 0.25f;

  String name;
  String music;
  int gravityLevel;
  int fuelFactor;
  Vector3 shipPos;
  ArrayList<Color> colors;
  ArrayList<ArrayList<int[]>> presets;

  ModelBuilder mb;
  ArrayList<Model> presetModels = new ArrayList<>();
  ArrayList<Cell> cells = new ArrayList<>();
  LinkedList<Cell> collisions = new LinkedList<>();

  public Level(String name, String music, int gravityLevel, int fuelFactor,
               Vector3 shipPos, ArrayList<Color> colors, ArrayList<ArrayList<int[]>> presets) {
    this.name = name;
    this.music = music;
    this.gravityLevel = gravityLevel;
    this.fuelFactor = fuelFactor;
    this.shipPos = shipPos;
    this.colors = colors;
    this.presets = presets;

    mb = new ModelBuilder();
    createPresetModels();
  }

  public Vector3 getShipPos() {
    return shipPos;
  }

  private void createPresetModels() {
    for (ArrayList<int[]> preset : presets) {
      presetModels.add(createTileModel(preset));
    }
  }

  private Model createTileModel(ArrayList<int[]> colors) {
    MeshPartBuilder partBuilder;
    VertexInfo v1, v2, v3, v4;

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
            new Material());

    v1 = new VertexInfo().setPos(1, 0, -1).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[0]));
    v2 = new VertexInfo().setPos(1, 0, 0).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[1]));
    v3 = new VertexInfo().setPos(0, 0, 0).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[2]));
    v4 = new VertexInfo().setPos(0, 0, -1).setNor(0, -1, 0).setCol(this.colors.get(bottomColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create top part
    partBuilder = mb.part("top", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    v1 = new VertexInfo().setPos(0, 1, -1).setNor(0, 1, 0).setCol(this.colors.get(topColors[0]));
    v2 = new VertexInfo().setPos(0, 1, 0).setNor(0, 1, 0).setCol(this.colors.get(topColors[1]));
    v3 = new VertexInfo().setPos(1, 1, 0).setNor(0, 1, 0).setCol(this.colors.get(topColors[2]));
    v4 = new VertexInfo().setPos(1, 1, -1).setNor(0, 1, 0).setCol(this.colors.get(topColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create left part
    partBuilder = mb.part("left", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    v1 = new VertexInfo().setPos(0, 0, -1).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[0]));
    v2 = new VertexInfo().setPos(0, 0, 0).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[1]));
    v3 = new VertexInfo().setPos(0, 1, 0).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[2]));
    v4 = new VertexInfo().setPos(0, 1, -1).setNor(-1, 0, 0).setCol(this.colors.get(leftColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create right part
    partBuilder = mb.part("right", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    v1 = new VertexInfo().setPos(1, 0, 0).setNor(1, 0, 0).setCol(this.colors.get(rightColors[0]));
    v2 = new VertexInfo().setPos(1, 0, -1).setNor(1, 0, 0).setCol(this.colors.get(rightColors[1]));
    v3 = new VertexInfo().setPos(1, 1, -1).setNor(1, 0, 0).setCol(this.colors.get(rightColors[2]));
    v4 = new VertexInfo().setPos(1, 1, 0).setNor(1, 0, 0).setCol(this.colors.get(rightColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create front part
    partBuilder = mb.part("front", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    v1 = new VertexInfo().setPos(1, 0, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[0]));
    v2 = new VertexInfo().setPos(0, 0, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[1]));
    v3 = new VertexInfo().setPos(0, 1, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[2]));
    v4 = new VertexInfo().setPos(1, 1, -1).setNor(0, 0, -1).setCol(this.colors.get(frontColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    // create back part (visible to player)
    partBuilder = mb.part("back", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
            new Material());

    v1 = new VertexInfo().setPos(0, 0, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[0]));
    v2 = new VertexInfo().setPos(1, 0, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[1]));
    v3 = new VertexInfo().setPos(1, 1, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[2]));
    v4 = new VertexInfo().setPos(0, 1, 0).setNor(0, 0, 1).setCol(this.colors.get(backColors[3]));
    partBuilder.rect(v1, v2, v3, v4);

    return mb.end();
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

  public void dispose() {

  }
}
