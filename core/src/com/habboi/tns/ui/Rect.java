package com.habboi.tns.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenEquations;

/**
 * A Rectangle.
 */
public class Rect {

  private final Rectangle rect = new Rectangle();
  private final Color color = new Color();

  static {
    Tween.registerAccessor(Rect.class, new Accessor());
  }

  public Rect(Rectangle rect) {
    this(rect, Color.BLACK);
  }

  public Rect(Rectangle rect, Color color) {
    this.rect.set(rect);
    this.color.set(color);
  }

  public Rectangle getRectangle() {
    return rect;
  }

  public Tween getEnterFromLeftTween(int width) {
    return Tween.to(this, Accessor.TWEEN_WIDTH, 0.5f)
            .target(width)
            .ease(TweenEquations.easeOutQuad);
  }

  public Tween getFadeTween(float from, float to) {
    color.a = from;
    return Tween.to(this, Accessor.TWEEN_ALPHA, 0.5f)
            .target(to)
            .ease(TweenEquations.easeOutQuad);
  }

  public void draw(ShapeRenderer sr) {
    draw(sr, ShapeRenderer.ShapeType.Filled);
  }

  public void draw(ShapeRenderer sr, ShapeRenderer.ShapeType type) {
    if (color.a == 0) return;

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    sr.begin(type);
    sr.setColor(color);
    sr.rect(rect.x, rect.y, rect.width, rect.height);
    sr.end();

    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  public static class Accessor implements TweenAccessor<Rect> {

    public static final int TWEEN_WIDTH = 0;
    public static final int TWEEN_ALPHA = 1;

    @Override
    public int getValues(Rect backgroundRect, int i, float[] floats) {
      if (i == TWEEN_WIDTH) {
        floats[0] = backgroundRect.rect.width;
        return 1;
      } else if (i == TWEEN_ALPHA) {
        floats[0] = backgroundRect.color.a;
        return 1;
      }
      return 0;
    }

    @Override
    public void setValues(Rect backgroundRect, int i, float[] floats) {
      if (i == TWEEN_WIDTH) {
        backgroundRect.rect.width = floats[0];
      } else if (i == TWEEN_ALPHA) {
        backgroundRect.color.a = floats[0];
      }
    }
  }
}
