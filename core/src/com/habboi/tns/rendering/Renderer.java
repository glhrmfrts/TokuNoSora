package com.habboi.tns.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.rendering.shaders.BasicShader;
import com.habboi.tns.rendering.shaders.BlurShader;
import com.habboi.tns.rendering.shaders.FXAAShader;
import com.habboi.tns.rendering.shaders.GlowShader;
import com.habboi.tns.ui.Rect;

import java.util.ArrayList;

public class Renderer implements Disposable {
    public static final float FOV = 45f;

    public static final int GraphicLevelNice = 0;
    public static final int GraphicLevelFast = 1;

    Game game;
    Environment environment;
    ModelBatch batch;
    SpriteBatch sb;
    Renderable tmpRenderable = new Renderable();
    FrameBuffer blurTempBuffer;
    BasicShader shaderBasic;
    GlowShader shaderGlow;
    BlurShader shaderBlur;
    FXAAShader shaderFXAA;
    Vector2 resolution;
    Rect screenRect;
    ModelInstance screenSurface;
    ArrayList<FrameBuffer> depthBuffers = new ArrayList<>();
    ArrayList<FrameBuffer> textureBuffers = new ArrayList<>();
    Array<Fragment> occludingFragments = new Array<>();
    Array<Fragment> glowingFragments = new Array<>();

    public Renderer(Game g) {
        game = g;

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        batch = new ModelBatch();
        sb = new SpriteBatch();

        int buffersWidth = game.getWidth();
        int buffersHeight = game.getHeight();
        depthBuffers.add(new FrameBuffer(Pixmap.Format.RGB888, game.getWidth(), game.getHeight(), true));
        depthBuffers.add(new FrameBuffer(Pixmap.Format.RGBA8888, buffersWidth, buffersHeight, true));
        textureBuffers.add(new FrameBuffer(Pixmap.Format.RGBA8888, buffersWidth, buffersHeight, false));
        textureBuffers.add(new FrameBuffer(Pixmap.Format.RGBA8888, buffersWidth, buffersHeight, false));
        textureBuffers.add(new FrameBuffer(Pixmap.Format.RGBA8888, buffersWidth / 2, buffersHeight / 2, false));
        textureBuffers.add(new FrameBuffer(Pixmap.Format.RGBA8888, buffersWidth / 2, buffersHeight / 2, false));
        textureBuffers.add(new FrameBuffer(Pixmap.Format.RGBA8888, buffersWidth / 2, buffersHeight / 2, false));
        blurTempBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, buffersWidth / 2, buffersHeight / 2, false);

        shaderBasic = new BasicShader();
        shaderBlur = new BlurShader();
        shaderGlow = new GlowShader();
        shaderFXAA = new FXAAShader();
        shaderBasic.init();
        shaderBlur.init();
        shaderGlow.init();
        shaderFXAA.init();
        shaderBlur.begin(null, null);
        shaderBlur.setImageSize(new Vector2(game.getWidth(), game.getHeight()));
        shaderBlur.end();

        resolution = new Vector2(game.getWidth(), game.getHeight());
        screenRect = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
        screenSurface = new ModelInstance(createScreenSurfaceModel());

        shaderFXAA.setResolution(resolution);

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

    public Rect getScreenRect() {
        return screenRect;
    }

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public void clear(Color c) {
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void drawFXAA(FrameBuffer in, FrameBuffer out) {
        if (out != null) out.begin();

        shaderFXAA.begin(null, null);
        in.getColorBufferTexture().bind(0);
        shaderFXAA.render(tmpRenderable);
        shaderFXAA.end();

        if (out != null) out.end();
    }

    private void drawBlur(FrameBuffer in, FrameBuffer out) {
        shaderBlur.begin(null, null);
        drawBlurCommon(in, out);
        shaderBlur.end();
    }

    private void drawBlur(FrameBuffer in, FrameBuffer out, int amount, float scale, float strength) {
        shaderBlur.begin(null, null, resolution, amount, scale, strength);
        drawBlurCommon(in, out);
        shaderBlur.end();
    }

    private void drawBlurCommon(FrameBuffer in, FrameBuffer out) {
        blurTempBuffer.begin();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        in.getColorBufferTexture().bind(0);
        shaderBlur.setOrientationUniform(0);
        shaderBlur.render(tmpRenderable);

        blurTempBuffer.end();

        // mix with vertical blur
        if (out != null) out.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        blurTempBuffer.getColorBufferTexture().bind(0);
        shaderBlur.setOrientationUniform(1);
        shaderBlur.render(tmpRenderable);

        if (out != null) out.end();
    }

    private void drawGlowMap(FrameBuffer in, FrameBuffer in2, FrameBuffer out) {
        if (out != null) out.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shaderGlow.begin(null, null);
        in.getColorBufferTexture().bind(0);
        in2.getColorBufferTexture().bind(1);
        shaderGlow.render(tmpRenderable);
        shaderGlow.end();

        if (out != null) out.end();
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    public void drawGlowingGeometry(Camera cam, FrameBuffer out) {
        if (out != null) out.begin();

        shaderBasic.begin(cam, null);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glColorMask(false, false, false, false);
        for (Fragment f : occludingFragments) {
            f.modelInstance.getRenderable(tmpRenderable);
            shaderBasic.render(tmpRenderable);
        }

        Gdx.gl.glColorMask(true, true, true, true);
        for (Fragment f : glowingFragments) {
            f.modelInstance.getRenderable(tmpRenderable);
            shaderBasic.render(tmpRenderable);
        }
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);

        shaderBasic.end();

        if (out != null) out.end();
    }

    public void render(Scene scene) {
        occludingFragments.clear();
        glowingFragments.clear();

        depthBuffers.get(0).begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        sb.begin();
        sb.draw(scene.getBackgroundTexture(), 0, 0);
        sb.end();

        batch.begin(scene.getCamera());

        for (Fragment fragment : scene.getFragments()) {
            renderFragment(fragment);
        }

        batch.end();
        depthBuffers.get(0).end();

        boolean isNice = GameConfig.get().getGraphicLevel() == GraphicLevelNice;
        if (isNice) {
            drawGlowingGeometry(scene.getCamera(), depthBuffers.get(1));
        }

        // the only renderable used from here on is the screen surface
        screenSurface.getRenderable(tmpRenderable);

        Gdx.gl.glDepthMask(false);
        if (isNice) {
            drawBlur(depthBuffers.get(1), textureBuffers.get(2), 10, 1.5f, 0.2f);
            drawBlur(textureBuffers.get(2), textureBuffers.get(3), 10, 1.5f, 0.2f);
            //drawBlur(textureBuffers.get(3), textureBuffers.get(4), 10, 1.5f, 0.2f);
            drawGlowMap(depthBuffers.get(0), textureBuffers.get(3), textureBuffers.get(1));
            //drawBlur(textureBuffers.get(1), textureBuffers.get(0), 10, 0.25f, 0.1f);
            drawFXAA(textureBuffers.get(1), textureBuffers.get(0));
            drawFXAA(textureBuffers.get(0), null);
        } else {
            drawFXAA(depthBuffers.get(0), null);
        }

        Gdx.gl.glDepthMask(true);
    }

    private void renderFragment(Fragment fragment) {
      if (!fragment.visible()) return;

      if (fragment.modelInstance != null) {
        batch.render(fragment.modelInstance, environment);
        if (fragment.glow()) {
          glowingFragments.add(fragment);
        } else {
          occludingFragments.add(fragment);
        }
      }

      for (Fragment f : fragment.children) {
        renderFragment(f);
      }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shaderGlow.dispose();
        shaderBlur.dispose();
        shaderBasic.dispose();

        for (FrameBuffer buffer : depthBuffers) {
            buffer.dispose();
        }
        for (FrameBuffer buffer : textureBuffers) {
            buffer.dispose();
        }
    }
}
