package com.habboi.tns.rendering.shaders;

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
    Vector2 texelSize = new Vector2();
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

    public void setImageSize(Vector2 imageSize) {
        texelSize.set(1.0f / imageSize.x, 1.0f / imageSize.y);
    }

    public void setOrientationUniform(int orientation) {
        program.setUniformi(u_orientation, orientation);
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
        program.setUniformf(u_texelSize, texelSize.x, texelSize.y);
    }

    public void begin(Camera camera, RenderContext context, Vector2 resolution, int amount, float scale, float strength) {
        program.begin();
        program.setUniformi(u_sampler0, 0);
        program.setUniformi(u_blurAmount, amount);
        program.setUniformf(u_blurScale, scale);
        program.setUniformf(u_blurStrength, strength);
        program.setUniformf(u_texelSize, 1.0f/resolution.x, 1.0f/resolution.y);
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
