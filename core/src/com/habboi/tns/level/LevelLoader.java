package com.habboi.tns.level;

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

/**
 * Loads a level from a file (synchronously because of OpenGL =/).
 */
public class LevelLoader extends SynchronousAssetLoader<Level, LevelLoader.LevelParameter> {

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
    String music = root.getString("music");
    int gravityLevel = root.getInt("gravity_level");
    int fuelFactor = root.getInt("fuel_factor");
    Vector3 shipPos = parseVector3(root.getString("ship_pos"));

    JsonValue jsonColors = root.get("colors");
    if (!jsonColors.isArray()) {
      throw new GdxRuntimeException("No colors or isn't array");
    }
    ArrayList<Color> colors = new ArrayList<>();
    for (String str : jsonColors.asStringArray()) {
      Color c = new Color(Integer.decode(str) << 8);
      c.a = 1;
      colors.add(c);
    }

    // read tile presets
    JsonValue jsonPresets = root.get("presets");
    if (!jsonPresets.isArray()) {
      throw new GdxRuntimeException("No presets or isn't array");
    }
    ArrayList<ArrayList<int[]>> presets = new ArrayList<>();
    for (JsonValue jsonPreset : jsonPresets.iterator()) {
      ArrayList<int[]> preset = new ArrayList<>();
      presets.add(preset);
      for (JsonValue jsonFace : jsonPreset.iterator()) {
        preset.add(jsonFace.asIntArray());
      }
    }

    // read tunnel presets
    jsonPresets = root.get("tunnel_presets");
    if (!jsonPresets.isArray()) {
      throw new GdxRuntimeException("No tunnel presets or isn't array");
    }
    ArrayList<int[]> tunnelPresets = new ArrayList<>();
    for (JsonValue jsonPreset : jsonPresets.iterator()) {
      int[] preset = jsonPreset.asIntArray();
      tunnelPresets.add(preset);
    }

    level = new Level(name, music, gravityLevel, fuelFactor, shipPos, colors, presets, tunnelPresets);
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

  static class LevelParameter extends AssetLoaderParameters<Level> {
  }
}
