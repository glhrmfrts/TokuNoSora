package com.habboi.tns.level;

import com.habboi.tns.rendering.GameRenderer;


public interface GenericObject {
    public void update(float dt);
    public void render(GameRenderer renderer, int pass);
}
