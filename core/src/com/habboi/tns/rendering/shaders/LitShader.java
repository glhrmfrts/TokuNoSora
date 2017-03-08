package com.habboi.tns.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

/*
 * Provides per-fragment lighting, as libgdx's DefaultShader
 * gives us only per-vertex lighting.
 */
public class LitShader extends DefaultShader {

    public LitShader(Renderable r, Config c) {
        super(r, c);
    }

    private void bindCustomLights(Renderable renderable) {

    }

    @Override
    public void render(Renderable renderable) {
        if (!renderable.material.has(BlendingAttribute.Type))
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        bindMaterial(renderable);
        if (lighting) bindCustomLights(renderable);
        super.render(renderable);
    }

    public static class Provider extends BaseShaderProvider {
        @Override
        protected Shader createShader(Renderable renderable) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shaders/lit.vert.glsl").readString(),
                    Gdx.files.internal("shaders/lit.frag.glsl").readString()
            );

            config.numPointLights = renderable.environment.pointLights.size;
            return new DefaultShader(renderable, config);
        }
    }
}
