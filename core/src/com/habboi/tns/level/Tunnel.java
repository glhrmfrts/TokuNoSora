package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.shapes.TunnelShape;
import com.habboi.tns.worlds.World;

/**
 * Represents a tunnel instance on the level.
 */
public class Tunnel extends Cell {
    ModelInstance outlineInstance;
    TunnelShape shape;

    public Tunnel(Vector3 pos, float depth, int preset, World world) {
        float x = pos.x*TunnelShape.TUNNEL_WIDTH;
        float y = pos.y*TileShape.TILE_HEIGHT;
        float z = -(pos.z*TunnelShape.TUNNEL_DEPTH + depth/2);
        this.shape = new TunnelShape(new Vector3(x, y, z), depth);

        modelInstance = new ModelInstance(world.getTunnelModel(preset));
        modelInstance.transform.setToScaling(TunnelShape.TUNNEL_WIDTH, TunnelShape.TUNNEL_HEIGHT, depth*TunnelShape.TUNNEL_DEPTH);

        outlineInstance = new ModelInstance(Models.getTunnelOutlineModel());
        outlineInstance.transform.setToScaling(TunnelShape.TUNNEL_WIDTH, TunnelShape.TUNNEL_HEIGHT, depth*TunnelShape.TUNNEL_DEPTH);
    }

    public boolean isShipInside() {
        return shape.isInside;
    }

    @Override
    public void reset() {
        shape.isInside = false;
    }

    @Override
    public void render(GameRenderer renderer) {
        modelInstance.transform.setTranslation(shape.pos.x, shape.pos.y + TunnelShape.TUNNEL_HEIGHT/4, shape.pos.z);
        renderer.render(modelInstance);

        outlineInstance.transform.setTranslation(shape.pos.x, shape.pos.y + TunnelShape.TUNNEL_HEIGHT/4, shape.pos.z);
        renderer.renderGlow(outlineInstance);
    }

    @Override
    public Shape getShape() {
        return shape;
    }
}
