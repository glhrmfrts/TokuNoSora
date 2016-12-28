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

public class TileWithTunnels extends LevelObject {
    ModelInstance outlineInstance;

    public TileWithTunnels(Vector3 pos, Vector3 size, int preset, int[] tunnels, TouchEffect effect, World world) {
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

        outlineInstance = new ModelInstance(world.getTileWithTunnelsOutlineModel(size, tunnels));
        outlineInstance.transform.setTranslation(pos.x, pos.y, pos.z);
    }
    
    @Override
    public void reset() {
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(GameRenderer renderer, int pass) {
        switch (pass) {
        case GameRenderer.RenderPassBody:
            renderer.render(modelInstance);
            break;

        case GameRenderer.RenderPassOutline:
            renderer.render(outlineInstance);
            break;
        }
    }
}
