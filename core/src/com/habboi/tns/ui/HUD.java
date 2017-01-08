package com.habboi.tns.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.habboi.tns.Game;
import com.habboi.tns.level.Ship;
import com.habboi.tns.utils.FontManager;

public class HUD {
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

        oxygenRectOutline = new Rect(new Rectangle(w - BORDER_WIDTH - OXYGEN_RECT_WIDTH, BORDER_WIDTH, OXYGEN_RECT_WIDTH, OXYGEN_RECT_HEIGHT), Color.WHITE);
        oxygenRect = new Rect(new Rectangle(w - BORDER_WIDTH - OXYGEN_RECT_WIDTH + 5, oxygenRectOutline.rect.y + 5, OXYGEN_RECT_WIDTH - 10, OXYGEN_RECT_HEIGHT - 10), Color.WHITE);
    
        oxygenText = new Text(
            FontManager.get().getFont(Game.MAIN_FONT, FONT_SIZE),
            "oxygen",
            null,
            Color.WHITE
        );
        oxygenText.pos.set(oxygenRect.rect.x, BORDER_WIDTH + oxygenRectOutline.rect.height + oxygenText.bounds.height + 10);
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