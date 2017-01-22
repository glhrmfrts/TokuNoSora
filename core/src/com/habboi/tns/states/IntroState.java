package com.habboi.tns.states;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.utils.InputManager;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;

public class IntroState extends GameState {

    private enum InnerState {
        START,
        AFTER_TEXT
    }

    Vector3 bgPos = new Vector3();
    Music music;
    boolean running;
    Rect screenRect;
    InnerState state;
    float timer;
    Text titleText;
    World world;
    PerspectiveCamera worldCam;

    public IntroState(Game g) {
        super(g);
    }

    @Override
    public void create() {
        world = Universe.get().worlds.get(0);

        worldCam = game.getRenderer().getWorldRenderer().getCamera();
        worldCam.position.set(0, MenuState.CAM_Y, 0);
        worldCam.lookAt(0, MenuState.CAM_LOOKAT_Y, -MenuState.CAM_DIST);

        FontManager fm = FontManager.get();
        titleText = new Text(
                             fm.getFont("Neon.ttf", Game.HUGE_FONT_SIZE),
                             "TAIHO",
                             null,
                             null
                             );
        titleText.getPos().set(game.getWidth()/2, game.getHeight()*0.9f);
        titleText.getColor().set(1, 1, 1, 0);

        screenRect = game.getRenderer().getScreenRect();

        GameTweenManager gtm = GameTweenManager.get();
        gtm.register("intro_state_in", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    return screenRect.getFadeTween(1, 0, 4);
                }

                @Override
                public void onComplete() {
                    running = true;
                    timer = 2f;
                    state = InnerState.START;
                }
            });

        gtm.register("intro_state_title_text_in", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    return Tween.to(titleText, Text.Accessor.TWEEN_ALPHA, 4f)
                        .target(1);
                }

                @Override
                public void onComplete() {
                    running = true;
                    timer = 2f;
                    state = InnerState.AFTER_TEXT;
                }
            });

        Gdx.gl.glClearColor(0, 0, 0, 0);
        gtm.start("intro_state_in");

        music = game.getAssetManager().get("audio/intro.ogg");
        music.setVolume(GameConfig.get().getMusicVolume());
        music.setLooping(true);
        music.play();
    }

    @Override
    public void resume() {
    }

    @Override
    public void update(float dt) {
        System.out.println(InputManager.getInstance().isAnyButtonDown());
        
        if (InputManager.getInstance().isAnyButtonDown()) {
            Color c = titleText.getColor();
            c.a = 1;

            game.setState(new MenuState(game, titleText, music));
        }

        worldCam.position.z -= MenuState.VEL * dt;
        worldCam.lookAt(0, MenuState.CAM_LOOKAT_Y, worldCam.position.z - MenuState.CAM_DIST);
        bgPos.set(worldCam.position);
        bgPos.y -= MenuState.CAM_LOOKAT_Y;
        world.setCenterX(0);
        world.update(bgPos, MenuState.VEL, dt);

        if (running) {
            timer -= dt;

            if (timer <= 0) {
                running = false;

                switch (state) {
                case START:
                    GameTweenManager.get().start("intro_state_title_text_in");
                    break;
                case AFTER_TEXT:
                    game.setState(new MenuState(game, titleText, music));
                    break;
                }
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        GameRenderer gr = game.getRenderer();
        SpriteBatch sb = game.getSpriteBatch();

        gr.render(world);

        sb.begin();
        titleText.draw(sb, true);
        sb.end();

        screenRect.draw(game.getShapeRenderer());
    }

    @Override
    public void dispose() {
        GameTweenManager.get().remove("intro_state_in");
        GameTweenManager.get().remove("intro_state_title_text_in");
    }
}
