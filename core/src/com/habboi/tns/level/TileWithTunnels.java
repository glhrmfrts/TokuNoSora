package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.shapes.TWTShape;
import com.habboi.tns.worlds.World;

/**
 * A tile with one or more tunnels in it's body
 */
public class TileWithTunnels extends Cell {
    TWTShape shape;

    public TileWithTunnels(Vector3 pos, Vector3 size, int preset, int[] tunnels, Color outlineColor, TouchEffect effect, World world) {
        size.x *= TileShape.TILE_WIDTH;
        size.y *= TileShape.TILE_HEIGHT;
        size.z *= TileShape.TILE_DEPTH;

        pos.x *= TileShape.TILE_WIDTH;
        pos.y *= TileShape.TILE_HEIGHT;
        pos.z *= -TileShape.TILE_DEPTH;

        this.shape = new TWTShape(pos, size, tunnels);
        this.effect = effect;

        modelInstance = new ModelInstance(world.getTileWithTunnelsModel(size, tunnels, preset));
        modelInstance.transform.setTranslation(pos.x, pos.y, pos.z);
    }

    @Override
    public void reset() {
    }

    @Override
    public void render(GameRenderer renderer) {
        renderer.render(modelInstance);
    }

    @Override
    public Shape getShape() {
        return shape;
    }
}
