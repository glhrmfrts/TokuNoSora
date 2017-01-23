package com.habboi.tns.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class Button extends Widget {
  Rect rect;
  ShapeRenderer sr;
  Text text;

  public Button(ShapeRenderer sr, Text text, float padx, float pady, final Color color, final Color pressedColor) {
    this.sr = sr;
    this.text = text;
    this.rect = new Rect(new Rectangle(
            text.pos.x - text.bounds.width / 2 - padx,
            text.pos.y - text.bounds.height / 2 - pady,
            text.bounds.width + padx * 2,
            text.bounds.height + pady * 2
    ));
    this.rect.color.set(color);

    setTouchable(Touchable.enabled);
    setBounds(rect.rect.x, rect.rect.y, rect.rect.width, rect.rect.height);

    addListener(new InputListener() {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        rect.color.set(pressedColor);
        return true;
      }

      @Override
      public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        rect.color.set(color);
      }
    });
  }

  public void setColor(Color c) {
    rect.color.set(c);
  }

  @Override
  public float getPrefWidth() {
    return rect.rect.width;
  }

  @Override
  public float getPrefHeight() {
    return rect.rect.height;
  }

  @Override
  public void setBounds(float x, float y, float width, float height) {
      super.setBounds(x, y, width, height);
      boundsChanged();
  }

  private void boundsChanged() {
      rect.rect.x = getX();
      rect.rect.y = getY();
      rect.rect.width = getWidth();
      rect.rect.height = getHeight();
      text.pos.x = getX() + getWidth() / 2;
      text.pos.y = getY() + getHeight() / 2;
  }

  @Override
  public void draw(Batch batch, float a) {
    batch.end();
    sr.setProjectionMatrix(batch.getProjectionMatrix());
    sr.setTransformMatrix(batch.getTransformMatrix());
    rect.draw(sr, ShapeRenderer.ShapeType.Filled, false);
    batch.begin();
    text.draw((SpriteBatch) batch, true, true);
  }
}
