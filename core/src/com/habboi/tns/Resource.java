package com.habboi.tns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * Created by w7 on 20/06/2016.
 */
public class Resource {

  private static HashMap<String, Sound> sounds = new HashMap<>();
  private static HashMap<String, Texture> textures = new HashMap<>();
  private static HashMap<String, BitmapFont> fonts = new HashMap<>();

  private static HashMap<String, TextureRegion[][]> tilesets = new HashMap<>();

  public static Sound loadSound(String name) {
    if (sounds.containsKey(name)) {
      return sounds.get(name);
    } else {
      Sound sound = Gdx.audio.newSound(Gdx.files.internal(name));
      sounds.put(name, sound);
      return sound;
    }
  }

  public static Texture loadTexture(String name) {
    if (textures.containsKey(name)) {
      return textures.get(name);
    } else {
      Texture t = new Texture(Gdx.files.internal(name));
      textures.put(name, t);
      return t;
    }
  }

  public static BitmapFont getFont(String file, int size) {
    String key = file + size;
    if (fonts.containsKey(key)) {
      return fonts.get(key);
    } else {
      BitmapFont font = Util.generateFont(file, size);
      fonts.put(key, font);
      return font;
    }
  }

  public static TextureRegion[][] getTileset(String file, int width, int height) {
    Texture tex = loadTexture(file);
    String key = file + width + height;
    if (tilesets.containsKey(key)) {
      return tilesets.get(key);
    } else {
      TextureRegion[][] tiles = TextureRegion.split(tex, width, height);
      tilesets.put(key, tiles);
      return tiles;
    }
  }
}
