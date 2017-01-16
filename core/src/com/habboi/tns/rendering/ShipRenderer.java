package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.habboi.tns.level.Ship;

public class ShipRenderer implements LevelObjectRenderer<Ship> {

    private static ShipRenderer instance;

    public static ShipRenderer getInstance() {
        if (instance == null) {
            instance = new ShipRenderer();
        }
        return instance;
    }

    private ShipRenderer() {
    }

    @Override
    public void render(Ship ship, ModelBatch batch, Environment environment) {
        batch.render(ship.modelInstance, environment);
        batch.render(ship.outlineInstance, environment);
    }
}