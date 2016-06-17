package com.habboi.tns.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by w7 on 13/06/2016.
 */
public class BlurShader implements Shader {
  ShaderProgram program;
  int u_sampler0;
  int u_texelSize;
  int u_orientation;
  int u_blurAmount;
  int u_blurScale;
  int u_blurStrength;

  @Override
  public void init() {
    String vert = Gdx.files.internal("shaders/image.vert.glsl").readString();
    String frag = Gdx.files.internal("shaders/blur.frag.glsl").readString();
    program = new ShaderProgram(vert, frag);
    if (!program.isCompiled()) {
      throw new GdxRuntimeException(program.getLog());
    }
    u_sampler0 = program.getUniformLocation("u_sampler0");
    u_texelSize = program.getUniformLocation("u_texelSize");
    u_orientation = program.getUniformLocation("u_orientation");
    u_blurAmount = program.getUniformLocation("u_blurAmount");
    u_blurScale = program.getUniformLocation("u_blurScale");
    u_blurStrength = program.getUniformLocation("u_blurStrength");
  }

  public void setOrientation(int orientation) {
    program.setUniformi(u_orientation, orientation);
  }

  public void setImageSize(Vector2 imageSize) {
    program.setUniformf(u_texelSize, 1.0f/imageSize.x, 1.0f/imageSize.y);
  }

  @Override
  public int compareTo(Shader other) {
    return 0;
  }

  @Override
  public boolean canRender(Renderable instance) {
    return true;
  }

  @Override
  public void begin(Camera camera, RenderContext context) {
    program.begin();
    program.setUniformi(u_sampler0, 0);
    program.setUniformi(u_blurAmount, 8);
    program.setUniformf(u_blurScale, 1.0f);
    program.setUniformf(u_blurStrength, 0.2f);
  }

  @Override
  public void render(Renderable renderable) {
    renderable.mesh.render(program, renderable.primitiveType);
  }

  @Override
  public void end() {
    program.end();
  }

  @Override
  public void dispose() {
    program.dispose();
  }
}
