package com.habboi.tns;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;
import com.habboi.tns.level.Ship;
import com.habboi.tns.rendering.Renderer;
import com.habboi.tns.states.MenuState;
import com.habboi.tns.utils.Models;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.utils.MusicAccessor;
import com.habboi.tns.utils.Shaders;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.states.*;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.utils.FontFileHandleResolver;
import com.habboi.tns.utils.FontLoader;
import com.habboi.tns.utils.InputManager;

import java.util.Stack;

import aurelienribon.tweenengine.Tween;

public class Game extends ApplicationAdapter {
    public static final String MAIN_FONT = "Neon.ttf";
    public static final int HUGE_FONT_SIZE = 64;
    public static final int BIG_FONT_SIZE = 52;
    public static final int MEDIUM_FONT_SIZE = 40;
    public static final int MAIN_FONT_SIZE = 28;
    public static final int SMALL_FONT_SIZE = 16;

    public static final String TAG = Game.class.getName();

    static final float STEP_SECONDS = 0.016f;
    static final float STEP	    = STEP_SECONDS * 1000f;

    static Game instance;

    long accumUpdateTime;
    long lastUpdateTime = -1;

    InputMultiplexer inputMul;
    Renderer renderer;
    ShapeRenderer sr;
    SpriteBatch sb;
    Rect exitingRect;
    GameState currentState;
    Stack<GameState> stateStack;
    boolean stateChanged;
    boolean exiting;
    float fpsTime;
    Ship ship;

    AssetManager am;
    Application.ApplicationType appType;
    int width;
    int height;

    public static Game getInstance() {
        return instance;
    }

    @Override
    public void create () {
        instance = this;
        
        appType = Gdx.app.getType();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        exitingRect = new Rect(new Rectangle(0, 0, width, height));

        renderer = new Renderer(this);
        sr = new ShapeRenderer();
        sb = new SpriteBatch();

        am = new AssetManager();
        am.setLoader(Level.class, new LevelLoader(new InternalFileHandleResolver()));
        am.setLoader(BitmapFont.class, new FontLoader(new FontFileHandleResolver()));
        FontManager.get().setAssetManager(am);
        GameTweenManager.get().register("game_fade_out", new GameTweenManager.GameTween(new String[]{"game_fade_out_music"}) {
                @Override
                public Tween tween() {
                    return exitingRect.getFadeTween(0, 1, 0.5f);
                }

                @Override
                public void onComplete() {
                    Gdx.app.exit();
                }
        }).register("game_fade_out_music", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    MenuState menuState = (MenuState)currentState;
                    return Tween.to(menuState.getCurrentMusic(), MusicAccessor.TWEEN_VOLUME, 0.5f)
                        .target(0);
                }

                @Override
                public void onComplete() {
                    MenuState menuState = (MenuState)currentState;
                    menuState.getCurrentMusic().getMusic().stop();
                }
        });

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log(TAG, controller.getName());
        }

        // Gdx.app.setLogLevel(Application.LOG_NONE);

        stateStack = new Stack<>();
        addState(new LoadingAllState(this));
    }

    public float getDensity() {
        if (isAndroid()) {
            return Gdx.graphics.getDensity();
        }
        return 1;
    }

    public boolean isAndroid() {
        return appType == Application.ApplicationType.Android;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getFPS() {
        return 1f / fpsTime;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public ShapeRenderer getShapeRenderer() {
        return sr;
    }

    public SpriteBatch getSpriteBatch() {
      return sb;
    }

    public AssetManager getAssetManager() {
        return am;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public GameState setState(GameState state) {
        GameState prev = popState(true);
        addState(state);
        return prev;
    }

    public void addState(GameState state) {
        state.create();
        stateStack.add(state);
        currentState = stateStack.peek();
        stateChanged = true;
    }

    public GameState popState() {
        return popState(false);
    }

    public GameState popState(boolean set) {
        GameState state = stateStack.pop();
        if (state != null) {
            state.dispose();
        }
        if (stateStack.size() > 0) {
            currentState = stateStack.peek();
            if (!set) currentState.resume();
        } else {
            currentState = null;
        }
        stateChanged = true;
        return state;
    }

    public void exit() {
        exiting = true;
        GameTweenManager.get().start("game_fade_out");
    }

    public void update() {
        currentState.update(STEP_SECONDS);
        InputManager.getInstance().update();
        GameTweenManager.get().update(STEP_SECONDS);
    }

    @Override
    public void render () {
        long start = TimeUtils.millis();
        if (lastUpdateTime == -1) {
            lastUpdateTime = start;
            return;
        }

        float delta = (start - lastUpdateTime);
        lastUpdateTime = start;
        fpsTime = delta / 1000;

        accumUpdateTime += delta;
        while (accumUpdateTime >= STEP) {
            accumUpdateTime -= delta;
            update();
            if (stateChanged) {
                update();
                stateChanged = false;
            }
        }

        currentState.render();
        if (exiting) {
            exitingRect.draw(sr, false);
        }
    }

    @Override
    public void dispose() {
        currentState.dispose();
        renderer.dispose();
        sb.dispose();
        sr.dispose();
        Models.dispose();
        Shaders.dispose();
        Universe.get().dispose();
    }
}
