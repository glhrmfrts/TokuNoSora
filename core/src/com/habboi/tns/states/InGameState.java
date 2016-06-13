package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.habboi.tns.Game;
import com.habboi.tns.Ship;
import com.habboi.tns.ShipController;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;

/**
 * In-game state.
 */
public class InGameState extends GameState {
  CameraInputController camController;
  Level level;
  Ship ship;

  public InGameState(Game g) {
    super(g);
  }

  @Override
  public void create() {
    camController = new CameraInputController(game.getRenderer().getCam());
    game.addInput(camController);

    level = LevelLoader.load("map1.json");

    ShipController sc = new ShipController(false);
    ship = new Ship(level.getShipPos(), sc);
    game.addInput(sc);
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

    game.getRenderer().begin();
    level.render(game.getRenderer());
    ship.render(game.getRenderer());
    game.getRenderer().end();

    //Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  @Override
  public void dispose() {
    level.dispose();
  }
}