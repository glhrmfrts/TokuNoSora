package com.habboi.tns.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by w7 on 13/06/2016.
 */
public class BasicShader implements Shader {
  ShaderProgram program;
  Camera camera;
  int u_worldTrans;
  int u_projViewTrans;
  int u_diffuseColor;

  @Override
  public void init() {
    String vert = Gdx.files.internal("shaders/basic.vert.glsl").readString();
    String frag = Gdx.files.internal("shaders/basic.frag.glsl").readString();
    program = new ShaderProgram(vert, frag);
    if (!program.isCompiled()) {
      throw new GdxRuntimeException(program.getLog());
    }
    u_worldTrans = program.getUniformLocation("u_worldTrans");
    u_projViewTrans = program.getUniformLocation("u_projViewTrans");
    u_diffuseColor = program.getUniformLocation("u_diffuseColor");
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
    program.setUniformMatrix(u_projViewTrans, camera.combined);

    Gdx.gl.glEnable(GL20.GL_CULL_FACE);
    Gdx.gl.glCullFace(GL20.GL_BACK);
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
  }

  @Override
  public void render(Renderable renderable) {
    program.setUniformMatrix(u_worldTrans, renderable.worldTransform);

    ColorAttribute colorAttr = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
    if (colorAttr != null)
      program.setUniformf(u_diffuseColor, colorAttr.color);

    renderable.mesh.render(program, renderable.primitiveType);
  }

  @Override
  public void end() {
    program.end();
    Gdx.gl.glDisable(GL20.GL_CULL_FACE);
    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
  }

  @Override
  public void dispose() {
    program.dispose();
  }
}
