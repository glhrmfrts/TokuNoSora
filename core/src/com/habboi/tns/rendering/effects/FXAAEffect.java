package com.habboi.tns.rendering.effects;

import com.badlogic.gdx.math.Vector2;
import com.habboi.tns.rendering.shaders.FXAAShader;
import com.habboi.tns.utils.Shaders;

public class FXAAEffect extends ShaderEffect {

  public FXAAEffect(Vector2 resolution) {
    super(Shaders.get(FXAAShader.class));

    FXAAShader fxaa = (FXAAShader)shader;
    fxaa.setResolution(resolution);
  }
}
