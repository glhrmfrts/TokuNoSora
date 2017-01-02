package com.habboi.tns.rendering.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.rendering.shaders.BlurShader;
import com.habboi.tns.utils.Shaders;

public class BlurEffect extends Effect {
  private FrameBuffer helperBuffer;
  private BlurShader shader;

  public BlurEffect() {
    shader = Shaders.get(BlurShader.class);
    shader.begin(null, null);
    shader.setImageSize(new Vector2(GameRenderer.Width, GameRenderer.Height));
    shader.end();

    helperBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GameRenderer.Width, GameRenderer.Height, false);
  }

  @Override
  public void render(GameRenderer renderer, FrameBuffer inBuffer, FrameBuffer outBuffer) {
    helperBuffer.begin();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    inBuffer.getColorBufferTexture().bind(0);
    shader.setOrientation(0);
    shader.render(renderer.getScreenQuadRenderable());

    helperBuffer.end();

    // mix with vertical blur
    if (outBuffer != null) outBuffer.begin();
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    helperBuffer.getColorBufferTexture().bind(0);
    shader.setOrientation(1);
    shader.render(renderer.getScreenQuadRenderable());

    if (outBuffer != null) outBuffer.end();
  }

  @Override
  public void dispose() {
    helperBuffer.dispose();
  }
}
