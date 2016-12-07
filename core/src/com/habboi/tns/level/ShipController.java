package com.habboi.tns.level;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Controls the ship, also record input.
 */
public class ShipController implements InputProcessor {
    enum Key {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        JUMP
    }
    static final int NUM_KEYS = 5;

    int[] keys = new int[NUM_KEYS];
    int[] prevKeys = new int[NUM_KEYS];
    boolean recording;
    float time;
    int anyKey;

    public ShipController(boolean record) {
        recording = record;
    }

    public boolean isDown(Key key) {
        return keys[key.ordinal()] > 0;
    }

    public boolean isJustDown(Key key) {
        return isDown(key) && !(prevKeys[key.ordinal()] > 0);
    }

    public boolean isAnyKeyDown() {
        return anyKey > 0;
    }

    public void reset() {
        for (int i = 0; i < NUM_KEYS; i++) {
            keys[i] = 0;
            prevKeys[i] = 0;
        }
    }

    public void update(float dt) {
        if (recording) {
            time += dt;
        }

        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
        anyKey = 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        anyKey++;
        switch (keycode) {
        case Input.Keys.LEFT:
            keys[Key.LEFT.ordinal()]++;
            return true;

        case Input.Keys.RIGHT:
            keys[Key.RIGHT.ordinal()]++;
            return true;

        case Input.Keys.UP:
            keys[Key.UP.ordinal()]++;
            return true;

        case Input.Keys.DOWN:
            keys[Key.DOWN.ordinal()]++;
            return true;

        case Input.Keys.SPACE:
            keys[Key.JUMP.ordinal()]++;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (anyKey > 0) {
            anyKey--;
        }

        switch (keycode) {
        case Input.Keys.LEFT:
            keys[Key.LEFT.ordinal()] = 0;
            return true;

        case Input.Keys.RIGHT:
            keys[Key.RIGHT.ordinal()] = 0;
            return true;

        case Input.Keys.UP:
            keys[Key.UP.ordinal()] = 0;
            return true;

        case Input.Keys.DOWN:
            keys[Key.DOWN.ordinal()] = 0;
            return true;

        case Input.Keys.SPACE:
            keys[Key.JUMP.ordinal()] = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
