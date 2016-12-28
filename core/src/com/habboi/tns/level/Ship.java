package com.habboi.tns.level;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;

public class Ship {
    public enum State {
        WAITING,
        PLAYABLE,
        EXPLODED,
        FELL,
        ENDED
    }
    
    public State state = State.WAITING;
    public Vector3 vel = new Vector3();
    public boolean readyToEnd;
    public float raceTime;
    public TileShape shape;

    static final float BODY_WIDTH = 0.60f;
    static final float BODY_HEIGHT = 0.3f;
    static final float BODY_DEPTH = 2.25f;
    static final float MAX_VEL = 65;
    static final float STEER_VEL = 12;
    static final float STEER_ACCELERATION = 50;
    static final float MAX_STEER_ACCUL = 300;
    static final float JUMP_VEL = 8;
    static final float MAX_BOUNCE_JUMP_INTERVAL = 0.15f;
    static final float MIN_BOUNCE_VEL = 1f;
    static final float MIN_BOUNCE_SOUND_INTERVAL = 0.50f;
    static final float BOUNCE_FACTOR = 0.35f;
    static final Color COLOR = new Color(0xff);
    static final Color OUTLINE_COLOR = new Color(0x00ff00<<8 | 0xFF);

    Vector3 spawnPos = new Vector3();
    Game game;
    ModelInstance bodyInstance;
    ModelInstance outlineInstance;
    ShipController controller;
    Finish finish;
    Sound bounceSound;
    Sound explosionSound;
    int floorCollisions;
    float dSlide;
    float dBounce;
    float steerAccul;

    public Ship(Game game, Vector3 pos, ShipController controller) {
        shape = new TileShape(
                              new Vector3(pos.x*TileShape.TILE_WIDTH, pos.y*TileShape.TILE_HEIGHT, -pos.z*TileShape.TILE_DEPTH),
                              new Vector3(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH)
                              );
        shape.pos.z -= shape.half.z;

        this.spawnPos.set(shape.pos);
        this.controller = controller;
        this.game = game;

        bodyInstance = new ModelInstance(Models.getShipModel());
        bodyInstance.transform.setToScaling(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH);

        outlineInstance = new ModelInstance(Models.getShipOutlineModel());
        outlineInstance.transform.setToScaling(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH);

        Renderable r = new Renderable();
        ColorAttribute attr;

        // set the body color
        bodyInstance.getRenderable(r);
        attr = (ColorAttribute) r.material.get(ColorAttribute.Diffuse);
        attr.color.set(COLOR);

        // set the outline color
        outlineInstance.getRenderable(r);
        attr = (ColorAttribute) r.material.get(ColorAttribute.Diffuse);
        attr.color.set(OUTLINE_COLOR);

        bounceSound = game.getAssetManager().get("audio/bounce.wav");
        explosionSound = game.getAssetManager().get("audio/explosion.wav");
    }

    public boolean canReceiveInput() {
        return state == State.PLAYABLE || state == State.WAITING || state == State.ENDED;
    }

    public Finish getFinish() {
        return finish;
    }

    public ShipController getController() {
        return controller;
    }

    private static float steer(float c, float t, float a, float dt) {
        if (c == t) {
            return t;
        }
        float dir = Math.signum(t - c);
        c += a * dir * dt;
        return (dir == Math.signum(t - c)) ? c : t;
    }

    public void reset() {
        raceTime = 0;
        state = State.WAITING;
        shape.pos.set(spawnPos);
        vel.set(0, 0, 0);
        controller.reset();
    }

    public void accelerate(boolean forward) {
        if (forward) {
            if (vel.z > -MAX_VEL) {
                vel.z -= 1;
            } else {
                vel.z = -MAX_VEL;
            }
        } else {
            if (vel.z < 0) {
                vel.z += 1;
            } else {
                vel.z = 0;
            }
        }
    }

    public void update(float dt) {
        if (state == State.WAITING || state == State.ENDED) {
            return;
        }
        if (controller.isDown(ShipController.Key.UP)) {
            accelerate(true);
        } else if (controller.isDown(ShipController.Key.DOWN)) {
            accelerate(false);
        }

        boolean isBouncing = dBounce < MAX_BOUNCE_JUMP_INTERVAL;
        if (floorCollisions > 0 || isBouncing) {
            if (controller.isDown(ShipController.Key.JUMP)) {
                float pvy = vel.y;
                vel.y = JUMP_VEL;

                if (isBouncing) {
                    vel.y -= Math.max(pvy * 0.25f, 0);
                }
                
            } else if (dSlide != 0) {
                vel.y = Math.min(vel.y, 0);
            }
        }

        if (floorCollisions > 0) {
            // only steer and jump when ship is on the floor
            float target = 0;
            if ((dSlide <= 0 || floorCollisions > 1) && controller.isDown(ShipController.Key.LEFT)) {
                target = -STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
            } else if ((dSlide >= 0 || floorCollisions > 1) && controller.isDown(ShipController.Key.RIGHT)) {
                target = STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
            }
            float accel = STEER_ACCELERATION + steerAccul;
            if (target == 0) {
                accel *= 4;
            }
            vel.x = steer(vel.x, target, accel, dt);
            steerAccul = 0;
        } else {
            if (controller.isDown(ShipController.Key.LEFT) || controller.isDown(ShipController.Key.RIGHT)) {
                steerAccul = Math.min(steerAccul + STEER_VEL, MAX_STEER_ACCUL);
            }
        }

        if (shape.pos.y < -5) {
            state = Ship.State.FELL;
        }

        floorCollisions = 0;
        dSlide = 0;
        controller.update(dt);
        raceTime += dt;
        dBounce += dt;
    }

    public void render(GameRenderer renderer, int pass) {
        if (state == State.ENDED) {
            return;
        }

        bodyInstance.transform.setTranslation(shape.pos);
        renderer.render(bodyInstance);
        outlineInstance.transform.setTranslation(shape.pos);
        renderer.render(outlineInstance);
    }

    public boolean handleCollision(LevelObject obj) {
        Shape.CollisionInfo c = obj.shape.getCollisionInfo();
        if (obj.effect == TouchEffect.END) {
            if (readyToEnd) {
                state = State.ENDED;
                finish = (Finish) obj;
            }
            return false;
        }
        if (c.normal.z == 1 && -vel.z > MAX_VEL/2) {
            if (state == State.PLAYABLE) {
                explosionSound.play(GameConfig.get().getSfxVolume());
            }
            state = State.EXPLODED;
            return true;
        }
        if (c.normal.y == 1) {
            floorCollisions++;

            if (vel.y < -MIN_BOUNCE_VEL) {
                vel.x = 0;
                vel.y = -vel.y * BOUNCE_FACTOR;
                playBounceSound();
            }

            if (obj.effect == TouchEffect.BOOST) {
                accelerate(true);
            }
        }
        if (Math.abs(c.normal.x) == 1 && Math.abs(vel.x) > MIN_BOUNCE_VEL) {
            playBounceSound();
        }
        if (Math.abs(c.normal.z) == 1 && Math.abs(vel.z) > MIN_BOUNCE_VEL) {
            playBounceSound();
        }
        if (c.slide != 0) {
            float dx = Math.copySign(1, vel.x);
            float ds = Math.copySign(1, c.slide);
            if (dx == ds || vel.x == 0) {
                c.normal.x = c.slide;
                dSlide = c.slide;
            }
        }
        return true;
    }

    private void playBounceSound() {
        if (dBounce > MIN_BOUNCE_SOUND_INTERVAL) {
            bounceSound.play(GameConfig.get().getSfxVolume());
            dBounce = 0;
        }
    }
}
