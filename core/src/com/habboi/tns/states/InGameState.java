package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.habboi.tns.level.Background;
import com.habboi.tns.Game;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.level.Ship;
import com.habboi.tns.level.ShipCamera;
import com.habboi.tns.level.ShipController;
import com.habboi.tns.level.Level;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.FontManager;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;

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
  GameTweenManager gtm;
  Rect screenRect;

  public InGameState(Game g) {
    super(g);
  }

  @Override
  public void create() {
    orthoCam = new OrthographicCamera();
    orthoCam.setToOrtho(false, game.getWidth(), game.getHeight());
    level = game.getAssetManager().get("map1.json");

    ShipController sc = new ShipController(false);
    game.addInput(sc);
    ship = new Ship(game, level.getShipPos(), sc);

    PerspectiveCamera cam = new PerspectiveCamera(45, game.getWidth(), game.getHeight());
    cam.near = 0.1f;
    cam.far = 1000f;

    shipCam = new ShipCamera(ship, cam);
    tempCamInput = new CameraInputController(cam);
    game.addInput(tempCamInput);

    ArrayList<Color> colors = level.getColors();
    background = new Background(colors.get(0), colors.get(1), 30);

    FontManager fm = FontManager.get();
    levelCompleteText = new Text(fm.getFont("Neon.ttf", (int)(24 * game.getDensity())),
            "LEVEL COMPLETE", null, Color.WHITE);
    levelCompleteText.getPos().set(game.getWidth() / 2, game.getHeight() / 2);
    levelCompleteText.getColor().a = 0;

    screenRect = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));

    gtm = GameTweenManager.get();
    gtm.register("start", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        return screenRect.getFadeTween(1, 0, 0.5f);
      }

      @Override
      public void onComplete() {
        ship.state = Ship.State.PLAYABLE;
      }
    }).register("level_complete", new GameTweenManager.GameTween() {

      @Override
      public Tween tween() {
        return Tween.to(levelCompleteText, Text.Accessor.TWEEN_ALPHA, 1f)
                .target(1);
      }

      @Override
      public void onComplete() {

      }
    }).register("reset", new GameTweenManager.GameTween() {

      @Override
      public Tween tween() {
        return screenRect.getFadeTween(0, 1, 0.5f);
      }

      @Override
      public void onComplete() {
        reset();
      }
    });

    gtm.start("start");
  }

  private void reset() {
    ship.reset();
    shipCam.reset();
    level.reset();
    gtm.start("start");
  }

  @Override
  public void update(float dt) {
    if (ship.state == Ship.State.ENDED) {
      if (!gtm.played("level_complete")) {
        gtm.start("level_complete");
      }
    } else if (ship.state == Ship.State.FELL) {
      if (!gtm.isActive("reset")) {
        gtm.start("reset");
      }
    }

    level.update(ship, dt);
    background.update(ship.pos, -ship.vel.z, dt);
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

    ShapeRenderer sr = game.getShapeRenderer();
    screenRect.draw(sr);
  }

  @Override
  public void dispose() {
    level.dispose();
  }
}
