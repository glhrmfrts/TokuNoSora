package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;

/**
 * Represents a cell in the level.
 */
public abstract class Cell {
    public TouchEffect effect;
    public Vector3 pos = new Vector3();

    ModelInstance modelInstance;

    public abstract void reset();
    public abstract void render(GameRenderer renderer);
    public abstract Shape getShape();
}
