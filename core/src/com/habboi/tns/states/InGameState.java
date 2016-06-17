package com.habboi.tns.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.habboi.tns.Background;
import com.habboi.tns.Game;
import com.habboi.tns.Ship;
import com.habboi.tns.ShipCamera;
import com.habboi.tns.ShipController;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;

import java.util.ArrayList;

/**
 * In-game state.
 */
public class InGameState extends GameState {
  Level level;
  Ship ship;
  ShipCamera shipCam;
  Background background;

  public InGameState(Game g) {
    super(g);
  }

  @Override
  public void create() {
    level = LevelLoader.load("map1.json");

    ShipController sc = new ShipController(false);
    game.addInput(sc);
    ship = new Ship(level.getShipPos(), sc);

    PerspectiveCamera cam = new PerspectiveCamera(45, game.getWidth(), game.getHeight());
    shipCam = new ShipCamera(ship, cam);

    ArrayList<Color> colors = level.getColors();
    background = new Background(colors.get(0), colors.get(1), 30);
  }

  @Override
  public void update(float dt) {
    level.update(ship, dt);
    background.update(ship, dt);
    ship.update(dt);
    shipCam.update(dt);
  }

  @Override
  public void render() {
    game.getRenderer().begin(shipCam.getCam());
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