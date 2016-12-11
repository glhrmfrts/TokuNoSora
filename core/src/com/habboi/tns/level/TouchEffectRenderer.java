package com.habboi.tns;

import com.habboi.tns.rendering.GameRenderer;

public interface TouchEffectRenderer {
    public abstract void init(Cell cell);
    public abstract void render(GameRenderer renderer);
}
