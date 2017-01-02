package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.habboi.tns.level.LevelObject;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.worlds.World;

public interface TouchEffectRenderer {
    public void init(LevelObject obj, World world);
    public void update(float dt);
    public void render(ModelBatch batch, Environment environment);
}
