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

    public int getGraphicLevel() {
        return prefs.getInteger("graphic_level", 0);
    }

    public float getMusicVolume() {
        return prefs.getFloat("music_volume", 0.5f);
    }

    public float getSfxVolume() {
        return prefs.getFloat("sfx_volume", 0.5f);
    }

    public void setGraphicLevel(int value) {
        prefs.putInteger("graphic_level", value);
    }

    public void setMusicVolume(float value) {
        prefs.putFloat("music_volume", value);
    }

    public void setSfxVolume(float value) {
        prefs.putFloat("sfx_volume", value);
    }
}
