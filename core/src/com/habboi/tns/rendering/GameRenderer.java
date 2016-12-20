package com.habboi.tns.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.ui.Rect;

import java.util.ArrayList;

/**
 * Encapsulates the rendering context.
 */
public class GameRenderer implements Disposable {
    static final float FOV = 45f;
    static final int GLOWMAP_WIDTH = 512;
    static final int GLOWMAP_HEIGHT = 512;

    Game game;
    PerspectiveCamera worldCam;
    Environment environment;
    ModelBatch batch;
    SpriteBatch sb;
    Renderable tmpRenderable = new Renderable();
    FrameBuffer fboDefault;
    FrameBuffer fboGlow;
    FrameBuffer fboBlur;
    FrameBuffer fboGlobalBlur;
    FrameBuffer fboFxaa;
    BasicShader shaderBasic;
    Basic2DShader shaderBasic2D;
    GlowShader shaderGlow;
    BlurShader shaderBlur;
    FXAAShader shaderFXAA;
    Vector2 resolution;
    Rect screenRect;
    ModelInstance screenSurface;
    ArrayList<ModelInstance> occludingInstances = new ArrayList<>();
    ArrayList<ModelInstance> glowingInstances = new ArrayList<>();

    public GameRenderer(Game g) {
        game = g;

        worldCam = new PerspectiveCamera(FOV, g.getWidth(), g.getHeight());
        worldCam.near = 0.1f;
        worldCam.far = 1000f;

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        batch = new ModelBatch();
        sb = new SpriteBatch();
        fboDefault = new FrameBuffer(Pixmap.Format.RGB888, game.getWidth(), game.getHeight(), true);
        fboGlow = new FrameBuffer(Pixmap.Format.RGBA8888, game.getWidth(), game.getHeight(), true);
        fboBlur = new FrameBuffer(Pixmap.Format.RGBA8888, game.getWidth(), game.getHeight(), false);
        fboGlobalBlur = new FrameBuffer(Pixmap.Format.RGBA8888, game.getWidth(), game.getHeight(), false);
        fboFxaa = new FrameBuffer(Pixmap.Format.RGBA8888, game.getWidth(), game.getHeight(), false);

        shaderBasic2D = new Basic2DShader();
        shaderBasic = new BasicShader();
        shaderBlur = new BlurShader();
        shaderGlow = new GlowShader();
        shaderFXAA = new FXAAShader();
        shaderBasic2D.init();
        shaderBasic.init();
        shaderBlur.init();
        shaderGlow.init();
        shaderFXAA.init();
        shaderBlur.begin(null, null);
        shaderBlur.setImageSize(new Vector2(GLOWMAP_WIDTH, GLOWMAP_HEIGHT));
        shaderBlur.end();

        resolution = new Vector2(game.getWidth(), game.getHeight());
        screenRect = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
        screenSurface = new ModelInstance(createScreenSurfaceModel());

        Gdx.gl.glLineWidth(2);
    }

    private Model createScreenSurfaceModel() {
        ModelBuilder mb = new ModelBuilder();
        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;
        mb.begin();

        v1 = new VertexInfo().setPos(-1, -1, 0).setUV(0, 0);
        v2 = new VertexInfo().setPos(1, -1, 0).setUV(1, 0);
        v3 = new VertexInfo().setPos(1, 1, 0).setUV(1, 1);
        v4 = new VertexInfo().setPos(-1, 1, 0).setUV(0, 1);
        partBuilder = mb.part("surface", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates,
                              new Material());

        partBuilder.rect(v1, v2, v3, v4);

        return mb.end();
    }

    public PerspectiveCamera getWorldCam() {
        return worldCam;
    }

    public Rect getScreenRect() {
        return screenRect;
    }

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public void begin() {
        occludingInstances.clear();
        glowingInstances.clear();
        fboDefault.begin();
        batch.begin(worldCam);
    }

    public void clear(Color c) {
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public void end() {
        batch.end();

        // draw glowing geometry
        shaderBasic.begin(worldCam, null);
        fboGlow.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glColorMask(false, false, false, false);
        for (ModelInstance inst : occludingInstances) {
            inst.getRenderable(tmpRenderable);
            shaderBasic.render(tmpRenderable);
        }

        Gdx.gl.glColorMask(true, true, true, true);
        for (ModelInstance inst : glowingInstances) {
            inst.getRenderable(tmpRenderable);
            shaderBasic.render(tmpRenderable);
        }
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        shaderBasic.end();

        // the only renderable used from here on is the screen surface
        screenSurface.getRenderable(tmpRenderable);

        // blur the glowmap (disables depth checks and writes)
        Gdx.gl.glDepthMask(false);
        shaderBlur.begin(null, null);
        fboBlur.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fboGlow.getColorBufferTexture().bind(0);
        shaderBlur.setOrientation(0);
        shaderBlur.render(tmpRenderable);

        // mix with vertical blur
        fboGlow.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fboBlur.getColorBufferTexture().bind(0);
        shaderBlur.setOrientation(1);
        shaderBlur.render(tmpRenderable);
        fboGlow.end();
        shaderBlur.end();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // blend the glowmap with the rendered scene
        fboBlur.begin();

        shaderGlow.begin(null, null);
        fboDefault.getColorBufferTexture().bind(0);
        fboGlow.getColorBufferTexture().bind(1);
        shaderGlow.render(tmpRenderable);
        shaderGlow.end();

        fboBlur.end();
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        // global blur
        fboGlobalBlur.begin();
        shaderBlur.begin(null, null, 10, 0.25f, 0.1f);

        fboBlur.getColorBufferTexture().bind(0);
        shaderBlur.setOrientation(0);
        shaderBlur.render(tmpRenderable);

        fboGlobalBlur.end();

        fboFxaa.begin();

        fboGlobalBlur.getColorBufferTexture().bind(0);
        shaderBlur.setOrientation(1);
        shaderBlur.render(tmpRenderable);
        shaderBlur.end();

        fboFxaa.end();

        shaderFXAA.begin(null, null, resolution);
        fboFxaa.getColorBufferTexture().bind(0);
        shaderFXAA.render(tmpRenderable);
        shaderFXAA.end();

        // re-enable depth checks and writes
        Gdx.gl.glDepthMask(true);
    }

    public void setDiffuseColor(ModelInstance instance, Color color) {
        instance.getRenderable(tmpRenderable);
        ColorAttribute colorAttribute = (ColorAttribute) tmpRenderable.material.get(ColorAttribute.Diffuse);
        colorAttribute.color.set(color);
    }

    public void render(ModelInstance instance) {
        batch.render(instance, environment);
        occludingInstances.add(instance);
    }

    public void render(ModelInstance instance, Color color) {
        setDiffuseColor(instance, color);
        batch.render(instance, environment);
        occludingInstances.add(instance);
    }

    public void renderGlow(ModelInstance instance) {
        batch.render(instance, environment);
        glowingInstances.add(instance);
    }

    public void renderGlow(ModelInstance instance, Color color) {
        setDiffuseColor(instance, color);
        batch.render(instance, environment);
        glowingInstances.add(instance);
    }

    public void renderGlowOnly(ModelInstance instance) {
        glowingInstances.add(instance);
    }

    public void renderGlowOnly(ModelInstance instance, Color color) {
        setDiffuseColor(instance, color);
        glowingInstances.add(instance);
    }

    public void beginOrtho() {
        sb.begin();
        sb.setShader(shaderBasic2D.getShaderProgram());
    }

    public void beginOrtho(Matrix4 projection) {
        sb.begin();
        sb.setProjectionMatrix(projection);
        sb.setShader(shaderBasic2D.getShaderProgram());
    }

    public void endOrtho() {
        sb.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shaderGlow.dispose();
        shaderBlur.dispose();
        shaderBasic.dispose();
        fboGlow.dispose();
        fboBlur.dispose();
        fboDefault.dispose();
    }
}
