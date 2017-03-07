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
    Environment environment;
    int u_worldTrans;
    int u_normalTrans;
    int u_projViewTrans;
    int u_diffuseColor;
    int u_ambientLight;
    DirectionalLightUniform[NUM_DIRECTIONAL_LIGHTS] u_directionalLights = new DirectionalLightUniform[NUM_DIRECTIONAL_LIGHTS];
    PointLightUniform[NUM_POINT_LIGHTS] u_pointLights = new PointLightUniform[NUM_POINT_LIGHTS];

    public CustomShader(Environment environment) {
        this.environment = environment;
    }

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

        for (int i = 0; i < NUM_POINT_LIGHTS; i++) {
            u_pointLights[i] = new PointLightUniform();
            u_pointLights[i].color = program.getUniformLocation("u_pointLights[" + i + "].color");
            u_pointLights[i].pos = program.getUniformLocation("u_pointLights[" + i + "].pos");
            u_pointLights[i].range = program.getUniformLocation("u_pointLights[" + i + "].range");
            u_pointLights[i].intensity = program.getUniformLocation("u_pointLights[" + i + "].intensity");
            u_pointLights[i].constAtt = program.getUniformLocation("u_pointLights[" + i + "].constAtt");
            u_pointLights[i].linearAtt = program.getUniformLocation("u_pointLights[" + i + "].linearAtt");
            u_pointLights[i].expAtt = program.getUniformLocation("u_pointLights[" + i + "].expAtt");
        }

        for (int i = 0; i < NUM_DIRECTIONAL_LIGHTS; i++) {
            u_directionalLights[i] = new DirectionalLightUniform();
            u_directionalLights[i].color = program.getUniformLocation("u_directionalLights[" + i + "].color");
            u_directionalLights[i].dir = program.getUniformLocation("u_directionalLights[" + i + "].dir");
            u_directionalLights[i].intensity = program.getUniformLocation("u_directionalLights[" + i + "].intensity");
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
        program.setUniformMatrix(u_projViewTrans, camera.combined);

        bindLights();

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void bindLights() {
        final DirectionalLightsAttribute dla = environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;
        final PointLightsAttribute pla = environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> points = pla == null ? null : pla.lights;

        if (dirs != null)
            for (int i = 0; i < dirs.size; i++) {
                DirectionalLight light = dirs.get(i);
                program.setUniformf(u_directionalLights[i].color, light.color.r, light.color.g, light.color.b, light.color.a);
                program.setUniformf(u_directionalLights[i].dir, light.direction.x, light.direction.y, light.direction.z);
                program.setUniformf(u_directionalLights[i].intensity, 0.5f);
            }

        if (points != null)
            for (int i = 0; i < points.size; i++) {
                PointLight light = points.get(i);
                program.setUniformf(u_pointLights[i].color, light.color.r, light.color.g, light.color.b, light.color.a);
                program.setUniformf(u_pointLights[i].pos, light.direction.x, light.direction.y, light.direction.z);
                program.setUniformf(u_pointLights[i].range, 0.5f);
                program.setUniformf(u_pointLights[i].intensity, 0.5f);
                program.setUniformf(u_pointLights[i].constAtt, 0.5f);
                program.setUniformf(u_pointLights[i].linearAtt, 0.5f);
                program.setUniformf(u_pointLights[i].expAtt, 0.5f);
            }
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
