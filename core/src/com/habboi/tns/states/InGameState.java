package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.habboi.tns.Game;
import com.habboi.tns.Ship;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;

/**
 * In-game state.
 */
public class InGameState extends GameState {
  PerspectiveCamera cam;
  CameraInputController camController;
  ModelBatch modelBatch;
  Environment environment;
  Level level;
  Ship ship;

  public InGameState(Game g) {
    super(g);
  }

  @Override
  public void create() {
    modelBatch = new ModelBatch();
    cam = new PerspectiveCamera(45, game.getWidth(), game.getHeight());
    cam.near = 0.1f;
    cam.far = 1000f;
    cam.position.set(0, 10f, 10f);
    cam.lookAt(0, 0, 0);

    camController = new CameraInputController(cam);
    Gdx.input.setInputProcessor(camController);

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    level = LevelLoader.load("map1.json");
  }

  @Override
  public void update(float dt) {
    level.update(ship, dt);
    ship.update(dt);
  }

  @Override
  public void render() {
    //Gdx.gl.glEnable(GL20.GL_BLEND);
    //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    camController.update();

    modelBatch.begin(cam);
    level.render(modelBatch, environment);
    modelBatch.end();

    //Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  @Override
  public void dispose() {
    modelBatch.dispose();
    level.dispose();
  }
}
