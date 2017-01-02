package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.states.EditorState;
import com.habboi.tns.states.MenuState;
import com.habboi.tns.ui.BaseMenu;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import java.util.ArrayList;


public class EditorWorldsMenu extends BaseMenu implements Disposable {
    static final int FONT_SIZE = Game.MAIN_FONT_SIZE;

    MenuState menuState;

    public EditorWorldsMenu(final MenuState state, final Game game) {
        menuState = state;
        sr = game.getShapeRenderer();
        sb = game.getSpriteBatch();

        float worldCount = Universe.get().worlds.size();
        setSize(game.getWidth() * 1.1f, FONT_SIZE * worldCount + 20 * worldCount);
        setItemSize(game.getWidth()*1.1f, FONT_SIZE + 20);
        setCenter(game.getWidth() / 2, game.getHeight() / 2);

        createHighlight();

        float y = bounds.y + bounds.height/2 - itemBounds.height/2;
        float x = bounds.x;

        Rectangle bnds = new Rectangle(x, y, itemBounds.width, itemBounds.height);

        for (final World world : Universe.get().worlds) {
            items.add(new EditorWorldsMenuItem(world.name, x, y, bnds).setAction(new MenuItemAction() {
                    @Override
                    public void doAction() {
                        game.addState(new EditorState(game, world));
                    }
                }));

            y -= bnds.height;
            bnds.y -= bnds.height;
        }

        GameTweenManager.get().register("editor_worlds_menu_select_menu", new GameTweenManager.GameTween() {
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
        }).register("editor_worlds_menu_select_menu_border", new GameTweenManager.GameTween() {
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

        setHighlight();
    }

    @Override
    public boolean onChange(int delta) {
        setHighlight();
        GameTweenManager.get().restart("editor_worlds_menu_select_menu");
        GameTweenManager.get().restart("editor_worlds_menu_select_menu_border");

        World world = Universe.get().worlds.get(activeIndex);
        menuState.setWorld(world);

        return true;
    }

    @Override
    public void render() {
        highlight.draw(sr, true);
        highlightBorder.draw(sr, ShapeRenderer.ShapeType.Line, true);

        sb.begin();
        for (int i = 0; i < items.size(); i++) {
            EditorWorldsMenuItem item = (EditorWorldsMenuItem)items.get(i);
            item.text.draw(sb, true);
        }
        sb.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            menuState.popMenu();
        }

        return super.keyDown(keycode);
    }

    @Override
    public void remove() {
    }

    public void dispose() {
        GameTweenManager.get().remove("editor_worlds_menu_select_menu");
        GameTweenManager.get().remove("editor_worlds_menu_select_menu_border");
    }

    static class EditorWorldsMenuItem extends MenuItem {
        Text text;

        public EditorWorldsMenuItem(String textValue, float x, float y, Rectangle bounds) {
            super(bounds);
            text = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), textValue, null, Color.WHITE);
            text.getPos().set(x, y);
        }
    }

}
