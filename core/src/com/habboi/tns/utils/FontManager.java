package com.habboi.tns.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;

/**
 * Loads fonts of different sizes.
 */
public class FontManager {
  private AssetManager am;
  private HashMap<String, FontLoader.FontParameter> fontParams;

  public FontManager(AssetManager am) {
    this.am = am;
    this.fontParams = new HashMap<>();
  }

  public void loadFont(String file, int size) {
    String key = getKey(file, size);
    FontLoader.FontParameter param = new FontLoader.FontParameter(size);
    fontParams.put(key, param);
    am.load(key, BitmapFont.class, param);
  }

  public BitmapFont getFont(String file, int size) {
    String key = getKey(file, size);
    if (!am.isLoaded(key) || !fontParams.containsKey(key)) {
      throw new GdxRuntimeException("font " + file + " not loaded with size: " + size);
    }
    return fontParams.get(key).font;
  }

  private String getKey(String file, int size) {
    return file + "." + size;
  }
}
