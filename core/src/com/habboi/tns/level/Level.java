package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Models;
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

    for (ArrayList<int[]> preset : presets) {
      Models.createTileModel(colors, preset);
    }
    for (int[] preset : tunnelPresets) {
      Models.createTunnelModel(colors, preset);
    }
  }

  public Vector3 getShipPos() {
    return shipPos;
  }

  public ArrayList<Color> getColors() {
    return colors;
  }

  public void addTile(Vector3 pos, Vector3 size, int outline, Tile.TouchEffect effect, int preset) {
    Color outlineColor = Color.WHITE;
    if (outline != -1) {
      outlineColor = colors.get(outline);
    }
    Tile tile = new Tile(pos, size, outlineColor, effect, preset);
    cells.add(tile);
  }

  public void addTunnel(Vector3 pos, float depth, int preset) {
    Tunnel tunnel = new Tunnel(pos, depth, preset);
    cells.add(tunnel);
  }

  public void addSun(Vector3 pos, float radius) {
    Sun sun = new Sun(pos, radius);
    cells.add(sun);
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
