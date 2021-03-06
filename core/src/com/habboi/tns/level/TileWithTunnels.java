package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.shapes.TWTShape;
import com.habboi.tns.worlds.World;

public class TileWithTunnels extends LevelObject {

    public Fragment fragment;
    boolean isShipInsideDt;

    public TileWithTunnels(Vector3 pos, Vector3 size, int preset, int[] tunnels, TouchEffect effect, World world) {
        size.x *= TileShape.TILE_WIDTH;
        size.y *= TileShape.TILE_HEIGHT;
        size.z *= TileShape.TILE_DEPTH;

        pos.x *= TileShape.TILE_WIDTH;
        pos.y *= TileShape.TILE_HEIGHT;
        pos.z *= -TileShape.TILE_DEPTH;

        this.shape = new TWTShape(pos, size, tunnels);
        this.effect = effect;

        fragment = new Fragment(new ModelInstance(world.getTileWithTunnelsModel(size, tunnels, preset)));
        fragment.modelInstance.transform.setTranslation(pos.x, pos.y, pos.z);
    }

    @Override
    public void addToScene(Scene scene) {
        scene.add(fragment);
    }
    
    @Override
    public void reset() {
    }

    @Override
    public void update(float dt) {
        ShipCamera cam = Game.getInstance().getShip().getCam();
        boolean isShipInside = ((TWTShape) shape).isShipInside;

        if (isShipInside) {
            cam.distanceY = ShipCamera.DISTANCE_Y + shape.getSize().y;
        } else if (isShipInsideDt) {
            cam.distanceY = ShipCamera.DISTANCE_Y;
        }

        isShipInsideDt = isShipInside;
    }
}
