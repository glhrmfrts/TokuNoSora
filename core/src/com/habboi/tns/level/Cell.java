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

    TouchEffectRenderer effectRenderer;
    ModelInstance modelInstance;

    public abstract Vector3 getPos();
    public abstract Vector3 getSize();
    public abstract Shape getShape();
    public abstract void reset();
    public abstract void update(float dt);
    public abstract void render(GameRenderer renderer);
}
