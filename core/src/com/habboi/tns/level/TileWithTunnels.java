package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.worlds.World;
import com.habboi.tns.rendering.GameRenderer;

/**
 * A tile with one or more tunnels in it's body
 */
public class TileWithTunnels extends Cell {

    public TileWithTunnels(Vector3 pos, Vector3 size, Color outlineColor, TouchEffect effect, int preset, World world) {
        size.x *= Tile.TILE_WIDTH;
        size.y *= Tile.TILE_HEIGHT;
        size.z *= Tile.TILE_DEPTH;

        this.pos.set(pos);
        this.effect = effect;

        modelInstance = new ModelInstance(world.getTileWithTunnelsModel(size, preset));
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
    public boolean checkCollision(Ship ship) {
        return false;
    }
}
