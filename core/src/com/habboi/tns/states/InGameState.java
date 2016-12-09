package com.habboi.tns.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import com.habboi.tns.utils.FontManager;
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
    GameTweenManager gtm;
    Rect screenRect;
    PauseMenu pauseMenu;
    float bestTimeForLevel;
    int timesLevelCompleted;
    boolean paused;
    boolean quitFromMenu;
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
        shipCam = new ShipCamera(ship, game.getRenderer().getWorldCam());

        FontManager fm = FontManager.get();
        levelCompleteText = new Text(fm.getFont("Neon.ttf", Game.MAIN_FONT_SIZE),
                                     "level complete", null, Color.WHITE);
        levelCompleteText.getPos().set(game.getWidth() / 2, game.getHeight() / 2 + Game.MAIN_FONT_SIZE/2 + 10);
        levelCompleteText.getColor().a = 0;
        raceTimeText = new Text(fm.getFont("Neon.ttf", Game.MAIN_FONT_SIZE), "", null, Color.WHITE);
        raceTimeText.getPos().set(game.getWidth() / 2, game.getHeight() / 2 - Game.MAIN_FONT_SIZE/2 - 10);
        raceTimeText.getColor().a = 0;
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

        Music worldMusic = game.getAssetManager().get(level.getWorld().music);
        worldMusic.setVolume(GameConfig.get().getFloat("music_volume"));
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
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE && ship.state != Ship.State.ENDED) {
            paused = !paused;
            return true;
        }

        if (paused) {
            return pauseMenu.keyDown(keycode);
        }

        if (ship.canReceiveInput()) {
            return ship.getController().keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (ship.canReceiveInput()) {
            return ship.getController().keyUp(keycode);
        }
        return false;
    }

    @Override
    public void update(float dt) {
        if (quitFromMenu) {
            if (!gtm.played("level_complete_end")) {
                gtm.start("level_complete_end");
            }
        }
        if (paused) {
            return;
        }
        if (ship.state == Ship.State.ENDED) {
            if (!gtm.played("level_complete")) {
                DecimalFormat format = new DecimalFormat("00.00");
                raceTimeText.setValue("time " + format.format(ship.raceTime), true);
                gtm.start("level_complete");
            } else if (!gtm.isActive("level_complete")) {
                if (ship.getController().isAnyKeyDown()) {
                    if (!gtm.played("level_complete_end")) {
                        gtm.start("level_complete_end");
                    }
                }
            }
        } else if (ship.state == Ship.State.FELL || ship.state == Ship.State.EXPLODED) {
            if (!gtm.isActive("reset")) {
                gtm.start("reset");
            }
        }
        level.update(ship, dt);
        ship.update(dt);
        shipCam.update(dt);
    }

    @Override
    public void render() {
        GameRenderer gr = game.getRenderer();
        gr.begin();
        level.getWorld().render(gr);
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
        if (paused) {
            pauseMenu.render();
        }
        ShapeRenderer sr = game.getShapeRenderer();
        screenRect.draw(sr);
    }

    @Override
    public void dispose() {
        level.dispose();
    }
}
