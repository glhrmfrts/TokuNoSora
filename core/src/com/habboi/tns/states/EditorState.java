package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.utils.InputManager;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;


public class EditorState extends GameState {
    static final float VEL = MenuState.VEL;
    static final float CAM_DIST = MenuState.CAM_DIST;
    static final float CAM_Y = 2;
    static final float CAM_LOOKAT_Y = -2;
    static final float WORLD_DIST = 6;

    Vector3 bgPos = new Vector3();
    Level level;
    OrthographicCamera orthoCam;
    World world;
    PerspectiveCamera worldCam;
    PerspectiveCamera levelCam;
    Grid grid;
    BoundingBox gridBounds = new BoundingBox();
    Vector3 gbMin = new Vector3();
    Vector3 gbMax = new Vector3();
    Vector3 intersection = new Vector3();
    ModelInstance newTile;
    Vector3 newTilePos = new Vector3();
    Vector3 newTileSize = new Vector3();
    float camDist = CAM_DIST;

    public EditorState(Game g, World world) {
        super(g);
        this.world = world;
    }

    @Override
    public void create() {
        orthoCam = new OrthographicCamera();
        orthoCam.setToOrtho(false, game.getWidth(), game.getHeight());

        level = new Level();
        level.setWorld(world);

        worldCam = game.getRenderer().getWorldRenderer().getCamera();
        worldCam.position.x = 0;
        worldCam.position.y = CAM_Y;
        worldCam.position.z = 0;

        levelCam = game.getRenderer().getLevelRenderer().getCamera();
        levelCam.position.set(worldCam.position);

        grid = new Grid(20, 50);

        newTile = new ModelInstance(Models.createTileModel(world.colors, Models.solidTileIndices(0)));
        Models.setColor(newTile, ColorAttribute.Diffuse, new Color(0x00000077));
    }

    @Override
    public void resume() {
    }

    private void handleCameraInput(float dt) {
        float h = InputManager.getInstance().getAxis(InputManager.Horizontal);
        float v = InputManager.getInstance().getAxis(InputManager.Vertical);
        float lv = InputManager.getInstance().getAxis(InputManager.LookVertical);

        worldCam.position.x += h * VEL * 2 * dt;
        worldCam.position.z -= v * VEL * 2 * dt;

        if (lv != 0) {
            worldCam.position.add(0, worldCam.direction.y * lv * VEL * 2 * dt, worldCam.direction.z * lv * VEL * 2 * dt);
            if (camDist > 0) {
                camDist -= lv * VEL * 2 * dt;
            }
        }
    }

    private void handleGridInput(float dt) {
        Ray ray = levelCam.getPickRay(InputManager.getInstance().screenX, InputManager.getInstance().screenY);

        for (int z = 0; z < grid.depth; z++) {
            for (int x = 0; x < grid.width; x++) {
                gridBounds.set(gbMin.set(x, 0, -z - 1), gbMax.set(x + 1, 1, -z));
                if (Intersector.intersectRayBounds(ray, gridBounds, intersection)) {
                    handleGridIntersection(x, z);
                    break;
                }
            }
        }
    }

    private void handleGridIntersection(int x, int z) {
        if (!InputManager.getInstance().isTouchingDown()) {
            newTilePos.set(x, 0, z);
            newTileSize.set(1, TileShape.TILE_HEIGHT, 1);
        } else {
            if (x < (int)newTilePos.x || z < (int)newTilePos.z) return;

            newTileSize.set(x - newTilePos.x + 1, TileShape.TILE_HEIGHT, z - newTilePos.z + 1);
        }
    }

    @Override
    public void update(float dt) {
        handleCameraInput(dt);
        handleGridInput(dt);

        if (InputManager.getInstance().isButtonDown(InputManager.Back)) {
            game.popState();
        }

        //worldCam.position.y = CAM_Y;
        worldCam.lookAt(worldCam.position.x, CAM_LOOKAT_Y, worldCam.position.z - camDist);
        levelCam.lookAt(worldCam.position.x, CAM_LOOKAT_Y, worldCam.position.z - camDist);

        levelCam.position.set(worldCam.position);
        bgPos.set(worldCam.position);
        bgPos.y -= WORLD_DIST;

        world.setCenterX(worldCam.position.x);
        world.update(bgPos, VEL, dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        worldCam.update();
        levelCam.update();

        // need to render things manually
        GameRenderer gr = game.getRenderer();
        gr.begin(worldCam);

        gr.worldRenderer.render(world, game.getSpriteBatch(), gr.batch, gr.environment);
        gr.batch.setCamera(levelCam);

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        grid.render(gr.batch, gr.environment);

        newTile.transform.setToTranslationAndScaling(
                newTilePos.x, newTilePos.y, -newTilePos.z,
                newTileSize.x, newTileSize.y, newTileSize.z
        );
        gr.batch.render(newTile, gr.environment);

        gr.end();
    }

    @Override
    public void dispose() {
    }
}

class Grid {
    static final float DIST_Y = 3f;
    static final Color LINE_COLOR = new Color(0xaaaaaaaa);
    static final Color PLANE_COLOR = new Color(0x00000077);

    int width, depth;
    Model horizontalLineModel;
    Model verticalLineModel;
    ModelInstance planeInstance;
    Array<ModelInstance> lines = new Array<>();

    public Grid(int width, int depth) {
        this.width = width;
        this.depth = depth;
        createModels();
    }

    private void createModels() {
        planeInstance = new ModelInstance(Models.createPlaneModel(PLANE_COLOR));
        planeInstance.transform.setTranslation(width / 2, -0.025f, -depth / 2);
        planeInstance.transform.scale(width / 2, 1, depth / 2);

        horizontalLineModel = Models.createLineModel(LINE_COLOR, new int[]{0, 0, 0, width, 0, 0});
        verticalLineModel = Models.createLineModel(LINE_COLOR, new int[]{0, 0, 0, 0, 0, -depth});

        for (int z = 0; z < depth + 1; z++) {
          for (int x = 0; x < width + 1; x++) {
              ModelInstance line = new ModelInstance(verticalLineModel);
              line.transform.setTranslation(x, 0, 0);
              lines.add(line);
          }

          ModelInstance line = new ModelInstance(horizontalLineModel);
          line.transform.setTranslation(0, 0, -z);
          lines.add(line);
        }
    }

    public void resize(int width, int depth) {
        this.width = width;
        this.depth = depth;
        planeInstance.model.dispose();
        horizontalLineModel.dispose();
        verticalLineModel.dispose();
        lines.clear();
        createModels();
    }

    public void render(ModelBatch batch, Environment environment) {
        batch.render(planeInstance, environment);
        for (ModelInstance line : lines) {
            batch.render(line, environment);
        }
    }
}