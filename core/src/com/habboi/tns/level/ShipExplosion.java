package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.utils.EventEmitter;
import com.habboi.tns.utils.Models;

import java.util.ArrayList;
import java.util.Random;

public class ShipExplosion extends LevelObject {
    static final int TRIANGLE_COUNT = 12;
    static final float TIME = 1f;
    static final float MIN_VEL = 2f;
    static final float MAX_VEL = 10f;
    static final float MIN_ROT = 1f;
    static final float MAX_ROT = 90f;
    static final Random rand = new Random();

    public static class Triangle extends Fragment {
        public Vector3 pos = new Vector3();
        public Vector3 vel = new Vector3();

        public Triangle(ModelInstance modelInstance) {
            super(modelInstance);
        }
    }

    public float time;
    public boolean isExploding;
    public Vector3 origin;
    public ArrayList<Triangle> triangles = new ArrayList<>();

    public ShipExplosion() {
        for (int i = 0; i < TRIANGLE_COUNT; i++) {
            Triangle triangle = new Triangle(new ModelInstance(Models.getTriangleModel()));
            triangle.visible(false);

            triangle.modelInstance.transform.setToScaling(0.25f, 0.25f, 0.25f);

            Models.setColor(triangle.modelInstance, ColorAttribute.Diffuse, Ship.COLOR);

            triangles.add(triangle);
        }
    }

    @Override
    public void addToScene(Scene scene) {
        for (Triangle t : triangles) {
            scene.add(t);
        }
    }

    private float randomVel() {
        int r = rand.nextInt(2);
        r = r == 1 ? 1 : -1;
        return randomVel((float)r);
    }

    private float randomVel(float sign) {
        return sign * (MIN_VEL + rand.nextFloat() * MAX_VEL);
    }

    private float randomRot() {
        return MIN_ROT + rand.nextFloat() * MAX_ROT;
    }

    public void explode(Vector3 pos) {
        isExploding = true;
        time = 0;
        origin = pos;

        for (Triangle t : triangles) {
            t.visible(true);

            t.pos.set(pos.x, pos.y, pos.z);
            t.vel.set(randomVel(), randomVel(1), randomVel());
            t.modelInstance.transform.rotate(Vector3.X, randomRot());
            t.modelInstance.transform.rotate(Vector3.Y, randomRot());
            t.modelInstance.transform.rotate(Vector3.Z, randomRot());
        }

        System.out.println("explosion " + pos);
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(float dt) {
        if (!isExploding)
            return;
   
        for (Triangle t : triangles) {
            t.pos.x += t.vel.x * dt;
            t.pos.y += t.vel.y * dt;
            t.pos.z -= t.vel.z * dt;

            t.modelInstance.transform.setTranslation(t.pos);
        }

        time += dt;
        if (time >= TIME) {
            time = 0;
            isExploding = false;
            for (Triangle t : triangles) {
                t.visible(false);
            }

            System.out.println("explosionEnd");
            EventEmitter.get().notify("ship_explosion_end", null);
        }
    }
}