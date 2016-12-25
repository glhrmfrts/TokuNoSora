package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Manages a level and it's objects.
 */
public class Level {
    static final float GRAVITY = -9.87f;

    String name;
    int number;
    World world;
    float centerX;
    Vector3 shipPos;

    ArrayList<Cell> cells = new ArrayList<>();
    LinkedList<Cell> collisions = new LinkedList<>();
    ArrayList<GenericObject> decorations = new ArrayList<>();
    ArrayList<Tunnel> endTunnels = new ArrayList<>();

    public Level(String name, int number, int worldIndex, float centerX, Vector3 shipPos) {
        this.name = name;
        this.number = number;
        this.shipPos = shipPos;
        this.centerX = centerX;
        this.world = Universe.get().worlds.get(worldIndex);
    }

    public String getName() {
        return name;
    }

    public Vector3 getShipPos() {
        return shipPos;
    }

    public World getWorld() {
        return world;
    }

    public void addArrows(Vector3 pos, Vector3 rotation, float height, int depth, int color) {
        decorations.add(new Arrows(pos, rotation, height, depth, color, world));
    }

    public void addTile(Vector3 pos, Vector3 size, int preset, TouchEffect effect) {
        cells.add(new Tile(pos, size, preset, effect, world));
    }

    public void addTunnel(Vector3 pos, float depth, int preset, boolean end) {
        Tunnel tunnel = new Tunnel(pos, depth, preset, world);
        if (end) {
            endTunnels.add(tunnel);
        }
        cells.add(tunnel);
    }

    public void addTileWithTunnels(Vector3 pos, Vector3 size, int preset, int[] tunnels, TouchEffect effect) {
        cells.add(new TileWithTunnels(pos, size, preset, tunnels, effect, world));
    }

    public void addFinish(Vector3 pos, float radius) {
        Finish finish = new Finish(pos, radius);
        cells.add(finish);
    }

    private void updatePhysics(Ship ship, float dt) {
        if (ship.state != Ship.State.ENDED) {
            ship.vel.y += (GRAVITY * world.gravityFactor) * dt;
        }
        for (Cell cell : cells) {
            cell.update(dt);

            if (cell.getShape().checkCollision(ship.shape)) {
                collisions.add(cell);
            }
        }

        Cell cell;
        while ((cell = collisions.pollFirst()) != null) {
            if (ship.handleCollision(cell)) {
                Shape.CollisionInfo c = cell.getShape().getCollisionInfo();

                float velNormal = ship.vel.dot(c.normal);
                if (velNormal > 0.0f) continue;

                float j = -1 * velNormal;
                Vector3 impulse = new Vector3(c.normal.x * j, c.normal.y * j, c.normal.z * j);
                ship.vel.add(impulse);

                float s = Math.max(c.depth - 0.01f, 0.0f);
                Vector3 correction = new Vector3(c.normal.x * s, c.normal.y * s, c.normal.z * s);
                ship.shape.pos.add(correction);
            }
        }
        ship.shape.pos.add(ship.vel.x * dt, ship.vel.y * dt, ship.vel.z * dt);
    }

    public void reset() {
        for (Cell c : cells) {
            c.reset();
        }
    }

    public void update(Ship ship, float dt) {
        updatePhysics(ship, dt);

        world.setCenterX(this.centerX);
        world.update(ship.shape.pos, -ship.vel.z, dt);

        boolean readyToEnd = ship.readyToEnd;
        for (Tunnel t : endTunnels) {
            if (t.isShipInside()) {
                readyToEnd = true;
                break;
            } else {
                readyToEnd = false;
            }
        }
        ship.readyToEnd = readyToEnd;

        for (GenericObject decoration : decorations) {
            decoration.update(dt);
        }
    }

    public void render(GameRenderer renderer, int pass) {
        for (Cell cell : cells) {
            cell.render(renderer, pass);
        }

        for (GenericObject decoration : decorations) {
            decoration.render(renderer, pass);
        }
    }

    public void dispose() {

    }
}
