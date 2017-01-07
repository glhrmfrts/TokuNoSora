package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;
import com.habboi.tns.level.LevelScore;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;
import com.habboi.tns.states.InGameState;
import com.habboi.tns.states.MenuState;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.utils.InputManager;
import com.habboi.tns.utils.MusicAccessor;
import com.habboi.tns.utils.MusicWrapper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

/**
 * Created by w7 on 02/07/2016.
 */
public class LevelsMenu extends BaseMenu implements Disposable {
    static final int FONT_SIZE = Game.MAIN_FONT_SIZE;
    static final int WORLD_FONT_SIZE = Game.MEDIUM_FONT_SIZE;

    MenuState menuState;
    Rect container;
    Rect background;
    String levelToStart;
    Text worldText;
    Text btnRightText;
    Text btnLeftText;
    int activeItemIndex;
    int activeWorldIndex;
    int prevActiveWorldIndex;
    HashMap<World, ArrayList<LevelMenuItem>> itemGroups = new HashMap<>();

    public LevelsMenu(final MenuState state, final Game game) {
        menuState = state;
        sr = game.getShapeRenderer();
        sb = game.getSpriteBatch();
        background = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
        background.getColor().a = 0;
        final GameTweenManager gtm = GameTweenManager.get();
        gtm.register("levels_menu_select_menu", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    highlight.getRectangle().height = 0;
                    return Tween.to(highlight, Rect.Accessor.TWEEN_HEIGHT, 0.25f)
                        .target(itemBounds.height)
                        .ease(TweenEquations.easeOutQuad);
                }

                @Override
                public void onComplete() {

                }
            });
        gtm.register("levels_menu_select_menu_border", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    highlightBorder.getRectangle().height = 0;
                    return Tween.to(highlightBorder, Rect.Accessor.TWEEN_HEIGHT, 0.25f)
                        .target(itemBounds.height)
                        .ease(TweenEquations.easeOutQuad);
                }

                @Override
                public void onComplete() {

                }
            });
        gtm.register("change_world_in", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    return background.getFadeTween(0, 1, 0.15f);
                }

                @Override
                public void onComplete() {
                    World world = Universe.get().worlds.get(activeWorldIndex);
                    menuState.setWorld(world);
                    gtm.start("change_world_out");
                }
            });

        gtm.register("change_world_out", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    return background.getFadeTween(1, 0, 0.15f);
                }

                @Override
                public void onComplete() {

                }
            });

        gtm.register("levels_menu_start_level", new GameTweenManager.GameTween() {
                @Override
                public Tween tween() {
                    return Tween.to(menuState.getCurrentMusic(), MusicAccessor.TWEEN_VOLUME, 0.50f)
                        .target(0);
                }

                @Override
                public void onComplete() {
                    menuState.getCurrentMusic().getMusic().stop();
                    game.addState(new InGameState(game, levelToStart));
                }
            });
        createItems(game);
        setHighlight();
    }

    private void createItems(final Game game) {
        AssetManager am = game.getAssetManager();
        LevelLoader l = (LevelLoader) am.getLoader(Level.class);
        l.sortLevelsNames(am);
        setSize(game.getWidth() * 1.1f, FONT_SIZE * 4 + 20 * 4);
        setItemSize(game.getWidth()*1.1f, FONT_SIZE + 20);
        setCenter(game.getWidth() / 2, game.getHeight() / 2);
        createHighlight();
        float y = bounds.y + bounds.height/2 - itemBounds.height/2;
        float x = bounds.x;
        Rectangle bnds = new Rectangle(x, y, itemBounds.width, itemBounds.height);
        World lastWorld = null;
        float top = y;
        for (final String str : l.getLevelsNames()) {
            Level level = am.get(str);
            World world = level.getWorld();
            if (world != lastWorld) {
                y = bnds.y = bounds.y + bounds.height/2 - itemBounds.height/2;
                lastWorld = world;
            }
            int completed = LevelScore.get().getTimesCompleted(level.getName());
            float bestTime = LevelScore.get().getBestTime(level.getName());
            LevelMenuItem item = new LevelMenuItem(level.getName(), completed, bestTime, x, y, bnds);
            item.setAction(new MenuItemAction() {
                    @Override
                    public void doAction() {
                        if (levelToStart != null) {
                            return;
                        }

                        levelToStart = str;
                        GameTweenManager.get().start("levels_menu_start_level");
                    }
                });
            ArrayList<LevelMenuItem> itemGroup = itemGroups.get(world);
            if (itemGroup == null) {
                itemGroup = new ArrayList<>();
                itemGroups.put(world, itemGroup);
            }
            itemGroup.add(item);
            y = top - (bnds.height * itemGroup.size());
            bnds.y = top - (bnds.height * itemGroup.size());
        }
        activeWorldIndex = 0;
        World activeWorld = Universe.get().worlds.get(activeWorldIndex);
        worldText = new Text(FontManager.get().getFont(Game.MAIN_FONT, WORLD_FONT_SIZE), "", null, Color.WHITE);
        worldText.setValue(activeWorld.name, true);
        float worldPadding = 20 * ((float) WORLD_FONT_SIZE / (float) FONT_SIZE);
        worldText.getPos().set(game.getWidth() * 0.05f, bounds.y + bounds.height / 2 + (WORLD_FONT_SIZE / 2) + worldPadding);
        container = new Rect(new Rectangle(bounds.x, bounds.y + worldText.getBounds().y/2, bounds.width, bounds.height+worldText.getBounds().y + worldPadding * 2));
        container.getColor().a = 0.5f;
        btnLeftText = new Text(FontManager.get().getFont(Game.MAIN_FONT, Game.BIG_FONT_SIZE), "<", null, Color.WHITE);
        btnLeftText.getPos().set(game.getWidth() / 2 - Game.BIG_FONT_SIZE/2, bounds.y - bounds.height/2 - 10 + itemBounds.height);
        btnRightText = new Text(FontManager.get().getFont(Game.MAIN_FONT, Game.BIG_FONT_SIZE), ">", null, Color.WHITE);
        btnRightText.getPos().set(game.getWidth() / 2 + Game.BIG_FONT_SIZE/2, bounds.y - bounds.height/2 - 10 + itemBounds.height);
    }

    @Override
    public void resume() {
        super.resume();
        levelToStart = null;

        DecimalFormat format = new DecimalFormat("00.00");
        for (Map.Entry<World, ArrayList<LevelMenuItem>> group : itemGroups.entrySet()) {
            for (LevelMenuItem item : group.getValue()) {
                String levelName = item.text.getValue();
                int completed = LevelScore.get().getTimesCompleted(levelName);
                float time = LevelScore.get().getBestTime(levelName);
                item.completedText.setValue("completed: " + completed);
                item.timeText.setValue("time: " + format.format(time));
            }
        }
    }

    @Override
    public boolean onChange(int delta) {
        setHighlight();
        GameTweenManager.get().restart("levels_menu_select_menu");
        GameTweenManager.get().restart("levels_menu_select_menu_border");
        return true;
    }

    private void changeGroup() {
        World world = Universe.get().worlds.get(activeWorldIndex);
        worldText.setValue(world.name, true);
        GameTweenManager.get().start("change_world_in");
    }

    @Override
    public void setHighlight() {
        World world = Universe.get().worlds.get(activeWorldIndex);
        ArrayList<LevelMenuItem> items = itemGroups.get(world);
        if (items == null) {
            return;
        }
        MenuItem item = items.get(activeItemIndex);
        highlight.getRectangle().setY(item.bounds.y - item.bounds.height / 4);
        highlightBorder.getRectangle().setY(item.bounds.y - item.bounds.height / 4);
    }

    @Override
    public void render() {
        if (activeWorldIndex == 0) {
            btnLeftText.getColor().a = 0.3f;
        } else {
            btnLeftText.getColor().a = 1;
        }
        if (activeWorldIndex == Universe.get().worlds.size() - 1) {
            btnRightText.getColor().a = 0.3f;
        } else {
            btnRightText.getColor().a = 1;
        }
        background.draw(sr, false);
        container.draw(sr, true);
        highlight.draw(sr, true);
        highlightBorder.draw(sr, ShapeRenderer.ShapeType.Line, true);
        sb.begin();
        worldText.draw(sb, false);
        btnLeftText.draw(sb, true);
        btnRightText.draw(sb, true);
        World world = Universe.get().worlds.get(activeWorldIndex);
        ArrayList<LevelMenuItem> group = itemGroups.get(world);
        renderGroup(group);
        sb.end();
    }

    private void renderGroup(ArrayList<LevelMenuItem> group) {
        if (group == null) {
            return;
        }
        for (int i = 0; i < group.size(); i++) {
            LevelMenuItem item = group.get(i);
            item.text.draw(sb, false);
            if (activeItemIndex == i) {
                item.completedText.draw(sb, false);
                item.timeText.draw(sb, false);
            }
        }
    }

    @Override
    public void remove() {
    }

    public void dispose() {
        GameTweenManager.get().remove("levels_menu_select_menu");
        GameTweenManager.get().remove("levels_menu_select_menu_border");
        GameTweenManager.get().remove("change_world_in");
        GameTweenManager.get().remove("change_world_out");
    }

    static class LevelMenuItem extends MenuItem {
        Text text;
        Text completedText;
        Text timeText;

        public LevelMenuItem(String textValue, int completed, float bestTime, float textX, float textY, Rectangle bounds) {
            super(bounds);
            float quarter = bounds.width * .40f;
            text = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), textValue, null, Color.WHITE);
            text.getPos().set(textX - quarter, textY);
            completedText = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), "completed: " + completed, null, Color.WHITE);
            completedText.getPos().set(textX + (quarter - completedText.getBounds().x), textY);
            DecimalFormat format = new DecimalFormat("00.00");
            timeText = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), "time: " + format.format(bestTime), null, Color.WHITE);
            timeText.getPos().set(textX + (quarter - completedText.getBounds().x - timeText.getBounds().x - 20), textY);
        }
    }

    @Override
    public boolean buttonDown(int buttonCode) {
        World world = Universe.get().worlds.get(activeWorldIndex);
        ArrayList<LevelMenuItem> currentGroup = itemGroups.get(world);
        boolean ret = false;
        if (buttonCode == InputManager.Right) {
            if (activeWorldIndex < Universe.get().worlds.size() - 1) {
                prevActiveWorldIndex = activeWorldIndex;
                activeWorldIndex++;
                changeGroup();
                activeItemIndex = 0;
                ret = onChange(0);
            }
        } else if (buttonCode == InputManager.Left) {
            if (activeWorldIndex > 0) {
                prevActiveWorldIndex = activeWorldIndex;
                activeWorldIndex--;
                changeGroup();
                activeItemIndex = 0;
                ret = onChange(0);
            }
        } else if (buttonCode == InputManager.Up) {
            if (activeItemIndex > 0) {
                activeItemIndex--;
                ret = onChange(-1);
            } else if (activeWorldIndex > 0) {
                activeWorldIndex--;
                activeItemIndex = currentGroup.size() - 1;
                changeGroup();
                ret = onChange(0);
            }
        } else if (buttonCode == InputManager.Up) {
            if (activeItemIndex < currentGroup.size() - 1) {
                activeItemIndex++;
                ret = onChange(1);
            } else if (activeWorldIndex < Universe.get().worlds.size() - 1) {
                activeWorldIndex++;
                activeItemIndex = 0;
                changeGroup();
                ret = onChange(0);
            }
        } else if (buttonCode == InputManager.Select) {
            currentGroup.get(activeItemIndex).action.doAction();
        } else if (buttonCode == InputManager.Back) {
            menuState.popMenu();
        }
        return ret;
    }
}
