package com.habboi.tns.states;

import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.habboi.tns.Background;
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
  Background background;

  public InGameState(Game g) {
    super(g);
  }

  @Override
  public void create() {
    camController = new CameraInputController(game.getRenderer().getCam());
    game.addInput(camController);

    level = LevelLoader.load("map1.json");

    ShipController sc = new ShipController(false);
    game.addInput(sc);
    ship = new Ship(level.getShipPos(), sc);

    background = new Background(10);
  }

  @Override
  public void update(float dt) {
    level.update(ship, dt);
    background.update(ship, dt);
    ship.update(dt);
  }

  @Override
  public void render() {
    camController.update();

    game.getRenderer().begin();
    background.render(game.getRenderer());
    level.render(game.getRenderer());
    ship.render(game.getRenderer());
    game.getRenderer().end();
  }

  @Override
  public void dispose() {
    level.dispose();
  }
}