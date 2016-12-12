package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
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

    public Tile(Vector3 pos, Vector3 size, int preset, TouchEffect effect, World world) {
        size.x *= TileShape.TILE_WIDTH;
        size.y *= TileShape.TILE_HEIGHT;
        size.z *= TileShape.TILE_DEPTH;

        shape = new TileShape(pos, size);

        shape.pos.x = shape.pos.x*TileShape.TILE_WIDTH + shape.half.x;
        shape.pos.y = shape.pos.y*TileShape.TILE_HEIGHT + shape.half.y;
        shape.pos.z = -(shape.pos.z*TileShape.TILE_DEPTH + shape.half.z);

        this.effect = effect;
        this.effectRenderer = effect.getRenderer(this, world);
        this.outlineColor.set(effect.getColor());

        Model model;
        if (effect == TouchEffect.BOOST) {
            model = Models.getTileBoostModel();
        } else {
            model = world.getTileModel(preset);
        }

        modelInstance = new ModelInstance(model);
        modelInstance.transform.setToScaling(size.x, size.y, size.z);

        outlineInstance = new ModelInstance(Models.getTileOutlineModel());
        outlineInstance.transform.setToScaling(size.x, size.y, size.z);
    }

    @Override
    public Vector3 getPos() {
        return shape.pos;
    }

    @Override
    public Vector3 getSize() {
        return new Vector3(shape.half.x*2, shape.half.y*2, shape.half.z*2);
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(float dt) {
        if (effectRenderer != null)
            effectRenderer.update(dt);
    }

    @Override
    public void render(GameRenderer renderer) {
        if (effectRenderer != null)
            effectRenderer.render(renderer);

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
