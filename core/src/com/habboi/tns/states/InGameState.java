package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.level.LevelScore;
import com.habboi.tns.ui.GameTweenManager;
import com.habboi.tns.level.Ship;
import com.habboi.tns.level.ShipCamera;
import com.habboi.tns.level.ShipController;
import com.habboi.tns.level.Level;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.ui.PauseMenu;
import com.habboi.tns.ui.Rect;
import com.habboi.tns.ui.Text;
import com.habboi.tns.utils.EventEmitter;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.utils.InputManager;
import com.habboi.tns.utils.MusicAccessor;
import com.habboi.tns.utils.MusicWrapper;

import java.text.DecimalFormat;

import aurelienribon.tweenengine.Tween;

/**
 * In-game state.
 */
public class InGameState extends GameState {

    String levelName;
    OrthographicCamera orthoCam;
    Level level;
    Ship ship;
    ShipCamera shipCam;
    Text levelCompleteText;
    Text raceTimeText;
    Text fpsText;
    HUD hud;
    GameTweenManager gtm;
    Rect screenRect;
    PauseMenu pauseMenu;
    float bestTimeForLevel;
    int timesLevelCompleted;
    boolean paused;
    boolean quitFromMenu;
    boolean debug;
    MusicWrapper music = new MusicWrapper();

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
        shipCam = new ShipCamera(ship, new PerspectiveCamera[] {
                game.getRenderer().getLevelRenderer().getCamera(),
                game.getRenderer().getWorldRenderer().getCamera()
        });

        level.setShip(ship);

        FontManager fm = FontManager.get();
        levelCompleteText = new Text(fm.getFont("Neon.ttf", Game.MAIN_FONT_SIZE),
                                     "level complete", null, Color.WHITE);
        levelCompleteText.getPos().set(game.getWidth() / 2, game.getHeight() / 2 + Game.MAIN_FONT_SIZE/2 + 10);
        levelCompleteText.getColor().a = 0;
        raceTimeText = new Text(fm.getFont("Neon.ttf", Game.MAIN_FONT_SIZE), "", null, Color.WHITE);
        raceTimeText.getPos().set(game.getWidth() / 2, game.getHeight() / 2 - Game.MAIN_FONT_SIZE/2 - 10);
        raceTimeText.getColor().a = 0;
        fpsText = new Text(fm.getFont("Neon.ttf", Game.MAIN_FONT_SIZE), "", null, Color.WHITE);
        fpsText.getPos().set(20, 60);

        hud = new HUD();
        
        screenRect = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
        pauseMenu = new PauseMenu(this, game);

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
        }).register("level_complete_end", new GameTweenManager.GameTween(new String[]{"level_complete_end_music"}) {
                @Override
                public Tween tween() {
                    return screenRect.getFadeTween(0, 1, 0.5f);
                }

                @Override
                public void onComplete() {
                    if (ship.state == Ship.State.ENDED) {
                        if (ship.raceTime < bestTimeForLevel || bestTimeForLevel == 0) {
                            LevelScore.get().setBestTime(level.getName(), ship.raceTime);
                        }
                        LevelScore.get().setTimesCompleted(level.getName(), timesLevelCompleted + 1);
                        LevelScore.get().flush();
                    }

                    game.popState();
                }
        }).register("level_complete_end_music", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    return Tween.to(music, MusicAccessor.TWEEN_VOLUME, 0.5f)
                        .target(0);
                }

                @Override
                public void onComplete() {
                    music.getMusic().stop();
                }
        });

        EventEmitter.get().listen("ship_explosion_end", new EventEmitter.EventHandler() {
            @Override
            public void onEvent(Object data) {
                if (!gtm.isActive("reset")) {
                    gtm.start("reset");
                }
            }
        });

        Music worldMusic = game.getAssetManager().get(level.getWorld().music);
        worldMusic.setVolume(GameConfig.get().getMusicVolume());
        worldMusic.setLooping(true);
        worldMusic.play();

        music.setMusic(worldMusic);

        gtm.start("start");
        reset();
    }

    public void resume(boolean quitFromMenu) {
        this.quitFromMenu = quitFromMenu;
    }

    @Override
    public void resume() {
        paused = false;
    }

    public void reset() {
        bestTimeForLevel = LevelScore.get().getBestTime(level.getName());
        timesLevelCompleted = LevelScore.get().getTimesCompleted(level.getName());
        paused = false;
        ship.reset();
        shipCam.reset();
        level.reset();
        level.getWorld().reset();
        gtm.start("start");
    }

    @Override
    public void update(float dt) {
        if (InputManager.getInstance().isButtonJustDown(InputManager.Pause) && ship.state != Ship.State.ENDED) {
            paused = !paused;
        }
        if (quitFromMenu) {
            if (!gtm.played("level_complete_end")) {
                gtm.start("level_complete_end");
            }
        }
        if (paused) {
            InputManager.getInstance().menuInteraction(pauseMenu);
            return;
        }
        if (ship.state == Ship.State.ENDED) {
            if (!gtm.played("level_complete")) {
                raceTimeText.setValue("time " + ship.getTimeText(), true);
                gtm.start("level_complete");
            } else if (!gtm.isActive("level_complete")) {
                if (InputManager.getInstance().isAnyButtonDown()) {
                    if (!gtm.played("level_complete_end")) {
                        gtm.start("level_complete_end");
                    }
                }
            }
        } else if (ship.state == Ship.State.FELL) {
            if (!gtm.isActive("reset")) {
                gtm.start("reset");
            }
        }

        level.update(dt);
        ship.update(dt);
        shipCam.update(dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        GameRenderer gr = game.getRenderer();
        SpriteBatch sb = game.getSpriteBatch();
        ShapeRenderer sr = game.getShapeRenderer();

        gr.render(level, level.getWorld());

        orthoCam.update();
        sb.setProjectionMatrix(orthoCam.combined);
        sr.setProjectionMatrix(orthoCam.combined);

        hud.renderShapes(ship, sr);

        sb.begin();
        hud.renderSprites(ship, sb);

        if (ship.state == Ship.State.ENDED) {
            levelCompleteText.draw(sb, true);
            raceTimeText.draw(sb, true);
        }

        if (debug) {
            DecimalFormat format = new DecimalFormat("00.00");
            fpsText.setValue("fps " + format.format(game.getFPS()), false);
            fpsText.draw(sb, false);
        }
        sb.end();
   
        if (paused) {
            pauseMenu.render();
        }
        screenRect.draw(sr);
    }

    @Override
    public void dispose() {
        level.dispose();
    }
}

class HUD {
    static final int BORDER_WIDTH = 20;
    static final int FONT_SIZE = Game.MAIN_FONT_SIZE;
    static final int OXYGEN_RECT_WIDTH = 150;
    static final int OXYGEN_RECT_HEIGHT = 30;

    Text collectedText;
    Rect oxygenRect;
    Rect oxygenRectOutline;
    Text oxygenText;
    Text timeText;

    public HUD() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        collectedText = new Text(
            FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE),
            "collected: 0",
            null,
            Color.WHITE
        );
        collectedText.pos.set(BORDER_WIDTH, h - BORDER_WIDTH);

        timeText = new Text(
            FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE),
            "00:00:00",
            null,
            Color.WHITE
        );
        timeText.pos.set(w - timeText.bounds.width - BORDER_WIDTH, h - BORDER_WIDTH);
    
        oxygenText = new Text(
            FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE),
            "oxygen",
            null,
            Color.WHITE
        );
        oxygenText.pos.set(w - BORDER_WIDTH - OXYGEN_RECT_WIDTH, BORDER_WIDTH + oxygenText.bounds.height);

        oxygenRectOutline = new Rect(
            new Rectangle(w - BORDER_WIDTH - OXYGEN_RECT_WIDTH, BORDER_WIDTH + oxygenText.bounds.height + 10, OXYGEN_RECT_WIDTH, OXYGEN_RECT_HEIGHT),
            Color.WHITE
        );

        oxygenRect = new Rect(
            new Rectangle(w - BORDER_WIDTH - OXYGEN_RECT_WIDTH + 5, oxygenRectOutline.rect.y + 5, OXYGEN_RECT_WIDTH - 10, OXYGEN_RECT_HEIGHT - 10),
            Color.WHITE
        );
    }

    public void renderShapes(Ship ship, ShapeRenderer sr) {
        oxygenRect.rect.width = (OXYGEN_RECT_WIDTH - 10) * ship.oxygenLevel;

        oxygenRect.draw(sr, ShapeRenderer.ShapeType.Filled, false);
        oxygenRectOutline.draw(sr, ShapeRenderer.ShapeType.Line, false);
    }

    public void renderSprites(Ship ship, SpriteBatch sb) {
        collectedText.value = "collected: " + ship.collected;
        timeText.value = ship.getTimeText();

        collectedText.draw(sb, false);
        timeText.draw(sb, false);
        oxygenText.draw(sb, false);
    }
}