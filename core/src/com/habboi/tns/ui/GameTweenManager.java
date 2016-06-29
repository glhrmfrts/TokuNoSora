package com.habboi.tns.ui;

import java.util.HashMap;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

/**
 * Manage which tweens are active.
 */
public class GameTweenManager {
  public static abstract class GameTween {
    boolean played;

    public abstract Tween tween();
    public abstract void onComplete();
  }
  private static GameTweenManager instance;

  private HashMap<String, GameTween> tweens;
  private HashMap<String, Tween> active;
  private TweenManager tm;

  public static GameTweenManager get() {
    if (instance == null) {
      instance = new GameTweenManager();
    }
    return instance;
  }

  private GameTweenManager() {
    tweens = new HashMap<>();
    active = new HashMap<>();
    tm = new TweenManager();
  }

  public GameTweenManager register(String name, GameTween tween) {
    tweens.put(name, tween);
    return this;
  }

  public boolean isActive(String name) {
    return active.containsKey(name);
  }

  public boolean played(String name) {
    return tweens.get(name).played;
  }

  public void remove(String name) {
    tweens.remove(name);
  }

  public void start(final String name) {
    final GameTween gt = tweens.get(name);
    Tween tween = gt.tween();
    tween.setCallbackTriggers(TweenCallback.COMPLETE);
    tween.setCallback(new TweenCallback() {
      @Override
      public void onEvent(int i, BaseTween<?> baseTween) {
        active.remove(name);
        gt.onComplete();
      }
    });
    tween.start(tm);
    gt.played = true;
    active.put(name, tween);
  }

  public void restart(final String name) {
    if (isActive(name)) {
      active.get(name).start(tm);
    } else {
      start(name);
    }
  }

  public void update(float dt) {
    tm.update(dt);
  }

  public TweenManager getTweenManager() {
    return tm;
  }
}
