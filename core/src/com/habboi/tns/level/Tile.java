package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.worlds.World;

public class Tile extends LevelObject {

    public Tile(Vector3 pos, Vector3 size, int preset, TouchEffect effect, World world) {
        size.x *= TileShape.TILE_WIDTH;
        size.y *= TileShape.TILE_HEIGHT;
        size.z *= TileShape.TILE_DEPTH;

        TileShape tileShape = new TileShape(pos, size);

        tileShape.pos.x = tileShape.pos.x*TileShape.TILE_WIDTH + tileShape.half.x;
        tileShape.pos.y = tileShape.pos.y*TileShape.TILE_HEIGHT + tileShape.half.y;
        tileShape.pos.z = -(tileShape.pos.z*TileShape.TILE_DEPTH + tileShape.half.z);

        this.shape = tileShape;
        this.effect = effect;
        this.effectRenderer = effect.getRenderer(this, world);

        Model model = world.getTileModel(preset);

        modelInstance = new ModelInstance(model);
        modelInstance.transform.setToScaling(size.x, size.y, size.z);
        modelInstance.transform.setTranslation(tileShape.pos.x - size.x / 2, tileShape.pos.y - size.y / 2, tileShape.pos.z + size.z / 2);
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(float dt) {
        if (effectRenderer != null)
            effectRenderer.update(dt);
    }
}
