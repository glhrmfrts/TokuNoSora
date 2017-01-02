package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.rendering.effects.Effect;

import java.util.ArrayList;

public class PostProcessor implements Disposable {
  private ArrayList<Effect> active = new ArrayList<>();
  private ArrayList<Effect> effects = new ArrayList<>();
  private FrameBuffer inDepthBuffer;
  private FrameBuffer inTextureBuffer;
  private FrameBuffer lastDepthBuffer;
  private FrameBuffer lastTextureBuffer;
  private FrameBuffer outDepthBuffer;
  private FrameBuffer outTextureBuffer;
  private Vector2 resolution;
  private boolean invalid;
  private GameRenderer renderer;

  public PostProcessor(GameRenderer renderer, Vector2 resolution) {
    this.resolution = resolution;
    this.renderer = renderer;

    inDepthBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)resolution.x, (int)resolution.y, true);
    inTextureBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)resolution.x, (int)resolution.y, false);
    outDepthBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)resolution.x, (int)resolution.y, true);
    outTextureBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)resolution.x, (int)resolution.y, false);

    lastDepthBuffer = inDepthBuffer;
    lastTextureBuffer = inTextureBuffer;
  }

  public void addEffect(Effect e) {
    effects.add(e);
    invalidate();
  }

  public void begin() {
    if (invalid) {
      validate();
    }

    if (active.size() == 0) {
      return;
    }

    lastDepthBuffer = inDepthBuffer;
    lastDepthBuffer.begin();
  }

  public void end() {
    if (active.size() == 0) {
      return;
    }

    lastDepthBuffer.end();

    FrameBuffer lastBuffer = lastDepthBuffer;
    int size = active.size();
    int i = 0;

    for (Effect eff : active) {
      FrameBuffer inBuffer = lastBuffer;
      FrameBuffer outBuffer;

      if (i == size - 1) {
        outBuffer = null;
      } else if (eff.useDepthBuffer) {
        outBuffer = swapDepthBuffer();
      } else {
        outBuffer = swapTextureBuffer();
      }

      eff.render(renderer, inBuffer, outBuffer);
      lastBuffer = outBuffer;

      i++;
    }
  }

  public void invalidate() {
    invalid = true;
  }

  private FrameBuffer swapDepthBuffer() {
    FrameBuffer result;
    if (lastDepthBuffer == inDepthBuffer) {
      result = outDepthBuffer;
    } else {
      result = inDepthBuffer;
    }

    return lastDepthBuffer = result;
  }

  private FrameBuffer swapTextureBuffer() {
    FrameBuffer result;
    if (lastTextureBuffer == inTextureBuffer) {
      result = outTextureBuffer;
    } else {
      result = inTextureBuffer;
    }

    return lastTextureBuffer = result;
  }

  private void validate() {
    active.clear();
    for (Effect eff : effects) {
      if (eff.active) {
        active.add(eff);
      }
    }

    invalid = false;
  }

  @Override
  public void dispose() {

  }
}
