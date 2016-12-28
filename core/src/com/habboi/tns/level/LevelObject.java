package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;

public abstract class LevelObject {
    public TouchEffect effect;
    public TouchEffectRenderer effectRenderer;
    public ModelInstance modelInstance;
    public Shape shape;
    
    public abstract void reset();
    public abstract void update(float dt);
    public abstract void render(GameRenderer renderer, int pass);
}
