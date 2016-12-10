package com.habboi.tns.shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class FinishShape implements Shape {

    public Vector3 pos = new Vector3();

    Vector3 dif = new Vector3();
    Vector3 closest = new Vector3();
    Vector2 n = new Vector2();
    float radius;

    Shape.CollisionInfo collisionInfo = new CollisionInfo();

    public FinishShape(Vector3 pos, float radius) {
        this.pos.set(pos);
        this.radius = radius;
    }

    @Override
    public boolean checkCollision(Shape abstractShape) {
        if (!(abstractShape instanceof TileShape)) {
            return false;
        }

        TileShape shape = (TileShape)abstractShape;
        Vector3 spos = shape.pos;
        Vector3 shalf = shape.half;
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

        Shape.CollisionInfo c = collisionInfo;
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

    @Override
    public Shape.CollisionInfo getCollisionInfo() {
        return collisionInfo;
    }
}
