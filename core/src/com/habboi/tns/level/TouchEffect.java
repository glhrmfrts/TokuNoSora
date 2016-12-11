package com.habboi.tns;

import com.badlogic.gdx.graphics.Color;

public enum TouchEffect {
    NONE(Color.WHITE, null),
    END(Color.WHITE, null),
    BOOST(Color.GREEN, BoostTouchEffectRenderer.class);

    private Color color;
    private Class<T extends TouchEffectRenderer> rendererClass;

    private TouchEffect(Color color, Class<? extends TouchEffectRenderer> rendererClass) {
        this.color = color;
        this.rendererClass = rendererClass;
    }

    public Color getColor() {
        return color;
    }

    public TouchEffectRenderer getRenderer(Cell cell) {
        TouchEffectRenderer renderer = rendererClass.newInstance();

        renderer.init(cell);
        return renderer;
    }
}
