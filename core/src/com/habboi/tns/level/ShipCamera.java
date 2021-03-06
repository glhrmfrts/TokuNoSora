package com.habboi.tns.level;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class ShipCamera {
    static final float DISTANCE_Y = 0.75f;
    static final float DISTANCE_Z = 3.25f;
    static final float LERP_X = 5;
    static final float LERP_Y = 3;
    static final float LERP_Z = 20;
    static final float LOOK_AT_OFFSET = 2;
    Ship ship;
    PerspectiveCamera cam;
    float distanceY;

    public ShipCamera(Ship ship, PerspectiveCamera cam) {
        this.ship = ship;
        this.cam = cam;
        reset();
    }

    public void reset() {
        distanceY = DISTANCE_Y;
        cam.position.set(ship.shape.pos.x, ship.shape.pos.y + distanceY, ship.shape.pos.z + DISTANCE_Z);
    }

    private void setCameraPos(Vector3 pos, float dt) {
        final float max = 80;

        float dx = pos.x - cam.position.x;
        float dy = pos.y + distanceY - cam.position.y;
        float dz = pos.z + DISTANCE_Z - cam.position.z;

        dx = Math.min(Math.abs(dx), max) * Math.copySign(1, dx);
        dy = Math.min(Math.abs(dy), max) * Math.copySign(1, dy);
        dz = Math.min(Math.abs(dz), max) * Math.copySign(1, dz);

        cam.translate(dx * LERP_X * dt, dy * LERP_Y * dt, dz * LERP_Z * dt);
    }

    public void update(float dt) {
        setCameraPos(ship.shape.pos, dt);
        cam.lookAt(cam.position.x, cam.position.y, ship.shape.pos.z - LOOK_AT_OFFSET);
        cam.update();
    }
}
