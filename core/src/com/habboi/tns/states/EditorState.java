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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.rendering.Renderer;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.ui.Button;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.utils.InputManager;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;


public class EditorState extends GameState {
    public enum Mode {
      TILE, TUNNEL, TWT
    }

    static final float VEL = MenuState.VEL;
    static final float CAM_DIST = MenuState.CAM_DIST;
    static final float CAM_Y = 2;
    static final float CAM_LOOKAT_Y = -2;
    static final float WORLD_DIST = 6;

    Mode mode = Mode.TILE;
    Vector3 bgPos = new Vector3();
    Level level;
    OrthographicCamera orthoCam;
    World world;
    PerspectiveCamera worldCam;
    Grid grid;
    UI ui;
    BoundingBox gridBounds = new BoundingBox();
    Vector3 gbMin = new Vector3();
    Vector3 gbMax = new Vector3();
    Vector3 intersection = new Vector3();
    ModelInstance newTileModel;
    ModelInstance newTunnelModel;
    ModelInstance newObjectModel;
    Vector3 newObjectPos = new Vector3();
    Vector3 newObjectSize = new Vector3();
    Scene scene;
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

        scene = new Scene(game.getWidth(), game.getHeight());
        level.addToScene(scene);

        worldCam = scene.getCamera();
        worldCam.position.x = 0;
        worldCam.position.y = CAM_Y;
        worldCam.position.z = 0;

        newTileModel = new ModelInstance(Models.createTileModel(world.colors, Models.solidTileIndices(0)));
        Models.setColor(newTileModel, ColorAttribute.Diffuse, new Color(0x00000077));
        scene.add(new Fragment(newTileModel));

        newTunnelModel = new ModelInstance(Models.createTunnelModel(world.colors, new int[]{0, 0}));
        Models.setColor(newTunnelModel, ColorAttribute.Diffuse, new Color(0x00000077));
        scene.add(new Fragment(newTunnelModel));

        grid = new Grid(20, 50);
        ui = new UI(this);
    }

    public void setMode(Mode m) {
        mode = m;
        switch (m) {
          case TILE:
            newObjectModel = newTileModel;
            newObjectPos.set((float)Math.floor(newObjectPos.x), (float)Math.floor(newObjectPos.y), (float)Math.floor(newObjectPos.z));
            newObjectSize.set(1, TileShape.TILE_HEIGHT, 1);
            break;
          case TUNNEL:
            newObjectModel = newTunnelModel;
            newObjectPos.set((float)Math.floor(newObjectPos.x) + 0.5f, (float)Math.floor(newObjectPos.y) + 0.25f, (float)Math.floor(newObjectPos.z));
            newObjectSize.set(1, 1, 1);
            break;
        }
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
        Ray ray = worldCam.getPickRay(InputManager.getInstance().screenX, InputManager.getInstance().screenY);

        for (int z = 0; z < grid.depth; z++) {
            for (int x = 0; x < grid.width; x++) {
                gridBounds.set(gbMin.set(x, grid.y, -z - 1), gbMax.set(x + 1, grid.y + 1, -z));
                if (Intersector.intersectRayBounds(ray, gridBounds, intersection)) {
                    handleGridIntersection(x, z);
                    break;
                }
            }
        }

        if (InputManager.getInstance().isButtonJustDown(InputManager.Action1)) {
            grid.setY(grid.y - TileShape.TILE_HEIGHT);
        } else if (InputManager.getInstance().isButtonJustDown(InputManager.Action2)) {
            grid.setY(grid.y + TileShape.TILE_HEIGHT);
        }
    }

    private void handleGridIntersection(int x, int z) {
        boolean isDown = InputManager.getInstance().isTouchingDown();
        switch (mode) {
          case TILE:
            if (!isDown) {
              newObjectPos.set(x, grid.y, z);
              newObjectSize.set(1, TileShape.TILE_HEIGHT, 1);
            } else {
              if (x < (int) newObjectPos.x || z < (int) newObjectPos.z) return;

              newObjectSize.set(x - newObjectPos.x + 1, TileShape.TILE_HEIGHT, z - newObjectPos.z + 1);
            }
            break;

          case TUNNEL:
            if (!isDown) {
              newObjectPos.set(x + 0.5f, grid.y + 0.25f, z);
              newObjectSize.set(1, 1, 1);
            } else {
              if (z < (int)newObjectPos.z) return;

              newObjectSize.set(1, 1, z - newObjectPos.z + 1);
            }
            break;
        }
    }

    @Override
    public void update(float dt) {
        handleCameraInput(dt);
        handleGridInput(dt);

        if (InputManager.getInstance().isButtonDown(InputManager.Back)) {
            ui.dispose();
            game.popState();
        }

        worldCam.lookAt(worldCam.position.x, CAM_LOOKAT_Y, worldCam.position.z - camDist);

        bgPos.set(worldCam.position);
        bgPos.y -= WORLD_DIST;

        world.setCenterX(worldCam.position.x);
        world.update(bgPos, VEL, dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        worldCam.update();

        newObjectModel.transform.setToTranslationAndScaling(
                newObjectPos.x, newObjectPos.y, -newObjectPos.z,
                newObjectSize.x, newObjectSize.y, newObjectSize.z
        );

        // need to render things manually
        Renderer gr = game.getRenderer();
        gr.render(scene);

        ui.render();
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
    float y;
    Model horizontalLineModel;
    Model verticalLineModel;
    ModelInstance planeInstance;
    Array<ModelInstance> lines = new Array<>();
    Vector3 translation = new Vector3();

    public Grid(int width, int depth) {
        this.width = width;
        this.depth = depth;
        this.y = 0;
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

    public void addToScene(Scene scene) {
        scene.add(new Fragment(planeInstance));
      for (ModelInstance line : lines) {
            scene.add(new Fragment(line));
      }
    }

  public void setY(float y) {
        this.y = y;

        planeInstance.transform.getTranslation(translation);
        planeInstance.transform.setTranslation(translation.x, y - 0.025f, translation.z);

        for (ModelInstance line : lines) {
            line.transform.getTranslation(translation);
            line.transform.setTranslation(translation.x, y, translation.z);
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
}

class UI {
    static final Color BUTTON_COLOR = new Color(0, 0, 0, 0.75f);
    static final Color BUTTON_PRESSED_COLOR = new Color(0, 0.55f, 0.68f, 1);
    static final Color BUTTON_SELECTED_COLOR = new Color(0, 0.65f, 0.78f, 1);
    Stage stage;
    EditorState state;
    Array<Button> modeButtons = new Array<>();

    public UI(EditorState state) {
        this.state = state;
        this.stage = new Stage(new ScreenViewport(), state.game.getSpriteBatch());

        FontManager fm = FontManager.get();
        HorizontalGroup modeGroup = new HorizontalGroup();
        modeGroup.setBounds(0, 0, Gdx.graphics.getWidth(), 30);
        modeGroup.setPosition(10, 10);
        modeGroup.space(5).pad(2);

        final Button tileMode = new Button(
                state.game.getShapeRenderer(),
                new Text(
                        fm.getFont(Game.MAIN_FONT, Game.SMALL_FONT_SIZE),
                        "tile",
                        null,
                        Color.WHITE
                ),
                20,
                5,
                BUTTON_COLOR,
                BUTTON_PRESSED_COLOR
        );
        tileMode.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent ev, float x, float y) {
            selectModeButton(tileMode, EditorState.Mode.TILE);
          }
        });
        final Button tunnelMode = new Button(
                state.game.getShapeRenderer(),
                new Text(
                        fm.getFont(Game.MAIN_FONT, Game.SMALL_FONT_SIZE),
                        "tunnel",
                        null,
                        Color.WHITE
                ),
                20,
                5,
                BUTTON_COLOR,
                BUTTON_PRESSED_COLOR
        );
        tunnelMode.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent ev, float x, float y) {
            selectModeButton(tunnelMode, EditorState.Mode.TUNNEL);
          }
        });
        final Button twtMode = new Button(
                state.game.getShapeRenderer(),
                new Text(
                        fm.getFont(Game.MAIN_FONT, Game.SMALL_FONT_SIZE),
                        "tile+tunnels",
                        null,
                        Color.WHITE
                ),
                20,
                5,
                BUTTON_COLOR,
                BUTTON_PRESSED_COLOR
        );
        twtMode.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent ev, float x, float y) {
            selectModeButton(twtMode, EditorState.Mode.TWT);
          }
        });
        modeButtons.add(tileMode);
        modeButtons.add(tunnelMode);
        modeButtons.add(twtMode);
        modeGroup.addActor(tileMode);
        modeGroup.addActor(tunnelMode);
        modeGroup.addActor(twtMode);
        stage.addActor(modeGroup);
        InputManager.getInstance().addInputProcessor(stage);

        selectModeButton(tileMode, EditorState.Mode.TILE);
    }

    private void selectModeButton(Button btn, EditorState.Mode mode) {
        for (Button b : modeButtons) {
            b.setColor(BUTTON_COLOR);
        }
        btn.setColor(BUTTON_SELECTED_COLOR);
        state.setMode(mode);
    }

    public void render() {
        stage.draw();
    }

    public void dispose() {
        InputManager.getInstance().removeInputProcessor(stage);
        stage.dispose();
    }
}