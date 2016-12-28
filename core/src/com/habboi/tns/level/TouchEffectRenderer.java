package com.habboi.tns.level;

import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.worlds.World;

public interface TouchEffectRenderer {
    public abstract void init(LevelObject obj, World world);
    public abstract void update(float dt);
    public abstract void render(GameRenderer renderer);
}
