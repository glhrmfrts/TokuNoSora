package com.habboi.tns.level.worlds;

import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

/**
 * The Universe has all the worlds.
 */
public class Universe implements Disposable {
  public ArrayList<World> worlds = new ArrayList<>();
  private static Universe ourInstance;

  public static Universe get() {
    if (ourInstance == null) {
      ourInstance = new Universe();
    }
    return ourInstance;
  }

  private Universe() {
  }

  public void createWorlds() {
    // The beggining of an end
    worlds.add(new World1());
    worlds.add(new World2());
  }

  @Override
  public void dispose() {
    for (World w : worlds) {
      w.dispose();
    }
  }
}
