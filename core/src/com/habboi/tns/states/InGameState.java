package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
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

import java.text.DecimalFormat;

import aurelienribon.tweenengine.Tween;

/**
 * In-game state.
 */
public class InGameState extends GameState {
  int debugCam;

  String levelName;
  OrthographicCamera orthoCam;
  CameraInputController tempCamInput;
  Level level;
  Ship ship;
  ShipCamera shipCam;
  Text levelCompleteText;
  Text raceTimeText;
  GameTweenManager gtm;
  Rect screenRect;

  public InGameState(Game g) {
    this(g, "map1.tl");
  }

  public InGameState(Game g, String levelName) {
    super(g);
    this.levelName = levelName;
  }

  @Override
  public void create() {
    orthoCam = new OrthographicCamera();
    orthoCam.setToOrtho(false, game.getWidth(), game.getHeight());
    level = game.getAssetManager().get(levelName);
    level.getWorld().reset();

    ShipController sc = new ShipController(false);
    ship = new Ship(game, level.getShipPos(), sc);

    PerspectiveCamera cam = new PerspectiveCamera(45, game.getWidth(), game.getHeight());
    cam.near = 0.1f;
    cam.far = 1000f;

    shipCam = new ShipCamera(ship, cam);
    tempCamInput = new CameraInputController(cam);

    FontManager fm = FontManager.get();
    levelCompleteText = new Text(fm.getFont("Neon.ttf", 36),
            "level complete", null, Color.WHITE);
    levelCompleteText.getPos().set(game.getWidth() / 2, game.getHeight() / 2 + 36/2 + 10);
    levelCompleteText.getColor().a = 0;
    raceTimeText = new Text(fm.getFont("Neon.ttf", 36), "", null, Color.WHITE);
    raceTimeText.getPos().set(game.getWidth() / 2, game.getHeight() / 2 - 36/2 - 10);
    raceTimeText.getColor().a = 0;
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
    }).register("level_complete", new GameTweenManager.GameTween(new String[]{"race_time_text"}) {

      @Override
      public Tween tween() {
        return Tween.to(levelCompleteText, Text.Accessor.TWEEN_ALPHA, 1f)
                .target(1);
      }

      @Override
      public void onComplete() {

      }
    }).register("race_time_text", new GameTweenManager.GameTween() {
      @Override
      public Tween tween() {
        return Tween.to(raceTimeText, Text.Accessor.TWEEN_ALPHA, 1f)
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
    level.getWorld().reset();
    gtm.start("start");
  }

  @Override
  public boolean keyDown(int keycode) {
    return ship.getController().keyDown(keycode);
  }

  @Override
  public boolean keyUp(int keycode) {
    return ship.getController().keyUp(keycode);
  }

  @Override
  public void update(float dt) {
    if (ship.state == Ship.State.ENDED) {
      if (!gtm.played("level_complete")) {
        DecimalFormat format = new DecimalFormat("00.00");
        raceTimeText.setValue("time " + format.format(ship.raceTime), true);
        gtm.start("level_complete");
      } else if (!gtm.isActive("level_complete")) {
        if (ship.getController().isAnyKeyDown()) {
          game.popState();
        }
      }
    } else if (ship.state == Ship.State.FELL) {
      if (!gtm.isActive("reset")) {
        gtm.start("reset");
      }
    }

    level.update(ship, dt);
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
    level.render(gr);
    ship.render(gr);
    gr.end();
    orthoCam.update();
    if (ship.state == Ship.State.ENDED) {
      gr.beginOrtho(orthoCam.combined);
      levelCompleteText.draw(gr.getSpriteBatch(), true);
      raceTimeText.draw(gr.getSpriteBatch(), true);
      gr.endOrtho();
    }
    ShapeRenderer sr = game.getShapeRenderer();
    screenRect.draw(sr);
  }

  @Override
  public void dispose() {
    level.dispose();
  }
}
