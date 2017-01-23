package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.shapes.TunnelShape;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;

public class Tunnel extends LevelObject {

    public Tunnel(Vector3 pos, float depth, int preset, World world) {
        float x = pos.x*TunnelShape.TUNNEL_WIDTH;
        float y = pos.y*TileShape.TILE_HEIGHT;
        float z = -(pos.z*TunnelShape.TUNNEL_DEPTH + depth/2);
        this.shape = new TunnelShape(new Vector3(x, y, z), depth);

        modelInstance = new ModelInstance(world.getTunnelModel(preset));
        modelInstance.transform.setToScaling(TunnelShape.TUNNEL_WIDTH, TunnelShape.TUNNEL_HEIGHT, depth*TunnelShape.TUNNEL_DEPTH);
        modelInstance.transform.setTranslation(shape.getPos().x, shape.getPos().y + TileShape.TILE_HEIGHT, shape.getPos().z + depth/2);
    }

    public boolean isShipInside() {
        return ((TunnelShape)shape).isInside;
    }

    @Override
    public void reset() {
        ((TunnelShape)shape).isInside = false;
    }

    @Override
    public void update(float dt) {
    }
}
