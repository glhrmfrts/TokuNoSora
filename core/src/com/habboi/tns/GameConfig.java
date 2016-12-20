package com.habboi.tns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameConfig {

    private static GameConfig instance;
    public static final String PREFERENCES_NAME = "taiho";

    public Preferences prefs;

    private GameConfig() {
        prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    public static GameConfig get() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public float getMusicVolume() {
        return prefs.getFloat("music_volume", 0.5f);
    }

    public float getSfxVolume() {
        return prefs.getFloat("sfx_volume", 0.5f);
    }
}
