package com.habboi.tns.rendering.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.rendering.shaders.BrightPassShader;
import com.habboi.tns.rendering.shaders.GlowShader;
import com.habboi.tns.utils.Shaders;

public class BloomEffect extends Effect {
  private FrameBuffer blurBuffer;
  private BlurEffect blurEffect;
  private BrightPassShader brightPassShader;
  private FrameBuffer brightPassBuffer;
  private GlowShader glowShader;

  public BloomEffect() {
    blurEffect = new BlurEffect();
    brightPassShader = Shaders.get(BrightPassShader.class);
    glowShader = Shaders.get(GlowShader.class);

    blurBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GameRenderer.Width, GameRenderer.Height, false);
    brightPassBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GameRenderer.Width, GameRenderer.Height, false);
  }

  private void doBrightPass(GameRenderer renderer, FrameBuffer inBuffer) {
    // TODO: bright pass shader
    brightPassBuffer.begin();
    brightPassShader.begin(null, null);

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    inBuffer.getColorBufferTexture().bind(0);
    brightPassShader.render(renderer.getScreenQuadRenderable());

    brightPassShader.end();
    brightPassBuffer.end();
  }

  private void doBlur(GameRenderer renderer) {
    blurEffect.render(renderer, brightPassBuffer, blurBuffer);
  }

  private void doGlow(GameRenderer renderer, FrameBuffer inBuffer, FrameBuffer outBuffer) {
    if (outBuffer != null) outBuffer.begin();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    glowShader.begin(null, null);

    inBuffer.getColorBufferTexture().bind(0);
    blurBuffer.getColorBufferTexture().bind(1);
    glowShader.render(renderer.getScreenQuadRenderable());

    glowShader.end();
    Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

    if (outBuffer != null) outBuffer.end();
  }

  @Override
  public void render(GameRenderer renderer, FrameBuffer inBuffer, FrameBuffer outBuffer) {
    doBrightPass(renderer, inBuffer);
    doBlur(renderer);
    doGlow(renderer, inBuffer, outBuffer);
  }

  @Override
  public void dispose() {
    blurBuffer.dispose();
    brightPassBuffer.dispose();
  }
}
