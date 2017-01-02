package com.habboi.tns.utils;

import com.badlogic.gdx.graphics.g3d.Shader;

import java.util.HashMap;

public class Shaders {
  private static HashMap<Class<? extends Shader>, Shader> shaders;

  static {
    shaders = new HashMap<>();
  }

  public static <T extends Shader> T get(Class<T> cls) {
    if (!shaders.containsKey(cls)) {
      try {
        Shader shader = cls.newInstance();
        shader.init();
        put(shader);
      } catch (Exception e) {
        return null;
      }
    }
    return (T)shaders.get(cls);
  }

  public static void put(Shader shader) {
    shaders.put(shader.getClass(), shader);
  }

  public static void dispose() {

  }
}
