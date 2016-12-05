package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * The finish.
 */
public class Finish extends Cell {

    float radius;

    // vectors used in collision detection
    // created here to avoid garbage collection
    Vector3 dif = new Vector3();
    Vector3 closest = new Vector3();
    Vector2 n = new Vector2();

    public Finish(Vector3 pos, float radius) {
        this.pos.set(pos.x*Tile.TILE_WIDTH, pos.y*Tile.TILE_HEIGHT, -pos.z * Tile.TILE_DEPTH);
        this.radius = radius;
        this.effect = TouchEffect.END;
    }

    @Override
    public void reset() {

    }

    @Override
    public void render(GameRenderer renderer) {
    }

    @Override
    public boolean checkCollision(Ship ship) {
        Vector3 spos = ship.pos;
        Vector3 shalf = ship.half;
        float front = spos.z - shalf.z;
        float back = spos.z + shalf.z;

        if (front > pos.z || back < pos.z) {
            return false;
        }

        dif.set(spos).sub(pos);
        closest.set(dif);
        closest.x = Math.max(-shalf.x, Math.min(closest.x, shalf.x));
        closest.y = Math.max(-shalf.y, Math.min(closest.y, shalf.y));

        boolean intersect = false;
        if (dif == closest) {
            intersect = true;
            if (dif.x < dif.y) {
                if (closest.x > 0)
                    closest.x = shalf.x;
                else
                    closest.x = -shalf.x;
            } else {
                if (closest.y > 0)
                    closest.y = shalf.y;
                else
                    closest.y = -shalf.y;
            }
        }

        n.set(dif.x, dif.y).sub(closest.x, closest.y);

        float dsqr = n.len2();
        float rsqr = radius;
        rsqr *= rsqr;

        if (dsqr > rsqr) return false;

        Cell.CollisionInfo c = collisionInfo;
        c.clear();
        float d = (float)Math.sqrt(dsqr);
        c.depth = radius - d;
        if (intersect)
            c.normal.set(-n.x / d, -n.y / d, 0);
        else
            c.normal.set(n.x / d, n.y / d, 0);

        c.slide = c.normal.x;
        return true;
    }
}
