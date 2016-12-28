package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.worlds.World;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;

public class Tile extends LevelObject {
    ModelInstance outlineInstance;
    Color outlineColor = new Color();

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
        this.outlineColor.set(Color.WHITE);

        Model model = world.getTileModel(preset);

        modelInstance = new ModelInstance(model);
        modelInstance.transform.setToScaling(size.x, size.y, size.z);

        outlineInstance = new ModelInstance(Models.getTileOutlineModel());
        outlineInstance.transform.setToScaling(size.x, size.y, size.z);
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
    public void render(GameRenderer renderer, int pass) {
        TileShape tileShape = (TileShape)shape;
        switch (pass) {
        case GameRenderer.RenderPassBody:
            modelInstance.transform.setTranslation(tileShape.pos.x - tileShape.half.x, tileShape.pos.y - tileShape.half.y, tileShape.pos.z + tileShape.half.z);
            renderer.render(modelInstance);
            break;
        case GameRenderer.RenderPassOutline:
            outlineInstance.transform.setTranslation(tileShape.pos.x - tileShape.half.x, tileShape.pos.y - tileShape.half.y, tileShape.pos.z + tileShape.half.z);
            renderer.render(outlineInstance, outlineColor);
            break;
        case GameRenderer.RenderPassEffect:
            if (effectRenderer != null)
                effectRenderer.render(renderer);
            break;
        }
    }
}
