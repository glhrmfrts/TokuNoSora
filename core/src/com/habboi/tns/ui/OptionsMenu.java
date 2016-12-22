package com.habboi.tns.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.states.MenuState;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.utils.EventEmitter;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

public class OptionsMenu extends BaseMenu implements Disposable {
    static final int FONT_SIZE = Game.MAIN_FONT_SIZE;

    MenuState menuState;
    SliderOptionsMenuItem musicItem, sfxItem;

    public OptionsMenu(final MenuState state, final Game game) {
        menuState = state;
        sr = game.getShapeRenderer();
        sb = game.getRenderer().getSpriteBatch();

        setSize(game.getWidth() * 1.1f, FONT_SIZE * 2 + 20 * 2);
        setItemSize(game.getWidth()*1.1f, FONT_SIZE + 20);
        setCenter(game.getWidth() / 2, game.getHeight() / 2);

        createHighlight();

        float y = bounds.y + bounds.height/2 - itemBounds.height/2;
        float x = bounds.x;
        Rectangle bnds = new Rectangle(x, y, itemBounds.width, itemBounds.height);

        musicItem = new SliderOptionsMenuItem("music volume", x, y, bnds, GameConfig.get().getMusicVolume());
        musicItem.setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    GameConfig.get().setMusicVolume(musicItem.getValue());
                    EventEmitter.get().notify("music_volume_update", musicItem.getValue());
                }
            });
        items.add(musicItem);

        y -= bnds.height;
        bnds.y -= bnds.height;
        sfxItem = new SliderOptionsMenuItem("sfx volume", x, y, bnds, GameConfig.get().getSfxVolume());
        sfxItem.setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    GameConfig.get().setSfxVolume(sfxItem.getValue());
                }
            });
        items.add(sfxItem);

        y -= bnds.height;
        bnds.y -= bnds.height;
        items.add(new DefaultOptionsMenuItem("back", x, y, bnds).setAction(new MenuItemAction() {
                @Override
                public void doAction() {
                    menuState.popMenu();
                }
            }));

        GameTweenManager.get().register("options_menu_select_menu", new GameTweenManager.GameTween() {
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
        }).register("options_menu_select_menu_border", new GameTweenManager.GameTween() {
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
        GameTweenManager.get().restart("options_menu_select_menu");
        GameTweenManager.get().restart("options_menu_select_menu_border");
        return true;
    }

    @Override
    public void render() {
        highlight.draw(sr, true);
        highlightBorder.draw(sr, ShapeRenderer.ShapeType.Line, true);

        for (int i = 0; i < items.size(); i++) {
            OptionsMenuItem item = (OptionsMenuItem)items.get(i);
            item.renderShapes(sr);
        }

        sb.begin();
        for (int i = 0; i < items.size(); i++) {
            OptionsMenuItem item = (OptionsMenuItem)items.get(i);
            item.renderSprites(sb);
        }
        sb.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            menuState.popMenu();
        }

        if (keycode == Input.Keys.LEFT) {
            ((OptionsMenuItem)items.get(activeIndex)).valueChange(-1);
        }
        if (keycode == Input.Keys.RIGHT) {
            ((OptionsMenuItem)items.get(activeIndex)).valueChange(1);
        }

        return super.keyDown(keycode);
    }

    @Override
    public void remove() {
        GameConfig.get().getPrefs().flush();
    }

    public void dispose() {
        GameTweenManager.get().remove("options_menu_select_menu");
        GameTweenManager.get().remove("options_menu_select_menu_border");
    }

    interface OptionsMenuItem {
        public void renderShapes(ShapeRenderer sr);
        public void renderSprites(SpriteBatch sb);
        public void valueChange(int delta);
    }

    static class DefaultOptionsMenuItem extends MenuItem implements OptionsMenuItem {
        Text text;

        public DefaultOptionsMenuItem(String textValue, float textX, float textY, Rectangle bounds) {
            super(bounds);
            text = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), textValue, null, Color.WHITE);
            text.getPos().set(textX, textY);
        }

        @Override
        public void renderShapes(ShapeRenderer sr) {
        }

        @Override
        public void renderSprites(SpriteBatch sb) {
            text.draw(sb, true);
        }

        @Override
        public void valueChange(int delta) {}
    }

    static class SliderOptionsMenuItem extends MenuItem implements OptionsMenuItem {
        Text text;
        Rect innerRect;
        Rect outerRect;
        float value;

        public SliderOptionsMenuItem(String textValue, float x, float y, Rectangle bounds, float value) {
            super(bounds);

            final float sideCenter = bounds.width / 6;

            text = new Text(FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE), textValue, null, Color.WHITE);
            text.getPos().set(x - (20 + sideCenter), y);

            outerRect = new Rect(new Rectangle(
                                               x + (20 + sideCenter),
                                               y - bounds.height / 2,
                                               sideCenter,
                                               bounds.height - 20
                                               ));
            innerRect = new Rect(new Rectangle(
                                               outerRect.getRectangle().x - (sideCenter/2) + 5,
                                               outerRect.getRectangle().y - (outerRect.getRectangle().height / 2) + 5,
                                               outerRect.getRectangle().width - 10,
                                               outerRect.getRectangle().height - 10
                                               ));

            innerRect.getColor().set(Color.WHITE);
            outerRect.getColor().set(Color.WHITE);

            this.value = value;
        }

        public float getValue() {
            return value;
        }

        @Override
        public void renderShapes(ShapeRenderer sr) {
            innerRect.getRectangle().width = (outerRect.getRectangle().width - 10) * value;

            innerRect.draw(sr, false);
            outerRect.draw(sr, ShapeRenderer.ShapeType.Line, true);
        }

        @Override
        public void renderSprites(SpriteBatch sb) {
            text.draw(sb, true);
        }

        @Override
        public void valueChange(int delta) {
            value += 0.05 * (float)delta;
            value = Math.max(0, Math.min(value, 1));

            action.doAction();
        }
    }
}
