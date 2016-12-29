package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;

import java.util.ArrayList;

public class BoostTouchEffectRenderer implements TouchEffectRenderer {
    static final float HALF_X = 0.5f * TileShape.TILE_WIDTH;
    static final float DISTANCE_Y = 0.01f;
    static final float SPEED = 4;

    static class Arrow {
        Color color;
        Vector3 pos;
        ModelInstance instance;
    }

    ArrayList<Arrow> arrows = new ArrayList<>();
    float baseX, baseY, baseZ;
    LevelObject obj;
    Vector3 objPos;
    Vector3 objSize;

    @Override
    public void init(LevelObject obj, World world) {
        this.obj = obj;

        Vector3 size = objSize = obj.shape.getSize();
        float width = (int)size.x;
        float depth = (int)size.z;

        Vector3 pos = objPos = obj.shape.getPos();
        baseX = pos.x - size.x * 0.5f + HALF_X;
        baseY = pos.y + size.y * 0.5f;
        baseZ = pos.z + size.z * 0.5f;

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                Arrow arrow = new Arrow();
                arrow.color = new Color(Color.GREEN);
                arrow.instance = new ModelInstance(Models.getFloorArrowModel());
                arrow.instance.transform.setToScaling(0.5f, 1, 1);

                arrow.pos = new Vector3(baseX + x, baseY + DISTANCE_Y, baseZ - z);
                arrows.add(arrow);
            }
        }
    }

    @Override
    public void update(float dt) {
        /*for (Arrow arrow : arrows) {
            arrow.pos.z -= SPEED * dt;

            if (arrow.pos.z <= baseZ - objSize.z + 1f) {
                arrow.pos.z = baseZ;
            }
        }*/
    }

    @Override
    public void render(GameRenderer renderer) {
        for (Arrow arrow : arrows) {
            arrow.instance.transform.setTranslation(arrow.pos.x, arrow.pos.y, arrow.pos.z);
            renderer.renderGlow(arrow.instance, arrow.color);
        }
    }
}
