package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;

public enum TouchEffect {
    NONE(Color.WHITE),
    END(Color.WHITE),
    COLLECT(Color.WHITE),
    BOOST(Color.GREEN),
    EXPLODE(Color.RED),
    OXYGEN(Color.BLUE);

    private Color color;

    private TouchEffect(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static TouchEffect fromName(String name) {
      switch (name) {
        case "boost":
          return TouchEffect.BOOST;
        case "explode":
          return TouchEffect.EXPLODE;
        case "oxygen":
          return TouchEffect.OXYGEN;
        default:
          return TouchEffect.NONE;
      }
    }
}
