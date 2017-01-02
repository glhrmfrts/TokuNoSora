package com.habboi.tns.rendering.effects;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.rendering.GameRenderer;

public abstract class Effect implements Disposable {
  public boolean active = true;
  public boolean useDepthBuffer = false;

  public abstract void render(GameRenderer renderer, FrameBuffer inBuffer, FrameBuffer outBuffer);
}
