package com.habboi.tns.shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TunnelShape implements Shape {
    public static final float TUNNEL_WIDTH = TileShape.TILE_WIDTH;
    public static final float TUNNEL_HEIGHT = 1;
    public static final float TUNNEL_DEPTH = TileShape.TILE_DEPTH;

    public Vector3 pos = new Vector3();
    public boolean isInside;

    Shape.CollisionInfo collisionInfo = new CollisionInfo();
    float depth;
    Vector3 closest = new Vector3();
    Vector3 dif = new Vector3();
    Vector3 half = new Vector3();
    Vector2 n = new Vector2();

    public TunnelShape(Vector3 pos, float depth) {
        this.depth = depth;

        this.pos.set(pos);
        this.half.set(TUNNEL_WIDTH/2, TUNNEL_HEIGHT/2, depth*TUNNEL_DEPTH/2);
    }

    @Override
    public boolean checkCollision(Shape abstractShape) {
        if (!(abstractShape instanceof TileShape)) {
            return false;
        }

        TileShape shape = (TileShape)abstractShape;
        Vector3 spos = shape.pos;
        Vector3 shalf = shape.half;
        final float thickness = 0.1f;
        final float radius = half.x+0.011f;

        float rightEdge = pos.x + radius;
        float leftEdge = pos.x - radius;
        float upperEdge = pos.y + radius;
        float downEdge = pos.y - radius;
        float frontEdge = pos.z - half.z;
        float backEdge = pos.z + half.z;

        boolean insideX = spos.x + (shalf.x) < rightEdge && spos.x - (shalf.x) > leftEdge;
        boolean insideY = spos.y < upperEdge && spos.y > downEdge;
        boolean insideZ = spos.z > frontEdge && spos.z < backEdge;

        Shape.CollisionInfo c = collisionInfo;
        c.clear();
        isInside = false;
        if (insideY) {
            if (insideZ && insideX) {
                float cos = (spos.x - pos.x) / (rightEdge - thickness - pos.x);
                float sin = (spos.y - pos.y) / (upperEdge - thickness - pos.y);
                isInside = true;

                if (Math.abs(cos) > 0.12f || sin > 0.4f) {
                    c.normal.x = -Math.copySign(1, cos);
                    c.normal.y = (sin > 0.4f) ? -1 : 0;

                    if (cos < 0)
                        c.depth = leftEdge - (spos.x - shalf.x);
                    else
                        c.depth = spos.x + shalf.x - rightEdge;
                    return true;
                }
            } else if (!insideX) {
                // possible frontal collision
                dif.set(spos).sub(pos);
                if (dif.z > half.z) {
                    float dx = half.x + shalf.x - Math.abs(dif.x);
                    if (dx <= 0.0f) return false;

                    float dz = half.z + shalf.z - Math.abs(dif.z);
                    if (dz <= 0.0f) return false;

                    if (dx < dz) {
                        c.normal.x = Math.copySign(1, dif.x);
                        c.depth = dx;
                    } else {
                        c.normal.z = Math.copySign(1, dif.z);
                        c.depth = dz;
                    }
                    return true;
                }
            }
        }

        if (insideZ && !isInside) {
            // since we know we are inside the tunnel's z range we simply
            // discard the z axis in this collision detection
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

            float d = (float)Math.sqrt(dsqr);
            c.depth = radius - d;
            if (intersect)
                c.normal.set(-n.x / d, -n.y / d, 0);
            else
                c.normal.set(n.x / d, n.y / d, 0);

            c.slide = c.normal.x;
            return true;
        }

        return false;
    }

    @Override
    public Shape.CollisionInfo getCollisionInfo() {
        return collisionInfo;
    }
}
