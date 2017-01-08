package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.habboi.tns.rendering.LevelObjectRenderer;
import com.habboi.tns.shapes.Shape;

public abstract class LevelObject {
    public TouchEffect effect;
    public ModelInstance modelInstance;
    public LevelObjectRenderer renderer;
    public Shape shape;
    public boolean visible = true;

    public abstract void reset();
    public abstract void update(float dt);
}
