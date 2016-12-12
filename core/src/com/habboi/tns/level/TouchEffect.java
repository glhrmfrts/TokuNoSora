package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.habboi.tns.worlds.World;

public enum TouchEffect {
    NONE(Color.WHITE, null),
    END(Color.WHITE, null),
    BOOST(Color.WHITE, BoostTouchEffectRenderer.class);

    private Color color;
    private Class<? extends TouchEffectRenderer> rendererClass;

    private TouchEffect(Color color, Class<? extends TouchEffectRenderer> rendererClass) {
        this.color = color;
        this.rendererClass = rendererClass;
    }

    public Color getColor() {
        return color;
    }

    public TouchEffectRenderer getRenderer(Cell cell, World world) {
        try {
            TouchEffectRenderer renderer = rendererClass.newInstance();

            renderer.init(cell, world);
            return renderer;
        } catch (Exception e) {
            return null;
        }
    }
}
