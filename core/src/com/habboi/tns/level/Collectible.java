package com.habboi.tns.level;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;

public class Collectible extends LevelObject {
    static final float WIDTH = 0.25f;
    static final float HEIGHT = 0.5f;
    static final float DEPTH = 0.25f;

    public boolean collected;
    public Fragment fragment;
    private CollectibleExplosion explosion;
    private Sound sound;

    public Collectible(Vector3 pos, World world) {
        pos.x = pos.x * TileShape.TILE_WIDTH;
        pos.y = pos.y * TileShape.TILE_HEIGHT + (HEIGHT * 0.5f * TileShape.TILE_HEIGHT) + HEIGHT;
        pos.z = -pos.z * TileShape.TILE_DEPTH + (DEPTH * 0.5f * TileShape.TILE_DEPTH);

        effect = TouchEffect.COLLECT;

        TileShape tileShape = new TileShape(pos, new Vector3(WIDTH * 2, HEIGHT * 2, DEPTH * 2));
        shape = tileShape;

        fragment = new Fragment(new ModelInstance(Models.getPrismModel()));
        fragment.modelInstance.transform.setToScaling(tileShape.half);
        fragment.modelInstance.transform.setTranslation(pos);

        explosion = new CollectibleExplosion(Level.GRAVITY * world.gravityFactor);

        sound = Game.getInstance().getAssetManager().get("audio/collect.wav", Sound.class);
    }

    @Override
    public void addToScene(Scene scene) {
        scene.add(fragment);
        explosion.addToScene(scene);
    }

    @Override
    public void reset() {
        fragment.visible(true);
        collected = false;
    }

    @Override
    public void update(float dt) {
        explosion.update(dt);
    }

    public void onCollect(Ship ship) {
        explosion.maxVel = -ship.vel.z;
        explosion.explode(shape.getPos(), 0, 0.5f, 1);

        sound.play(GameConfig.get().getSfxVolume());
    }

    static class CollectibleExplosion extends Explosion {

        public static final int PRISM_COUNT = 12;

        public CollectibleExplosion(float gravity) {
            super(PRISM_COUNT, Models.getPrismModel());

            minVel = 1f;
            maxVel = 7f;
            minRot = 130f;
            maxRot = 250f;
            this.gravity = gravity;

            for (Explosion.Piece p : pieces) {
                p.modelInstance.transform.setToScaling(0.1f, 0.1f, 0.1f);
            }
        }

        @Override
        public void onExplosionEnd() {
        }
    }  
}
