package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.utils.Models;

import java.util.Random;

public abstract class Explosion extends LevelObject {
    static final float TIME = 1f;
    static final Random rand = new Random();

    public float minVel = 2f;
    public float maxVel = 10f;
    public float minRot = 1f;
    public float maxRot = 90f;
    public float gravity;
    public float time;
    public boolean isExploding;
    public Vector3 origin;
    public Array<Piece> pieces = new Array<>();

    public Explosion(int count, Model model) {
        for (int i = 0; i < count; i++) {
            Piece p = new Piece(new ModelInstance(model));
            p.visible(false);

            pieces.add(p);
        }
    }

    @Override
    public void addToScene(Scene scene) {
        for (Piece p : pieces) {
            scene.add(p);
        }
    }

    public float randomVel() {
        int r = rand.nextInt(2);
        r = r == 1 ? 1 : -1;
        return randomVel((float)r);
    }

    public float randomVel(float sign) {
        return sign * (minVel + rand.nextFloat() * maxVel);
    }

    public float randomRot() {
        return minRot + rand.nextFloat() * maxRot;
    }

    public void explode(Vector3 pos) {
        explode(pos, 0, 1, 0);
    }

    public void explode(Vector3 pos, float sx, float sy, float sz) {
        isExploding = true;
        time = 0;
        origin = pos;

        for (Piece p : pieces) {
            p.visible(true);

            float vx = 0;
            float vy = 0;
            float vz = 0;

            if (sx == 0)
                vx = randomVel();
            else
                vx = randomVel(sx);

            if (sy == 0)
                vy = randomVel();
            else
                vy = randomVel(sy);

            if (sz == 0)
                vz = randomVel();
            else
                vz = randomVel(sz);

            p.pos.set(pos.x, pos.y, pos.z);
            p.vel.set(vx, vy, vz);
            p.modelInstance.transform.rotate(Vector3.X, randomRot());
            p.modelInstance.transform.rotate(Vector3.Y, randomRot());
            p.modelInstance.transform.rotate(Vector3.Z, randomRot());
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(float dt) {
        if (!isExploding)
            return;
   
        for (Piece p : pieces) {
            p.vel.y += gravity * dt;

            p.pos.x += p.vel.x * dt;
            p.pos.y += p.vel.y * dt;
            p.pos.z -= p.vel.z * dt;

            p.modelInstance.transform.setTranslation(p.pos);
        }

        time += dt;
        if (time >= TIME) {
            time = 0;
            isExploding = false;
            for (Piece p : pieces) {
                p.visible(false);
            }

            onExplosionEnd();
        }
    }

    public abstract void onExplosionEnd();

    static class Piece extends Fragment {
        public Vector3 pos = new Vector3();
        public Vector3 vel = new Vector3();

        public Piece(ModelInstance modelInstance) {
            super(modelInstance);
        }
    }
}