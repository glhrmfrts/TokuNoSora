package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.worlds.World;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;

/**
 * Represents a tile instance on the level.
 */
public class Tile extends Cell {
    ModelInstance outlineInstance;
    Color outlineColor = new Color();
    TileShape shape;

    public Tile(Vector3 pos, Vector3 size, int preset, Color outlineColor, TouchEffect effect, World world) {
        size.x *= TileShape.TILE_WIDTH;
        size.y *= TileShape.TILE_HEIGHT;
        size.z *= TileShape.TILE_DEPTH;

        shape = new TileShape(pos, size);

        shape.pos.x = shape.pos.x*TileShape.TILE_WIDTH + shape.half.x;
        shape.pos.y = shape.pos.y*TileShape.TILE_HEIGHT + shape.half.y;
        shape.pos.z = -(shape.pos.z*TileShape.TILE_DEPTH + shape.half.z);

        this.effect = effect;
        this.outlineColor.set(outlineColor);

        modelInstance = new ModelInstance(world.getTileModel(preset));
        modelInstance.transform.setToScaling(size.x, size.y, size.z);

        outlineInstance = new ModelInstance(Models.getTileOutlineModel());
        outlineInstance.transform.setToScaling(size.x, size.y, size.z);
    }

    @Override
    public void reset() {

    }

    @Override
    public void render(GameRenderer renderer) {
        modelInstance.transform.setTranslation(shape.pos.x - shape.half.x, shape.pos.y - shape.half.y, shape.pos.z + shape.half.z);
        renderer.render(modelInstance);

        outlineInstance.transform.setTranslation(shape.pos.x - shape.half.x, shape.pos.y - shape.half.y, shape.pos.z + shape.half.z);
        renderer.renderGlow(outlineInstance, outlineColor);
    }

    @Override
    public Shape getShape() {
        return shape;
    }
}
