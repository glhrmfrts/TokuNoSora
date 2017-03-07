package com.habboi.tns.level;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.GameConfig;
import com.habboi.tns.rendering.Fragment;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.shapes.Shape;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.utils.EventEmitter;
import com.habboi.tns.utils.InputManager;
import com.habboi.tns.utils.Models;

import java.util.concurrent.TimeUnit;

public class Ship extends LevelObject {
    public enum State {
        WAITING,
        PLAYABLE,
        EXPLODED,
        FELL,
        ENDED
    }

    public int collected;
    public State state = State.WAITING;
    public Vector3 vel = new Vector3();
    public boolean readyToEnd;
    public float raceTime;
    public long raceTimeMillis;
    public float oxygenLevel;
    public TileShape shape;
    public Fragment body;

    public static ShipExplosion explosion;

    static final float BODY_WIDTH = 0.6f;
    static final float BODY_HEIGHT = 0.5f;
    static final float BODY_DEPTH = 2f;
    static final float BODY_OFF = 0.98f;
    static final float MAX_VEL = 65;
    static final float STEER_VEL = 10;
    static final float STEER_ACCELERATION = 50;
    static final float MAX_STEER_ACCUL = 300;
    static final float JUMP_VEL = 8;
    static final float MAX_BOUNCE_JUMP_INTERVAL = 0.15f;
    static final float MIN_BOUNCE_VEL = 1f;
    static final float MIN_BOUNCE_SOUND_INTERVAL = 0.50f;
    static final float BOUNCE_FACTOR = 0.35f;
    static final float OXYGEN_FILL_FACTOR = 0.50f;
    static final Color COLOR = new Color(0xff);
    static final float MODEL_SCALE = 0.1f;
    static final float MODEL_OFFSET = 0.5f;

    Vector3 spawnPos = new Vector3();
    Game game;
    ShipController controller;
    Finish finish;
    Sound bounceSound;
    Sound explosionSound;
    int floorCollisions;
    float dSlide;
    float dBounce;
    float steerAccul;
    boolean fillOxygen;
    Quaternion rotationGetter = new Quaternion();

    public Ship(Game game, Vector3 pos, ShipController controller) {
        shape = new TileShape(
                              new Vector3(pos.x*TileShape.TILE_WIDTH, pos.y*TileShape.TILE_HEIGHT, -pos.z*TileShape.TILE_DEPTH),
                              new Vector3(BODY_WIDTH, BODY_HEIGHT, BODY_DEPTH)
                              );
        shape.pos.z -= shape.half.z;

        this.spawnPos.set(shape.pos);
        this.controller = controller;
        this.game = game;
        
        body = new Fragment(new ModelInstance(game.getAssetManager().get("models/ship.obj", Model.class)));
        body.modelInstance.materials.get(2).set(IntAttribute.createCullFace(GL20.GL_FRONT_FACE));
        
        bounceSound = game.getAssetManager().get("audio/bounce.wav");
        explosionSound = game.getAssetManager().get("audio/explosion.wav");

        if (explosion == null) {
            explosion = new ShipExplosion();
        } else {
            explosion.reset();
        }

        reset();
    }

    @Override
    public void addToScene(Scene scene) {
        scene.add(body);
        explosion.addToScene(scene);
    }

    public boolean canReceiveInput() {
        return state == State.PLAYABLE || state == State.WAITING || state == State.ENDED;
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

    public String getTimeText() {
        long millis = raceTimeMillis;
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
            millis % 1000).substring(0, 8);
    }

    public void reset() {
        collected = 0;
        raceTime = 0;
        raceTimeMillis = 0;
        body.visible(true);
        fillOxygen = false;
        oxygenLevel = 1;
        state = State.WAITING;
        shape.pos.set(spawnPos);
        vel.set(0, 0, 0);
        explosion.reset();

        body.modelInstance.transform.setToRotation(1, 1, 1, 0);

        body.modelInstance.transform.setToScaling(BODY_WIDTH*BODY_OFF*MODEL_SCALE, BODY_HEIGHT*BODY_OFF*MODEL_SCALE, BODY_DEPTH*BODY_OFF*MODEL_SCALE);
        body.modelInstance.transform.rotate(Vector3.Y, 180);
    }

    public void accelerate(float amount) {
        if (amount > 0) {
            if (vel.z > -MAX_VEL) {
                vel.z -= amount;
            } else {
                vel.z = -MAX_VEL;
            }
        } else if (amount < 0) {
            if (vel.z < 0) {
                vel.z -= amount;
            } else {
                vel.z = 0;
            }
        }
    }

    public void update(float dt) {
        body.modelInstance.transform.setTranslation(shape.pos.x, shape.pos.y, shape.pos.z + MODEL_OFFSET);

        if (state == State.WAITING || state == State.ENDED || state == State.EXPLODED) {
            return;
        }

        if (fillOxygen && oxygenLevel < 1.0f) {
            oxygenLevel += OXYGEN_FILL_FACTOR * dt;
        }

        float acceleration = controller.inputManager.getAxis(InputManager.Acceleration);
        if (acceleration != 0) {
            accelerate(acceleration);
        }

        boolean isBouncing = dBounce < MAX_BOUNCE_JUMP_INTERVAL;
        if (floorCollisions > 0 || isBouncing) {
            if (controller.inputManager.isButtonDown(InputManager.Jump)) {
                float pvy = vel.y;
                vel.y = JUMP_VEL;

                if (isBouncing) {
                    vel.y -= Math.max(pvy * 0.25f, 0);
                }

            } else if (dSlide != 0) {
                vel.y = Math.min(vel.y, 0);
            }
        }

        float steerAxis = controller.inputManager.getAxis(InputManager.Horizontal);
        if (floorCollisions > 0) {
            // only steer and jump when ship is on the floor
            float target = 0;

            if ((dSlide <= 0 || floorCollisions > 1) && steerAxis < 0) {
                target = steerAxis * STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
            } else if ((dSlide >= 0 || floorCollisions > 1) && steerAxis > 0) {
                target = steerAxis * STEER_VEL * Math.max(-vel.z / MAX_VEL, 0.25f);
            }

            float accel = STEER_ACCELERATION + steerAccul;
            if (target == 0) {
                accel *= 4;
            }
            vel.x = steer(vel.x, target, accel, dt);
            steerAccul = 0;
        } else {
            if (steerAxis != 0) {
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
        fillOxygen = false;
        raceTimeMillis = (long)(raceTime * 1000);
    }

    public void doExplode() {
        if (state == State.PLAYABLE) {
            vel.set(0, 0, 0);
            body.visible(false);
            explosion.explode(shape.pos);
            explosionSound.play(GameConfig.get().getSfxVolume());
        }
        state = State.EXPLODED;
    }

    public void onCollect(Collectible c) {
        collected++;
    }

    public boolean onCollision(LevelObject obj) {
        Shape.CollisionInfo c = obj.shape.getCollisionInfo();
        if (obj.effect == TouchEffect.END) {
            if (readyToEnd) {
                state = State.ENDED;
                finish = (Finish) obj;
                body.visible(false);
            }
            return false;
        }
        if (c.normal.z == 1 && -vel.z > MAX_VEL/2) {
            doExplode();
            return true;
        }
        if (c.normal.y == 1) {
            floorCollisions++;

            body.modelInstance.transform.getRotation(rotationGetter);
            body.modelInstance.transform.rotate(Vector3.Z, (float)(-rotationGetter.x * 180 / Math.PI));

            if (obj.effect == TouchEffect.EXPLODE) {
                doExplode();
                return true;
            }

            if (vel.y < -MIN_BOUNCE_VEL) {
                vel.x = 0;
                vel.y = -vel.y * BOUNCE_FACTOR;
                playBounceSound();
                return true;
            }

            if (obj.effect == TouchEffect.BOOST && state == State.PLAYABLE) {
                accelerate(1);
                return true;
            }

            if (obj.effect == TouchEffect.OXYGEN && state == State.PLAYABLE) {
                fillOxygen = true;
                return true;
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

                body.modelInstance.transform.rotate(Vector3.Z, dSlide);
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

    static class ShipExplosion extends Explosion {

        public static final int TRIANGLE_COUNT = 12;

        public ShipExplosion() {
            super(TRIANGLE_COUNT, Models.getTriangleModel());

            for (Explosion.Piece p : pieces) {
                p.modelInstance.transform.setToScaling(0.25f, 0.25f, 0.25f);
                Models.setColor(p.modelInstance, ColorAttribute.Diffuse, Ship.COLOR);
            }
        }

        @Override
        public void onExplosionEnd() {
            EventEmitter.get().notify("ship_explosion_end", null);
        }
    } 
}
