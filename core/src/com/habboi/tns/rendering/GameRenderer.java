package com.habboi.tns.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.rendering.effects.*;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.worlds.World;


public class GameRenderer implements Disposable {
    public static final float FOV = 45f;

    public static final int GraphicLevelNice = 0;
    public static final int GraphicLevelFast = 1;

    public static final int RenderPassBody = 0;
    public static final int RenderPassOutline = 1;
    public static final int RenderPassEffect = 2;

    public static int Width = 0;
    public static int Height = 0;

    Game game;
    Environment environment;
    ModelBatch batch;
    LevelRenderer levelRenderer;
    PostProcessor postProcessor;
    Vector2 resolution;
    Rect screenRect;
    ModelInstance screenQuad;
    Renderable screenQuadRenderable = new Renderable();
    WorldRenderer worldRenderer;
    ShaderEffect fxaa;
    BlurEffect blur;
    BloomEffect bloom;

    public GameRenderer(Game g) {
        game = g;
        Width = g.getWidth();
        Height = g.getHeight();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        batch = new ModelBatch();
        levelRenderer = new LevelRenderer();
        worldRenderer = new WorldRenderer();

        resolution = new Vector2(game.getWidth(), game.getHeight());
        screenRect = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
        screenQuad = new ModelInstance(createScreenQuadModel());
        screenQuad.getRenderable(screenQuadRenderable);

        bloom = new BloomEffect(new Vector2(resolution.x / 2, resolution.y / 2));
        fxaa = new FXAAEffect(resolution);
        blur = new BlurEffect(resolution);

        postProcessor = new PostProcessor(this, new Vector2(resolution.x, resolution.y));
        postProcessor.addEffect(bloom);
        postProcessor.addEffect(fxaa);

        Gdx.gl.glLineWidth(1);
    }

    private Model createScreenQuadModel() {
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

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public WorldRenderer getWorldRenderer() {
        return worldRenderer;
    }

    public Rect getScreenRect() {
        return screenRect;
    }

    public Renderable getScreenQuadRenderable() {
        return screenQuadRenderable;
    }

    public void render(Level level, World world) {
        postProcessor.begin();
        batch.begin(worldRenderer.getCamera());
        worldRenderer.render(world, game.getSpriteBatch(), batch, environment);
        batch.setCamera(levelRenderer.getCamera());

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        levelRenderer.render(level, batch, environment);
        batch.end();
        postProcessor.end();
    }

    public void render(World world) {
        postProcessor.begin();

        batch.begin(worldRenderer.getCamera());
        worldRenderer.render(world, game.getSpriteBatch(), batch, environment);
        batch.end();

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        postProcessor.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        postProcessor.dispose();
    }
}
