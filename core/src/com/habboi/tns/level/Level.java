package com.habboi.tns.level;

import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;

import java.util.ArrayList;
import java.util.LinkedList;


public class Level {
    static final float GRAVITY = -9.87f;

    String name;
    int number;
    int worldIndex;
    World world;
    float oxygenFactor;
    public float centerX;
    Vector3 shipPos = new Vector3();

    Ship ship;
    ArrayList<LevelObject> objects = new ArrayList<>();
    LinkedList<LevelObject> collisions = new LinkedList<>();
    ArrayList<Tunnel> endTunnels = new ArrayList<>();

    public Level() {}

    public Level(String name, int number, int worldIndex, float oxygenFactor, float centerX, Vector3 shipPos) {
        this.name = name;
        this.number = number;
        this.worldIndex = worldIndex;
        this.oxygenFactor = oxygenFactor;
        this.shipPos.set(shipPos);
        this.centerX = centerX;
        this.world = Universe.get().worlds.get(worldIndex);
    }

    public void addArrows(Vector3 pos, Vector3 rotation, Vector3 movement, float height, int depth, int color) {
        objects.add(new Arrows(pos, rotation, movement, height, depth, color, world));
    }

    public void addCollectable(Vector3 pos) {
        objects.add(new Collectible(pos, world));
    }

    public void addTile(Vector3 pos, Vector3 size, int preset, TouchEffect effect) {
        objects.add(new Tile(pos, size, preset, effect, world));
    }

    public void addTunnel(Vector3 pos, float depth, int preset, boolean end) {
        Tunnel tunnel = new Tunnel(pos, depth, preset, world);
        if (end) {
            endTunnels.add(tunnel);
        }
        objects.add(tunnel);
    }

    public void addTileWithTunnels(Vector3 pos, Vector3 size, int preset, int[] tunnels, TouchEffect effect) {
        objects.add(new TileWithTunnels(pos, size, preset, tunnels, effect, world));
    }

    public void addFinish(Vector3 pos, float radius) {
        Finish finish = new Finish(pos, radius);
        objects.add(finish);
    }

    public String getName() {
        return name;
    }

    public ArrayList<LevelObject> getObjects() {
      return objects;
    }

    public Ship getShip() {
      return ship;
    }

    public Vector3 getShipPos() {
        return shipPos;
    }

    public World getWorld() {
        return world;
    }

    public void setShip(Ship ship) {
      this.ship = ship;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    private void updatePhysics(float dt) {
        if (ship.state != Ship.State.ENDED) {
            ship.vel.y += (GRAVITY * world.gravityFactor) * dt;
        }
        for (LevelObject obj : objects) {
            obj.update(dt);

            if (obj.shape != null && obj.shape.checkCollision(ship.shape)) {
                collisions.add(obj);
            }
        }

        LevelObject obj;
        while ((obj = collisions.pollFirst()) != null) {
            if (obj instanceof Collectible) {
                Collectible c = (Collectible)obj;
                if (!c.collected) {
                    ship.onCollect(c);
                    c.collected = true;
                    c.visible = false;
                }
                continue;
            }

            if (ship.onCollision(obj)) {
                Shape.CollisionInfo c = obj.shape.getCollisionInfo();

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
        for (LevelObject obj : objects) {
            obj.reset();
        }
    }

    public void update(float dt) {
        updatePhysics(dt);

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
        ship.oxygenLevel -= oxygenFactor * dt;
    }

    public void dispose() {

    }
}
