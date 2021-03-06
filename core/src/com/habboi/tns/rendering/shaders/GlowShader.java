package com.habboi.tns.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GlowShader implements Shader {
  ShaderProgram program;
  int u_sampler0;
  int u_sampler1;
  int u_exposure;

  @Override
  public void init() {
    String vert = Gdx.files.internal("shaders/image.vert.glsl").readString();
    String frag = Gdx.files.internal("shaders/glow.frag.glsl").readString();
    program = new ShaderProgram(vert, frag);
    if (!program.isCompiled()) {
      throw new GdxRuntimeException(program.getLog());
    }
    u_sampler0 = program.getUniformLocation("u_sampler0");
    u_sampler1 = program.getUniformLocation("u_sampler1");
    u_exposure = program.getUniformLocation("u_exposure");
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
    program.setUniformi(u_sampler1, 1);
    program.setUniformf(u_exposure, 3);
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
