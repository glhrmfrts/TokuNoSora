package com.habboi.tns.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

/**
 * Loads a level from a file.
 */
public class LevelLoader {

  public static Level load(String filename) {
    Level level;

    JsonValue root = new JsonReader().parse(Gdx.files.internal(filename));
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
      colors.add(c);
    }

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

    level = new Level(name, music, gravityLevel, fuelFactor, shipPos, colors, presets);
    JsonValue cells = root.get("cells");
    for (JsonValue cell : cells.iterator()) {
      if (cell.has("tile")) {
        JsonValue tile = cell.get("tile");
        Vector3 pos = parseVector3(tile.getString("pos"));
        Vector3 size = parseVector3(tile.getString("size"));
        int preset = tile.getInt("preset");

        Tile.TouchEffect effect = Tile.TouchEffect.None;
        if (tile.has("effect")) {
          effect = Tile.TouchEffect.values()[tile.getInt("effect")];
        }

        Color outlineColor = Color.WHITE;
        if (tile.has("outline")) {
          outlineColor = colors.get(tile.getInt("outline"));
        }
        level.addTile(pos, size, outlineColor, effect, preset);
      }
    }
    return level;
  }

  private static Vector3 parseVector3(String str) {
    String[] parts = str.split(",");
    float x = Float.parseFloat(parts[0].trim());
    float y = Float.parseFloat(parts[1].trim());
    float z = Float.parseFloat(parts[2].trim());

    return new Vector3(x, y, z);
  }
}
