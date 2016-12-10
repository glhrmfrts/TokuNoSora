package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.FinishShape;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * The finish.
 */
public class Finish extends Cell {

    FinishShape shape;

    public Finish(Vector3 pos, float radius) {
        this.effect = TouchEffect.END;
        this.shape = new FinishShape(new Vector3(pos.x*TileShape.TILE_WIDTH, pos.y*TileShape.TILE_HEIGHT, -pos.z * TileShape.TILE_DEPTH), radius);
    }

    @Override
    public void reset() {

    }

    @Override
    public void render(GameRenderer renderer) {
    }

    @Override
    public Shape getShape() {
        return shape;
    }
}
