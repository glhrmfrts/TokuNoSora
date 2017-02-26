package com.habboi.tns.worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.utils.Models;

import java.util.ArrayList;

/**
 * Created by w7 on 03/07/2016.
 */
public class World2 extends World {
    static class Rect extends Fragment {
        float zOffset;

        public Rect(ModelInstance modelInstance) {
            super(modelInstance);
        }
    }

    static final float DEPTH = 800f;
    static final float WIDTH = 40f;
    static final float HEIGHT = 20f;
    static final float STEP = 0.05f;

    float centerX;
    float time;
    float offset;
    float spread;
    int count;
    int litRect;
    Vector3 shipPos = new Vector3();
    ArrayList<Rect> rects = new ArrayList<>();

    public World2() {
        super(2, 2, "Andromeda", "nice.ogg");
        addColor(0x64f7f7);
        addColor(0x458696);
        addColor(0x989822);
        addColor(0x1a37f5);
        addColor(0x440bad);
        addTileModel(new int[][]{
                new int[]{0, 0, 0, 0}, new int[]{1, 0, 0, 1}, new int[]{0, 0, 0, 0},
                new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}
            });
        addTileModel(new int[][]{
                new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4},
                new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4}, new int[]{4, 4, 4, 4}
            });
        addTileModel(new int[][]{
                new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3},
                new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3}, new int[]{3, 3, 3, 3}
            });
        addTunnelModel(new int[]{1, 4});

        // create the landscape
        count = 40;
        spread = DEPTH / count;
        Model rectModel = Models.createLineRectModel(colors.get(3));
        for (int i = 0; i < count; i++) {
            Rect rect = new Rect(new ModelInstance(rectModel));
            rect.zOffset = spread * i;
            rect.modelInstance.transform.setToScaling(WIDTH, HEIGHT, 1);
            rects.add(rect);
        }

        background = new Texture(Gdx.files.internal("background.jpg"));
    }

    @Override
    public void addToScene(Scene scene) {
        for (Rect rect : rects) {
            scene.add(rect);
        }
    }

    @Override
    public void setCenterX(float cx) {
        centerX = cx;
    }

    @Override
    public void reset() {
        offset = 0;
        litRect = 0;
        time = 0;
    }

    @Override
    public void update(Vector3 shipPos, float vel, float dt) {
        this.shipPos.set(shipPos);
        offset = vel * dt * 2;

        time += dt;
        if (time >= STEP) {
          time = 0;
          litRect = (litRect + 1) % count;
        }

        int rev = count - litRect;
        Models.setColor(rects.get(rev % count).modelInstance, ColorAttribute.Diffuse, colors.get(1));
        Models.setColor(rects.get((rev + 1) % count).modelInstance, ColorAttribute.Diffuse, colors.get(3));

        final float base = shipPos.z - DEPTH/1.1f;
        for (int i = 0; i < count; i++) {
          Rect rect = rects.get(i);
          rect.zOffset += offset;
          rect.zOffset %= DEPTH;
          rect.modelInstance.transform.setTranslation(centerX, 0, base + rect.zOffset);
        }
        offset = 0;
    }

    @Override
    public void dispose() {
        rects.get(0).modelInstance.model.dispose();
        rects.clear();
    }
}
