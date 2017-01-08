package com.habboi.tns.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * A simple 2D text.
 */
public class Text {

    static {
        Tween.registerAccessor(Text.class, new Accessor());
    }

    public Vector2 pos = new Vector2();
    public String value;
    public BitmapFont font;
    public BitmapFont.TextBounds bounds;
    public final Color color = new Color();

    public Text(BitmapFont font, String value, Vector2 pos, Color color) {
        this.font = font;
        this.value = value;
        this.bounds = new BitmapFont.TextBounds(font.getBounds(value));
        if (pos != null)
            this.pos.set(pos);
        if (color != null)
            this.color.set(color);
    }

    public Vector2 getPos() {
        return pos;
    }

    public Color getColor() {
        return color;
    }

    public Vector2 getBounds() {
        return new Vector2(bounds.width, bounds.height);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(String value, boolean updateBounds) {
        this.value = value;
        if (updateBounds)
            this.bounds = new BitmapFont.TextBounds(font.getBounds(value));
    }

    public String getValue() {
        return value;
    }

    public void draw(SpriteBatch sb) {
        draw(sb, false);
    }

    public void draw(SpriteBatch sb, boolean center) {
        if (color.a == 0) return;

        float x = pos.x;
        float y = pos.y;
        if (center) {
            x -= bounds.width / 2;
            y -= bounds.height / 2;
        }

        font.setColor(color);
        font.draw(sb, value, x, y);
    }

    public static class Accessor implements TweenAccessor<Text> {

        public static final int TWEEN_POS = 0;
        public static final int TWEEN_ALPHA = 1;

        @Override
        public int getValues(Text text, int i, float[] floats) {
            if (i == TWEEN_POS) {
                floats[0] = text.pos.x;
                floats[1] = text.pos.y;
                return 2;
            } else if (i == TWEEN_ALPHA) {
                floats[0] = text.color.a;
                return 1;
            }
            return 0;
        }

        @Override
        public void setValues(Text text, int i, float[] floats) {
            if (i == TWEEN_POS) {
                text.pos.x = floats[0];
                text.pos.y = floats[1];
            } else if (i == TWEEN_ALPHA) {
                text.color.a = floats[0];
            }
        }
    }
}
