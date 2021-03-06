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
public class FXAAShader implements Shader {
    ShaderProgram program;
    Vector2 resolution = new Vector2();
    int u_sampler0;
    int u_resolution;

    @Override
    public void init() {
        String vert = Gdx.files.internal("shaders/image.vert.glsl").readString();
        String frag = Gdx.files.internal("shaders/fxaa.frag.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
        u_sampler0 = program.getUniformLocation("u_sampler0");
        u_resolution = program.getUniformLocation("u_resolution");
    }

    public void setResolution(Vector2 resolution) {
        this.resolution.set(resolution);
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
        program.setUniformf(u_resolution, resolution.x, resolution.y);
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
