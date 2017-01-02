package com.habboi.tns.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by w7 on 20/06/2016.
 */
public class Basic2DShader implements Shader {
  ShaderProgram program;
  Camera camera;

  public ShaderProgram getShaderProgram() {
    return program;
  }

  @Override
  public void init() {
    String vert = Gdx.files.internal("shaders/basic2d.vert.glsl").readString();
    String frag = Gdx.files.internal("shaders/basic2d.frag.glsl").readString();
    program = new ShaderProgram(vert, frag);
    if (!program.isCompiled()) {
      throw new GdxRuntimeException(program.getLog());
    }
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
    this.camera = camera;
    program.begin();
  }

  @Override
  public void render(Renderable renderable) {
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
