package com.habboi.tns.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CustomShader implements Shader {
    static final int NUM_POINT_LIGHTS = 64;
    static final int NUM_DIRECTIONAL_LIGHTS = 1;

    Matrix4 _matrix = new Matrix4();
    ShaderProgram program;
    Camera camera;
    int u_worldTrans;
    int u_normalTrans;
    int u_projViewTrans;
    int u_diffuseColor;
    int u_ambientLight;

    @Override
    public void init() {
        String vert = Gdx.files.internal("shaders/lit.vert.glsl").readString();
        String frag = Gdx.files.internal("shaders/lit.frag.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }

        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_normalTrans = program.getUniformLocation("u_normalTrans");
        u_projViewTrans = program.getUniformLocation("u_projViewTrans");
        u_diffuseColor = program.getUniformLocation("u_diffuseColor");
        u_ambientLight = program.getUniformLocation("u_ambientLight");
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
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void render(Renderable renderable) {
        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
        program.setUniformMatrix(u_normalTrans, _matrix.set(renderable.worldTransform).inv().trans());

        ColorAttribute colorAttr = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
        if (colorAttr != null)
            program.setUniformf(u_diffuseColor, colorAttr.color);
        else
            program.setUniformf(u_diffuseColor, Color.WHITE);

        renderable.mesh.render(program, renderable.primitiveType);
    }

    @Override
    public void end() {
        program.end();
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        program.dispose();
    }

    static class DirectionalLightUniform {
        public int dir;
        public int color;
        public int intensity;
    }

    static class PointLightUniform {
        public int color;
        public int pos;
        public int range;
        public int intensity;
        public int constAtt;
        public int linearAtt;
        public int expAtt;
    }
}
