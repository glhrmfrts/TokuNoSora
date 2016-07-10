package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.level.worlds.Universe;
import com.habboi.tns.level.worlds.World;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.MainMenu;
import com.habboi.tns.ui.Menu;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.FontManager;

import java.util.Stack;

/**
 * Handles the menu screen state.
 */
public class MenuState extends FadeState {
  static final float VEL = 10;
  static final float CAM_DIST = 20;
  static final float CAM_Y = 10;
  static final float CAM_LOOKAT_Y = 6;

  Vector3 bgPos = new Vector3();
  GameTweenManager gtm;
  World world;
  PerspectiveCamera cam;
  Text titleText;
  Stack<Menu> menuStack = new Stack<>();

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
    addMenu(new MainMenu(this, game));
    AssetManager am = game.getAssetManager();
    FontManager fm = FontManager.get();

    titleText = new Text(
            fm.getFont("Neon.ttf", Game.HUGE_FONT_SIZE),
            "TAIHO",
            null,
            Color.WHITE
    );
    titleText.getPos().set(game.getWidth()/2, game.getHeight()*0.9f);
    world = Universe.get().worlds.get(0);

    cam = new PerspectiveCamera(45, game.getWidth(), game.getHeight());
    cam.near = 0.01f;
    cam.far = 1000f;
    cam.position.set(0, CAM_Y, 0);
    cam.lookAt(0, CAM_LOOKAT_Y, -CAM_DIST);

    Gdx.gl.glClearColor(0, 0, 0, 0);
    fadeIn();
  }

  @Override
  public void resume() {
    menuStack.peek().resume();
    fadeIn();
  }

  public void setWorld(World newWorld) {
    world = newWorld;
  }

  public void addMenu(Menu menu) {
    menuStack.add(menu);
  }

  public Menu popMenu() {
    Menu m = menuStack.pop();
    return m;
  }

  public Menu setMenu(Menu menu) {
    Menu m = popMenu();
    addMenu(menu);
    return m;
  }

  @Override
  public boolean keyDown(int keycode) {
    return menuStack.peek().keyDown(keycode);
  }

  @Override
  public void update(float dt) {
    super.update(dt);
    cam.position.z -= VEL * dt;
    cam.lookAt(0, CAM_LOOKAT_Y, cam.position.z - CAM_DIST);
    bgPos.set(cam.position);
    bgPos.y -= CAM_LOOKAT_Y;
    world.update(bgPos, VEL, dt);
  }

  @Override
  public void render() {
    GameRenderer gr = game.getRenderer();
    cam.update();
    gr.begin(cam);
    world.render(gr);
    gr.end();
    menuStack.peek().render();
    gr.beginOrtho();
    titleText.draw(gr.getSpriteBatch(), true);
    gr.endOrtho();
    super.render();
  }

  @Override
  public void dispose() {
    super.dispose();
  }
}
