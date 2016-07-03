package com.habboi.tns.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Loads a level from a file (synchronously because of OpenGL =/).
 */
public class LevelLoader extends SynchronousAssetLoader<Level, LevelLoader.LevelParameter> {
  ArrayList<String> levelsNames = new ArrayList<>();

  private static Vector3 parseVector3(String str) {
    String[] parts = str.split(",");
    float x = Float.parseFloat(parts[0].trim());
    float y = Float.parseFloat(parts[1].trim());
    float z = Float.parseFloat(parts[2].trim());

    return new Vector3(x, y, z);
  }

  public LevelLoader(FileHandleResolver resolver) {
    super(resolver);
  }

  @Override
  public Level load(AssetManager assetManager, String fileName, FileHandle file, LevelParameter parameter) {
    Level level;
    JsonValue root = new JsonReader().parse(file);
    String name = root.getString("name");
    int number = root.getInt("number");
    int worldIndex = root.getInt("world_index");
    Vector3 shipPos = parseVector3(root.getString("ship_pos"));
    level = new Level(name, number, worldIndex, shipPos);
    JsonValue cells = root.get("cells");
    for (JsonValue cell : cells.iterator()) {
      if (cell.has("tile")) {
        JsonValue tile = cell.get("tile");
        Vector3 pos = parseVector3(tile.getString("pos"));
        Vector3 size = parseVector3(tile.getString("size"));
        int preset = tile.getInt("preset");

        Cell.TouchEffect effect = Cell.TouchEffect.NONE;
        if (tile.has("effect")) {
          effect = Tile.TouchEffect.values()[tile.getInt("effect")];
        }

        int outline = -1;
        if (tile.has("outline")) {
          outline = tile.getInt("outline");
        }
        level.addTile(pos, size, outline, effect, preset);
      } else if (cell.has("sun")) {
        JsonValue sun = cell.get("sun");
        Vector3 pos = parseVector3(sun.getString("pos"));
        float radius = sun.getFloat("radius");

        level.addSun(pos, radius);
      } else if (cell.has("tunnel")) {
        JsonValue tunnel = cell.get("tunnel");
        Vector3 pos = parseVector3(tunnel.getString("pos"));
        int depth = tunnel.getInt("depth");
        int preset = tunnel.getInt("preset");
        boolean end = false;
        if (tunnel.has("end")) {
          end = true;
        }

        level.addTunnel(pos, depth, preset, end);
      }
    }
    return level;
  }

  @Override
  public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, LevelParameter parameter) {
    return null;
  }

  public void loadAllLevels(AssetManager am) {
    FileHandle handle = Gdx.files.internal(".");
    for (FileHandle fh : handle.list()) {
      if (fh.extension().equals("tl")) {
        am.load(fh.name(), Level.class);
        levelsNames.add(fh.name());
      }
    }
  }

  public void sortLevelsNames(final AssetManager am) {
    levelsNames.sort(new Comparator<String>() {
      @Override
      public int compare(String s, String ss) {
        Level l = am.get(s);
        Level ll = am.get(ss);
        return l.number - ll.number;
      }
    });
  }

  public ArrayList<String> getLevelsNames() {
    return levelsNames;
  }

  static class LevelParameter extends AssetLoaderParameters<Level> {
  }
}
