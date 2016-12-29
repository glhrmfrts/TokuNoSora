package com.habboi.tns.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;

public class Collectable extends LevelObject {
    static final float WIDTH = 0.25f;
    static final float HEIGHT = 0.5f;
    static final float DEPTH = 0.25f;

    boolean visible = true;

    public Collectable(Vector3 pos, World world) {
        pos.x = pos.x * TileShape.TILE_WIDTH + (WIDTH * 0.5f * TileShape.TILE_WIDTH);
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
    public void reset() {
        effect = TouchEffect.NONE;
        visible = true;
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(GameRenderer renderer, int pass) {
        if (!visible) return;

        if (pass == GameRenderer.RenderPassBody) {
            renderer.render(modelInstance);
        }
    }

    @Override
    public boolean onCollision(Ship ship) {
        effect = TouchEffect.NONE;
        visible = false;
        return false;
    }
}