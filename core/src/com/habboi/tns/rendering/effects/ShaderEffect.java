package com.habboi.tns.rendering.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.habboi.tns.rendering.GameRenderer;

public class ShaderEffect extends Effect {
  protected Shader shader;

  public ShaderEffect(Shader shader) {
    shader.init();
    this.shader = shader;
  }

  public Shader getShader() {
     return shader;
  }

  public void render(GameRenderer renderer, FrameBuffer inBuffer, FrameBuffer outBuffer) {
    if (outBuffer != null) outBuffer.begin();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    shader.begin(null, null);
    inBuffer.getColorBufferTexture().bind(0);
    shader.render(renderer.getScreenQuadRenderable());
    shader.end();

    if (outBuffer != null) outBuffer.end();
  }

  @Override
  public void dispose() {
  }
}
