package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.states.MenuState;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.utils.InputManager;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

/**
 * The main menu.
 */
public class MainMenu extends BaseMenu implements Disposable {
    static final int FONT_SIZE = Game.MAIN_FONT_SIZE;

    MenuState menuState;

    public MainMenu(final MenuState state, final Game game) {
        menuState = state;
        sr = game.getShapeRenderer();
        sb = game.getSpriteBatch();

        setSize(game.getWidth() * 1.1f, FONT_SIZE * 4 + 20 * 4);
        setItemSize(game.getWidth()*1.1f, FONT_SIZE + 20);
        setCenter(game.getWidth() / 2, game.getHeight() / 2);
        createHighlight();

        float y = bounds.y + bounds.height/2 - itemBounds.height/2;
        float x = bounds.x;
        Rectangle bnds = new Rectangle(x, y, itemBounds.width, itemBounds.height);
        items.add(new MainMenuItem("play", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    state.addMenu(new LevelsMenu(state, game));
                }
            }));

        y -= bnds.height;
        bnds.y -= bnds.height;
        items.add(new MainMenuItem("editor", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    state.addMenu(new EditorWorldsMenu(state, game));
                }
            }));

        y -= bnds.height;
        bnds.y -= bnds.height;
        items.add(new MainMenuItem("options", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    state.addMenu(new OptionsMenu(state, game));
                }
            }));

        y -= bnds.height;
        bnds.y -= bnds.height;
        items.add(new MainMenuItem("credits", x, y, bnds));

        y -= bnds.height;
        bnds.y -= bnds.height;
        items.add(new MainMenuItem("quit", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    game.exit();
                }
            }));

        GameTweenManager.get().register("main_menu_select_menu", new GameTweenManager.GameTween() {
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
        }).register("main_menu_select_menu_border", new GameTweenManager.GameTween() {
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
        GameTweenManager.get().restart("main_menu_select_menu");
        GameTweenManager.get().restart("main_menu_select_menu_border");
        return true;
    }

    @Override
    public void render() {
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
    public boolean buttonDown(int buttonCode) {
        if (buttonCode == InputManager.Back) {
            menuState.getGame().exit();
        }
        return super.buttonDown(buttonCode);
    }

    @Override
    public void remove() {
    }

    public void dispose() {
        GameTweenManager.get().remove("main_menu_select_menu");
        GameTweenManager.get().remove("main_menu_select_menu_border");
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
