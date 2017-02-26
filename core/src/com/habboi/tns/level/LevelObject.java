package com.habboi.tns.level;

import com.habboi.tns.rendering.Scene;
import com.habboi.tns.shapes.Shape;

public abstract class LevelObject {
    public TouchEffect effect;
    public Shape shape;

    public abstract void addToScene(Scene scene);
    public abstract void reset();
    public abstract void update(float dt);
}
