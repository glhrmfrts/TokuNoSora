package com.habboi.tns;

import java.util.HashMap;
import java.util.Map;

public class GameConfig {

    private static GameConfig instance;

    public Map<String, Object> values = new HashMap<>();

    private GameConfig() {
        values.put("music_volume", (float)0.75f);
        values.put("sfx_volume", (float)0.75f);
    }

    public static GameConfig get() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    public float getFloat(String key) {
        return (float)values.get(key);
    }
}
