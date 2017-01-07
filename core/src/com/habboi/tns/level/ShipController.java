package com.habboi.tns.level;

import com.habboi.tns.utils.InputManager;

/**
 * Controls the ship, also record input.
 */
public class ShipController {
    InputManager inputManager;
    boolean recording;
    float time;

    public ShipController(boolean record) {
        recording = record;
        inputManager = InputManager.getInstance();
    }

    public void update(float dt) {
        if (recording) {
            time += dt;
        }
    }
}
