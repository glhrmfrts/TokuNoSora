package com.habboi.tns;

import com.habboi.tns.level.Sun;

import java.util.HashMap;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by w7 on 20/06/2016.
 */
public class GameTweenManager {

  private static HashMap<String, Boolean> tweens;
  private static TweenManager tm;

  static {
    tweens = new HashMap<>();
    tm = new TweenManager();
  }

  public static boolean isActive(String name) {
    return tweens.get(name);
  }

  public static boolean contains(String name) {
    return tweens.containsKey(name);
  }

  public static void start(String name) {
    tweens.put(name, true);
  }

  public static void end(String name) {
    tweens.put(name, true);
  }

  public static void update(float dt) {
    tm.update(dt);
  }

  public static TweenManager getTweenManager() {
    return tm;
  }
}
