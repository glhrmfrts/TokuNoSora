package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.habboi.tns.level.LevelObject;

public interface LevelObjectRenderer<T extends LevelObject> {
    public void render(T obj, ModelBatch batch, Environment environment);
}