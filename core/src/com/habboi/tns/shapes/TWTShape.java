package com.habboi.tns.shapes;

import com.badlogic.gdx.math.Vector3;

public class TWTShape implements Shape {

    public Vector3 pos = new Vector3();

    Shape.CollisionInfo collisionInfo = new CollisionInfo();

    public TWTShape(Vector3 pos, Vector3 size, int[] tunnels) {
        this.pos.set(pos);
    }

    @Override
    public boolean checkCollision(Shape abstractShape) {
        return false;
    }

    @Override
    public Shape.CollisionInfo getCollisionInfo() {
        return collisionInfo;
    }
}
