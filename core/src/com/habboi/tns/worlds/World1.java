package com.habboi.tns.worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.utils.Models;
import com.habboi.tns.rendering.GameRenderer;

import java.util.ArrayList;

/**
 * Created by w7 on 03/07/2016.
 */
public class World1 extends World {
    static class Line {
        float offset;
        ModelInstance instance;
    }

    static final float DEPTH = 800f;
    static final float DISTANCE_Y = -10;
    static final float SUN_RADIUS = 150;
    static final float WIDTH = 400f;

    Texture backgroundTexture;
    float centerX;
    int count;
    ArrayList<Line> lines = new ArrayList<>();
    float offset;
    ModelInstance planeInstance;
    Vector3 shipPos = new Vector3();
    float spread;
    ModelInstance sunInstance;
    int verticalCount;
    ArrayList<Line> verticalLines = new ArrayList<>();

    public World1() {
        super(2, 2, "Red Heat", "audio/world1.ogg");
        addColor(0x370737);
        addColor(0xf540ef);
        addColor(0x00ff00);
        addColor(0x1a37f5);
        addColor(0x5a67c7);
        addTileModel(new int[][]{
                new int[]{0, 0, 0, 0}, new int[]{1, 0, 0, 1}, new int[]{0, 0, 0, 0},
                new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}
            });
        addTileModel(4);
        addTileModel(3);
        addTileModel(2);

        addTunnelModel(new int[]{1, 4});

        // create the landscape
        count = 40;
        spread = DEPTH / count;
        planeInstance = new ModelInstance(Models.createPlaneModel(colors.get(0)));
        planeInstance.transform.setToScaling(WIDTH, 1, DEPTH);

        Model lineModel = Models.createLineModel(colors.get(1), new int[]{-1, 0, 0, 1, 0, 0});
        for (int i = 0; i < count; i++) {
            Line line = new Line();
            line.offset = spread * i;
            line.instance = new ModelInstance(lineModel);
            line.instance.transform.setToScaling(WIDTH, 1, 1);
            lines.add(line);
        }

        Model verticalLineModel = Models.createLineModel(colors.get(1), new int[]{0, 0, -1, 0, 0, 1});
        verticalCount = (int)(WIDTH / (int)spread);
        for (int i = 0; i < verticalCount; i++) {
            Line line = new Line();
            line.offset = spread * i;
            line.instance = new ModelInstance(verticalLineModel);
            line.instance.transform.setToScaling(1, 1, DEPTH);
            verticalLines.add(line);
        }

        sunInstance = new ModelInstance(Models.getSunModel());
        sunInstance.transform.setToScaling(SUN_RADIUS, SUN_RADIUS, 1);

        // the texture would be loaded synchronously anyway, so :)
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
    }

    @Override
    public void setCenterX(float cx) {
        centerX = cx;
    }

    @Override
    public void reset() {
        offset = 0;
    }

    @Override
    public void update(Vector3 shipPos, float vel, float dt) {
        this.shipPos.set(shipPos);
        offset = vel * dt * 2;
    }

    @Override
    public void render(GameRenderer renderer) {
        renderer.clear(Color.BLACK);
        renderBackground(renderer);

        planeInstance.transform.setTranslation(centerX, DISTANCE_Y - 0.5f, shipPos.z);
        renderer.render(planeInstance);

        final float base = shipPos.z - DEPTH/1.1f;
        for (int i = 0; i < count; i++) {
            Line line = lines.get(i);
            line.offset += offset;
            line.offset %= DEPTH;
            line.instance.transform.setTranslation(centerX, DISTANCE_Y, base + line.offset);
            renderer.renderGlow(line.instance);
        }

        for (int i = 0; i < verticalCount; i++) {
            Line line = verticalLines.get(i);
            line.instance.transform.setTranslation(centerX - (WIDTH * 0.5f) + line.offset, DISTANCE_Y, shipPos.z + DEPTH * 0.25f);
            renderer.renderGlow(line.instance);
        }

        sunInstance.transform.setTranslation(centerX, DISTANCE_Y, shipPos.z - DEPTH);
        renderer.renderGlowOnly(sunInstance);

        offset = 0;
    }

    private void renderBackground(GameRenderer renderer) {
        renderer.beginOrtho();
        renderer.getSpriteBatch().draw(backgroundTexture, 0, 0);
        renderer.endOrtho();
    }

    @Override
    public void dispose() {
        lines.get(0).instance.model.dispose();
        lines.clear();
    }
}
