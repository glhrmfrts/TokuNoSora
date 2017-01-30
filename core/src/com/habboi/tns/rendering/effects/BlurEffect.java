package com.habboi.tns.rendering.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FloatFrameBuffer;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.rendering.shaders.BlurShader;
import com.habboi.tns.utils.Shaders;

public class BlurEffect extends Effect {
  private FrameBuffer helperBuffer;
  private BlurShader shader;
  private int amount;
  private Vector2 resolution = new Vector2();
  private float scale;
  private float strength;

  public BlurEffect(Vector2 resolution) {
    this(resolution, 10, 0.25f, 0.1f);
  }

  public BlurEffect(Vector2 resolution, int amount, float scale, float strength) {
    this.resolution.set(resolution);
    this.amount = amount;
    this.scale = scale;
    this.strength = strength;

    shader = Shaders.get(BlurShader.class);
    helperBuffer = new FloatFrameBuffer((int)resolution.x, (int)resolution.y, false);
  }

  @Override
  public void render(GameRenderer renderer, FrameBuffer inBuffer, FrameBuffer outBuffer) {
    helperBuffer.begin();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    shader.begin(null, null, resolution, amount, scale, strength);
    inBuffer.getColorBufferTexture().bind(0);
    shader.setOrientationUniform(0);
    shader.render(renderer.getScreenQuadRenderable());

    helperBuffer.end();

    // mix with vertical blur
    if (outBuffer != null) outBuffer.begin();
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    helperBuffer.getColorBufferTexture().bind(0);
    shader.setOrientationUniform(1);
    shader.render(renderer.getScreenQuadRenderable());
    shader.end();

    if (outBuffer != null) outBuffer.end();
  }

  @Override
  public void dispose() {
    helperBuffer.dispose();
  }
}
