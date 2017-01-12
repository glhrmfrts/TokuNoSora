package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.habboi.tns.level.ShipExplosion;

public class ShipExplosionRenderer implements LevelObjectRenderer<ShipExplosion> {

    private static ShipExplosionRenderer instance;

    public static ShipExplosionRenderer getInstance() {
        if (instance == null) {
            instance = new ShipExplosionRenderer();
        }
        return instance;
    }

    private ShipExplosionRenderer() {
    }

    @Override
    public void render(ShipExplosion explosion, ModelBatch batch, Environment environment) {
        for (ShipExplosion.Triangle t : explosion.triangles) {
            batch.render(t.modelInstance, environment);
        }
    }
}