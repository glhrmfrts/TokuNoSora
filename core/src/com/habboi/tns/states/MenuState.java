package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.level.Background;
import com.habboi.tns.level.Level;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.FontManager;

import java.util.ArrayList;

/**
 * Handles the menu state.
 */
public class MenuState extends FadeState {
  private enum InternalState {
    TITLE
  }

  static final float VEL = 10;
  static final float CAM_DIST = 20;
  static final float CAM_Y = 10;
  static final float CAM_LOOKAT_Y = 6;

  Vector3 bgPos = new Vector3();
  GameTweenManager gtm;
  Background background;
  PerspectiveCamera cam;
  InternalState state;
  Text titleText;

  public MenuState(Game g) {
    super(g);
  }

  @Override
  public void onFadeInComplete() {

  }

  @Override
  public void onFadeOutComplete() {

  }

  @Override
  public void create() {
    super.create();
    state = InternalState.TITLE;
    AssetManager am = game.getAssetManager();
    FontManager fm = game.getFontManager();

    titleText = new Text(
            fm.getFont("Neon.ttf", (int)(64 * game.getDensity())),
            "TAIHO",
            null,
            Color.WHITE
    );
    titleText.getPos().set(game.getWidth()/2, game.getHeight()*0.9f);

    Level firstLevel = am.get("map1.json");
    ArrayList<Color> colors = firstLevel.getColors();
    background = new Background(colors.get(0), colors.get(1), 40);

    cam = new PerspectiveCamera(45, game.getWidth(), game.getHeight());
    cam.near = 0.01f;
    cam.far = 1000f;
    cam.position.set(0, CAM_Y, 0);
    cam.lookAt(0, CAM_LOOKAT_Y, -CAM_DIST);

    Gdx.gl.glClearColor(0, 0, 0, 0);
    fadeIn();
  }

  @Override
  public void update(float dt) {
    super.update(dt);
    cam.position.z -= VEL * dt;
    cam.lookAt(0, CAM_LOOKAT_Y, cam.position.z - CAM_DIST);
    bgPos.set(cam.position);
    bgPos.y -= CAM_LOOKAT_Y;
    background.update(bgPos, VEL, dt);
  }

  @Override
  public void render() {
    GameRenderer gr = game.getRenderer();
    cam.update();
    gr.begin(cam);
    background.render(gr);
    gr.end();

    gr.beginOrtho();
    titleText.draw(gr.getSpriteBatch(), true);
    gr.endOrtho();

    super.render();
  }

  @Override
  public void dispose() {
    super.dispose();
    background.dispose();
  }
}
