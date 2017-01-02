package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.habboi.tns.Game;
import com.habboi.tns.states.InGameState;
import com.habboi.tns.utils.FontManager;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

/**
 * Created by w7 on 10/07/2016.
 */
public class PauseMenu extends BaseMenu {
    static final int FONT_SIZE = Game.MAIN_FONT_SIZE;

    Rect container;

    public PauseMenu(final InGameState state, final Game game) {
        sr = game.getShapeRenderer();
        sb = game.getSpriteBatch();
        container = new Rect(new Rectangle(0, 0, game.getWidth(), game.getHeight()));
        container.getColor().a = 0.30f;

        setSize(game.getWidth() * 1.1f, FONT_SIZE * 4 + 20 * 3);
        setItemSize(game.getWidth() * 1.1f, FONT_SIZE + 20);
        setCenter(game.getWidth() / 2, game.getHeight() / 2);
        createHighlight();

        float y = bounds.y + bounds.height / 2 - itemBounds.height / 2;
        float x = bounds.x;
        Rectangle bnds = new Rectangle(x, y, itemBounds.width, itemBounds.height);
        items.add(new MainMenuItem("resume", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    state.resume();
                }
            }));
        y -= bnds.height;
        bnds.y -= bnds.height;
        items.add(new MainMenuItem("restart", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    state.reset();
                }
            }));
        y -= bnds.height;
        bnds.y -= bnds.height;
        items.add(new MainMenuItem("quit", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    state.resume(true);
                }
            }));

        GameTweenManager.get().register("pause_menu_select_menu", new GameTweenManager.GameTween() {
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
            }).register("pause_menu_select_menu_border", new GameTweenManager.GameTween() {
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
        GameTweenManager.get().restart("pause_menu_select_menu");
        GameTweenManager.get().restart("pause_menu_select_menu_border");
        return true;
    }

    @Override
    public void render() {
        container.draw(sr, false);
        highlight.draw(sr, true);
        highlightBorder.draw(sr, ShapeRenderer.ShapeType.Line, true);

        sb.begin();
        for (int i = 0; i < items.size(); i++) {
            Text text = ((MainMenuItem) items.get(i)).text;
            text.draw(sb, true);
        }
        sb.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        return super.keyDown(keycode);
    }

    @Override
    public void remove() {
    }

    public void dispose() {
        GameTweenManager.get().remove("pause_menu_select_menu");
        GameTweenManager.get().remove("pause_menu_select_menu_border");
    }

    static class MainMenuItem extends MenuItem {
        Text text;

        public MainMenuItem(String textValue, float textX, float textY, Rectangle bounds) {
            super(bounds);
            text = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), textValue, null, Color.WHITE);
            text.getPos().set(textX, textY);
        }
    }
}
