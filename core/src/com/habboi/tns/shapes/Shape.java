package com.habboi.tns.shapes;

import com.badlogic.gdx.math.Vector3;

public interface Shape {

    public static class CollisionInfo {
        public Vector3 normal = new Vector3();
        public float depth;
        public float slide;

        public void clear() {
            normal.set(0, 0, 0);
            depth = 0;
            slide = 0;
        }

        public String toString() {
            String result = "{" + normal;
            result += ", " + depth + ", " + slide + "}";
            return result;
        }
    }

    boolean checkCollision(Shape shape);
    CollisionInfo getCollisionInfo();
}
