package com.habboi.tns.level;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class ShipCamera {
    static final float DISTANCE_Y = 1f;
    static final float DISTANCE_Z = 5f;
    static final float LERP_X = 5;
    static final float LERP_Y = 3;
    static final float LERP_Z = 20;
    static final float LOOK_AT_OFFSET = 2;
    Ship ship;
    PerspectiveCamera[] cams;

    public ShipCamera(Ship ship, PerspectiveCamera[] cams) {
        this.ship = ship;
        this.cams = cams;
        reset();
    }

    public void reset() {
        for (PerspectiveCamera cam : cams) {
            cam.position.set(ship.shape.pos.x, ship.shape.pos.y + DISTANCE_Y, ship.shape.pos.z + DISTANCE_Z);
        }
    }

    private void setCameraPos(Vector3 pos, float dt) {
        final float max = 80;

        float dx = pos.x - cams[0].position.x;
        float dy = pos.y + DISTANCE_Y - cams[0].position.y;
        float dz = pos.z + DISTANCE_Z - cams[0].position.z;

        dx = Math.min(Math.abs(dx), max) * Math.copySign(1, dx);
        dy = Math.min(Math.abs(dy), max) * Math.copySign(1, dy);
        dz = Math.min(Math.abs(dz), max) * Math.copySign(1, dz);

        cams[0].translate(dx * LERP_X * dt, dy * LERP_Y * dt, dz * LERP_Z * dt);

        for (int i = 1; i < cams.length; i++) {
            cams[i].position.set(cams[0].position);
        }
    }

    public void update(float dt) {
        setCameraPos(ship.shape.pos, dt);

        for (PerspectiveCamera cam : cams) {
            cam.lookAt(cam.position.x, cam.position.y, ship.shape.pos.z - LOOK_AT_OFFSET);
            cam.update();
        }
    }
}
