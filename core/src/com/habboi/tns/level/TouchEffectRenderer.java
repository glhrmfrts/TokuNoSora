package com.habboi.tns.level;

import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.worlds.World;

public interface TouchEffectRenderer {
    public abstract void init(Cell cell, World world);
    public abstract void update(float dt);
    public abstract void render(GameRenderer renderer);
}
