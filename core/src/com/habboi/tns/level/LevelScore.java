package com.habboi.tns.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by w7 on 10/07/2016.
 */
public class LevelScore {
  private static LevelScore instance;

  private Preferences prefs;

  public static LevelScore get() {
    if (instance == null) {
      instance = new LevelScore();
    }
    return instance;
  }

  private LevelScore() {
    prefs = Gdx.app.getPreferences("taiho");
  }

  private String getLevelPrefKey(String levelName, String key) {
    return levelName + "_" + key;
  }

  public int getTimesCompleted(String levelName) {
    String key = getLevelPrefKey(levelName, "completed");
    return prefs.getInteger(key, 0);
  }

  public float getBestTime(String levelName) {
    String key = getLevelPrefKey(levelName, "best_time");
    return prefs.getFloat(key, 0);
  }

  public void setTimesCompleted(String levelName, int times) {
    String key = getLevelPrefKey(levelName, "completed");
    prefs.putInteger(key, times);
  }

  public void setBestTime(String levelName, float time) {
    String key = getLevelPrefKey(levelName, "best_time");
    prefs.putFloat(key, time);
  }

  public void flush() {
    prefs.flush();
  }
}
