package com.habboi.tns.states;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.MainMenu;
import com.habboi.tns.ui.Menu;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.FontManager;
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

    Vector3 bgPos = new Vector3();
    MusicWrapper currentMusic = new MusicWrapper();
    World world;
    PerspectiveCamera worldCam;
    Text titleText;
    Stack<Menu> menuStack = new Stack<>();
    Rect screenRect;

    public MenuState(Game g, Text t, Music m) {
        super(g);
        titleText = t;
        currentMusic.setMusic(m);
    }

    @Override
    public void create() {
        addMenu(new MainMenu(this, game));

        world = Universe.get().worlds.get(0);
        worldCam = game.getRenderer().getWorldRenderer().getCamera();

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
        menuStack.peek().resume();
        GameTweenManager.get().start("menu_state_in");

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
        world = newWorld;
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
    public boolean keyDown(int keycode) {
        return menuStack.peek().keyDown(keycode);
    }

    @Override
    public void update(float dt) {
        worldCam.position.x = 0;
        worldCam.position.y = CAM_Y;
        worldCam.position.z -= VEL * dt;
        worldCam.lookAt(0, CAM_LOOKAT_Y, worldCam.position.z - CAM_DIST);

        bgPos.set(worldCam.position);
        bgPos.y -= CAM_LOOKAT_Y;

        world.setCenterX(0);
        world.update(bgPos, VEL, dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GameRenderer gr = game.getRenderer();
        SpriteBatch sb = game.getSpriteBatch();

        gr.render(world);

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
