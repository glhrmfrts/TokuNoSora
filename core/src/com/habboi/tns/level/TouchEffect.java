package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.habboi.tns.rendering.BoostTouchEffectRenderer;
import com.habboi.tns.rendering.FuelTouchEffectRenderer;
import com.habboi.tns.rendering.TouchEffectRenderer;
import com.habboi.tns.worlds.World;

public enum TouchEffect {
    NONE(Color.WHITE, null),
    END(Color.WHITE, null),
    COLLECT(Color.WHITE, null),
    BOOST(Color.GREEN, BoostTouchEffectRenderer.class),
    FUEL(Color.BLUE, FuelTouchEffectRenderer.class);

    private Color color;
    private Class<? extends TouchEffectRenderer> rendererClass;

    private TouchEffect(Color color, Class<? extends TouchEffectRenderer> rendererClass) {
        this.color = color;
        this.rendererClass = rendererClass;
    }

    public Color getColor() {
        return color;
    }

    public TouchEffectRenderer getRenderer(LevelObject obj, World world) {
        try {
            TouchEffectRenderer renderer = rendererClass.newInstance();

            renderer.init(obj, world);
            return renderer;
        } catch (Exception e) {
            return null;
        }
    }
}
