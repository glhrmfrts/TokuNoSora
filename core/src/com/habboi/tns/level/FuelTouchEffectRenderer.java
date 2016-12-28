package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.worlds.World;

import java.util.ArrayList;

public class FuelTouchEffectRenderer implements TouchEffectRenderer {
    static final float HALF_X = 0.5f * TileShape.TILE_WIDTH;
    static final float DISTANCE_Y = 0.01f;

    ArrayList<ModelInstance> circleInstances = new ArrayList<>();

    @Override
    public void init(LevelObject obj, World world) {
        Vector3 size = obj.shape.getSize();
        float width = (int)size.x;
        float depth = (int)size.z;

        Vector3 pos = obj.shape.getPos();
        float baseX = pos.x - size.x * 0.5f + 0.5f;
        float baseY = pos.y + size.y * 0.5f;
        float baseZ = pos.z + size.z * 0.5f;

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                ModelInstance circle = new ModelInstance(Models.getFloorCircleModel());
                circle.transform.setToScaling(0.5f, 1, 0.5f);
                circle.transform.setTranslation(baseX + (float)x, baseY + DISTANCE_Y, baseZ - (float)z);

                circleInstances.add(circle);
            }
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(GameRenderer renderer) {
        for (ModelInstance circle : circleInstances) {
            renderer.renderGlow(circle, Color.BLUE);
        }
    }
}
