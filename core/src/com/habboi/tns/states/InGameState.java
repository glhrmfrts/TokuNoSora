package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.habboi.tns.Background;
import com.habboi.tns.Game;
import com.habboi.tns.GameTweenManager;
import com.habboi.tns.Resource;
import com.habboi.tns.Ship;
import com.habboi.tns.ShipCamera;
import com.habboi.tns.ShipController;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.Text;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * In-game state.
 */
public class InGameState extends GameState {
  int debugCam;

  OrthographicCamera orthoCam;
  CameraInputController tempCamInput;
  Level level;
  Ship ship;
  ShipCamera shipCam;
  Background background;
  Text levelCompleteText;

  public InGameState(Game g) {
    super(g);
  }

  @Override
  public void create() {
    orthoCam = new OrthographicCamera();
    orthoCam.setToOrtho(false, game.getWidth(), game.getHeight());
    level = LevelLoader.load("map1.json");

    ShipController sc = new ShipController(false);
    game.addInput(sc);
    ship = new Ship(level.getShipPos(), sc);

    PerspectiveCamera cam = new PerspectiveCamera(45, game.getWidth(), game.getHeight());
    cam.near = 0.1f;
    cam.far = 1000f;

    shipCam = new ShipCamera(ship, cam);
    tempCamInput = new CameraInputController(cam);
    game.addInput(tempCamInput);

    ArrayList<Color> colors = level.getColors();
    background = new Background(colors.get(0), colors.get(1), 30);

    levelCompleteText = new Text(Resource.getFont("mine.ttf", 32),
            "LEVEL COMPLETE", null, Color.WHITE);
    levelCompleteText.getPos().set(game.getWidth() / 2, game.getHeight() / 2);
    levelCompleteText.getColor().a = 0;
  }

  @Override
  public void update(float dt) {
    if (ship.state == Ship.State.ENDED) {
      if (!GameTweenManager.contains("level_complete")) {
        GameTweenManager.start("level_complete");
        Tween.to(levelCompleteText, Text.Accessor.TWEEN_ALPHA, 1f)
                .target(1)
                .setCallback(new TweenCallback() {
                  @Override
                  public void onEvent(int i, BaseTween<?> baseTween) {
                    GameTweenManager.end("level_complete");
                  }
                }).start(GameTweenManager.getTweenManager());
      }
    }

    level.update(ship, dt);
    background.update(ship, dt);
    ship.update(dt);

    if (debugCam == 0) {
      shipCam.update(dt);
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
      debugCam = (debugCam == 0) ? 1 : 0;
    }
  }

  @Override
  public void render() {
    GameRenderer gr = game.getRenderer();
    if (debugCam == 0) {
      gr.begin(shipCam.getCam());
    } else {
      tempCamInput.update();
      gr.begin(tempCamInput.camera);
    }

    background.render(gr);
    level.render(gr);
    ship.render(gr);
    gr.end();

    orthoCam.update();
    gr.beginOrtho(orthoCam.combined);
    levelCompleteText.draw(gr.getSpriteBatch(), true);
    gr.endOrtho();
  }

  @Override
  public void dispose() {
    level.dispose();
  }
}