package com.habboi.tns.states;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.rendering.Renderer;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.MainMenu;
import com.habboi.tns.ui.Menu;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.InputManager;
import com.habboi.tns.utils.MusicWrapper;

import java.util.Stack;

/**
 * Handles the menu screen state.
 */
public class MenuState extends GameState {
    static final float VEL = 10;
    static final float CAM_DIST = 20;
    static final float CAM_Y = 10;
    static final float CAM_LOOKAT_Y = 6;

    public MusicWrapper currentMusic = new MusicWrapper();
    Vector3 bgPos = new Vector3();
    World world;
    PerspectiveCamera worldCam;
    Text titleText;
    Stack<Menu> menuStack = new Stack<>();
    Rect screenRect;
    Scene scene;

    public MenuState(Game g, Text t, Music m) {
        super(g);
        titleText = t;
        currentMusic.setMusic(m);
    }

    @Override
    public void create() {
        game.getSpriteBatch().setTransformMatrix(new Matrix4().idt());
        game.getShapeRenderer().setTransformMatrix(new Matrix4().idt());
        addMenu(new MainMenu(this, game));

        scene = new Scene(game.getWidth(), game.getHeight());

        world = Universe.get().worlds.get(0);

        for (World w : Universe.get().worlds) {
          w.addToScene(scene);
          w.fragment.visible(false);
        }

        world.fragment.visible(true);
        scene.setBackgroundTexture(world.background);

        worldCam = scene.getCamera();
        worldCam.position.x = 0;
        worldCam.position.y = CAM_Y;

        screenRect = game.getRenderer().getScreenRect();
        GameTweenManager.get().register("menu_state_in", new GameTweenManager.GameTween() {
          @Override
          public Tween tween() {
            return screenRect.getFadeTween(1, 0, 2);
          }

          @Override
          public void onComplete() {

          }
        });
    }

    @Override
    public void resume() {
        game.getSpriteBatch().setTransformMatrix(new Matrix4().idt());
        game.getShapeRenderer().setTransformMatrix(new Matrix4().idt());

        worldCam.position.x = 0;
        worldCam.position.y = CAM_Y;

        menuStack.peek().resume();
        GameTweenManager.get().start("menu_state_in");

        if (currentMusic.getMusic().isPlaying()) {
          return;
        }

        Music menuMusic = game.getAssetManager().get("audio/menu.ogg");
        menuMusic.setVolume(GameConfig.get().getMusicVolume());
        menuMusic.setLooping(true);
        menuMusic.play();

        currentMusic.setMusic(menuMusic);
    }

    public MusicWrapper getCurrentMusic() {
        return currentMusic;
    }

    public void setWorld(World newWorld) {
        world.fragment.visible(false);
        world = newWorld;
        world.fragment.visible(true);
        scene.setBackgroundTexture(world.background);
    }

    public void addMenu(Menu menu) {
        menuStack.add(menu);
    }

    public Menu popMenu() {
        Menu m = menuStack.pop();
        m.remove();
        return m;
    }

    public Menu setMenu(Menu menu) {
        Menu m = popMenu();
        addMenu(menu);
        return m;
    }

    @Override
    public void update(float dt) {
        InputManager.getInstance().menuInteraction(menuStack.peek());

        worldCam.position.z -= VEL * dt;
        worldCam.lookAt(0, CAM_LOOKAT_Y, worldCam.position.z - CAM_DIST);

        bgPos.set(worldCam.position);
        bgPos.y -= CAM_LOOKAT_Y;

        world.setCenterX(0);
        world.update(bgPos, VEL, dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Renderer gr = game.getRenderer();
        SpriteBatch sb = game.getSpriteBatch();

        gr.render(scene);

        menuStack.peek().render();

        sb.begin();
        titleText.draw(sb, true);
        sb.end();

        screenRect.draw(game.getShapeRenderer());
    }

    @Override
    public void dispose() {
        GameTweenManager.get().remove("menu_state_in");
    }
}
