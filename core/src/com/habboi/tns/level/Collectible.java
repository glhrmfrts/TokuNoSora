package com.habboi.tns.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;

public class Collectible extends LevelObject {
    static final float WIDTH = 0.25f;
    static final float HEIGHT = 0.5f;
    static final float DEPTH = 0.25f;

    public boolean collected;

    public Collectible(Vector3 pos, World world) {
        pos.x = pos.x * TileShape.TILE_WIDTH;
        pos.y = pos.y * TileShape.TILE_HEIGHT + (HEIGHT * 0.5f * TileShape.TILE_HEIGHT) + HEIGHT;
        pos.z = -pos.z * TileShape.TILE_DEPTH + (DEPTH * 0.5f * TileShape.TILE_DEPTH);

        effect = TouchEffect.COLLECT;

        TileShape tileShape = new TileShape(pos, new Vector3(WIDTH * 2, HEIGHT * 2, DEPTH * 2));
        shape = tileShape;

        modelInstance = new ModelInstance(Models.getPrismModel());
        modelInstance.transform.setToScaling(tileShape.half);
        modelInstance.transform.setTranslation(pos);
    }

    @Override
    public void addToScene(Scene scene) {
        scene.add(new Fragment(modelInstance));
    }

    @Override
    public void reset() {
        visible = true;
        collected = false;
    }

    @Override
    public void update(float dt) {
    }
}
