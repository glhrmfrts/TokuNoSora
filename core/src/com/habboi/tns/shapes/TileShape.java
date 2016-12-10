package com.habboi.tns.shapes;

import com.badlogic.gdx.math.Vector3;

public class TileShape implements Shape {
    public static final float TILE_WIDTH = 1;
    public static final float TILE_HEIGHT = 0.25f;
    public static final float TILE_DEPTH = 1;
    public static final float SLIDE_DISTANCE_MIN = 1.05f;

    public Vector3 dif = new Vector3();
    public Vector3 half = new Vector3();
    public Vector3 pos = new Vector3();

    Shape.CollisionInfo collisionInfo = new CollisionInfo();

    public TileShape(Vector3 pos, Vector3 size) {
        this.pos.set(pos);
        this.half.set(size.x / 2, size.y / 2, size.z / 2);
    }

    @Override
    public boolean checkCollision(Shape abstractShape) {
        if (!(abstractShape instanceof TileShape)) {
            return false;
        }

        TileShape shape = (TileShape)abstractShape;
        Vector3 dif = new Vector3(shape.pos).sub(pos);

        float dx = half.x + shape.half.x - Math.abs(dif.x);
        if (dx <= 0) {
            return false;
        }

        float dy = half.y + shape.half.y - Math.abs(dif.y);
        if (dy <= 0) {
            return false;
        }

        float dz = half.z + shape.half.z - Math.abs(dif.z);
        if (dz <= 0) {
            return false;
        }

        Shape.CollisionInfo c = collisionInfo;
        c.clear();
        if (dx < dy && dx < dz && dz > 1 && dy > 0.1f) {
            c.normal.x = Math.copySign(1, dif.x);
            c.depth = dx;
        } else if (dy < dz && dz > 1 || dy < 0.1f) {
            c.normal.y = Math.copySign(1, dif.y);
            c.depth = dy;

            float slide = Math.abs(dif.x)/half.x;
            if (slide > SLIDE_DISTANCE_MIN && c.normal.y == 1) {
                c.slide = slide * Math.copySign(1, dif.x) * 2;
            }
        } else {
            c.normal.z = Math.copySign(1, dif.z);
            c.depth = dz;
        }
        return true;
    }

    @Override
    public Shape.CollisionInfo getCollisionInfo() {
        return collisionInfo;
    }
}
