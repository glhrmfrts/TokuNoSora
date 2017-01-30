package com.habboi.tns.level;

import com.badlogic.gdx.math.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LevelParser {
  private static HashMap<String, String> tempFields = new HashMap<>();

  public static void parse(BufferedReader reader, Level level) {
    parseFields(reader, tempFields);

    level.name = tempFields.get("name");
    level.number = Integer.valueOf(tempFields.get("number"));
    level.worldIndex = Integer.valueOf(tempFields.get("world_index"));
    level.shipPos = parseVector3(tempFields.get("ship_pos"));
    level.centerX = Integer.valueOf(tempFields.getOrDefault("center_x", "0"));
    level.oxygenFactor = Float.valueOf(tempFields.get("oxygen_factor"));

    parseObjects(reader, level);
  }

  public static void parseFields(BufferedReader reader, Map<String, String> fields) {
    String line;

    try {
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) return;

        if (line.charAt(0) == '#') continue;

        String[] spl = line.split("=");
        if (spl.length != 2) return;

        fields.put(spl[0].trim(), spl[1].trim());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void parseObject(BufferedReader reader, Level level, String objectName) {
    parseFields(reader, tempFields);

    if (objectName.equals("collectible")) {
      Vector3 pos = parseVector3(tempFields.get("pos"));
      level.addCollectible(pos);
    }

    if (objectName.equals("tile")) {
      Vector3 pos = parseVector3(tempFields.get("pos"));
      Vector3 size = parseVector3(tempFields.get("size"));
      int preset = Integer.valueOf(tempFields.get("preset"));
      TouchEffect effect = TouchEffect.fromName(tempFields.getOrDefault("effect", ""));

      level.addTile(pos, size, preset, effect);
    }

    if (objectName.equals("finish")) {
      Vector3 pos = parseVector3(tempFields.get("pos"));
      float radius = Float.valueOf(tempFields.get("radius"));

      level.addFinish(pos, radius);
    }

    if (objectName.equals("tunnel")) {
      Vector3 pos = parseVector3(tempFields.get("pos"));
      int depth = Integer.valueOf(tempFields.get("depth"));
      int preset = Integer.valueOf(tempFields.get("preset"));

      level.addTunnel(pos, depth, preset, tempFields.containsKey("end"));

      tempFields.remove("end");
    }

    if (objectName.equals("twt")) {
      Vector3 pos = parseVector3(tempFields.get("pos"));
      Vector3 size = parseVector3(tempFields.get("size"));
      int preset = Integer.valueOf(tempFields.get("preset"));
      int[] tunnels = parseIntArray(tempFields.get("tunnels"));
      TouchEffect effect = TouchEffect.fromName(tempFields.getOrDefault("effect", ""));

      level.addTileWithTunnels(pos, size, preset, tunnels, effect);
    }

    if (objectName.equals("arrows")) {
      Vector3 pos = parseVector3(tempFields.get("pos"));
      Vector3 rotation = parseVector3(tempFields.get("rotation"));
      Vector3 movement = parseVector3(tempFields.get("movement"));
      float height = Integer.valueOf(tempFields.getOrDefault("height", "1"));
      float pad = Float.valueOf(tempFields.getOrDefault("pad", "1"));
      int depth = Integer.valueOf(tempFields.get("depth"));
      int color = Integer.valueOf(tempFields.get("color"));

      level.addArrows(pos, rotation, movement, height, pad, depth, color);
    }
  }

  public static void parseObjects(BufferedReader reader, Level level) {
    String line;

    try {
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) continue;

        if (line.charAt(0) == '#') continue;
        if (line.charAt(0) != ':') return;

        parseObject(reader, level, line.substring(1));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int[] parseIntArray(String str) {
    String[] spl = str.split(",");
    int[] arr = new int[spl.length];
    for (int i = 0; i < spl.length; i++) {
      arr[i] = Integer.valueOf(spl[i]);
    }
    return arr;
  }

  private static Vector3 parseVector3(String str) {
    String[] parts = str.split(",");
    float x = Float.parseFloat(parts[0].trim());
    float y = Float.parseFloat(parts[1].trim());
    float z = Float.parseFloat(parts[2].trim());

    return new Vector3(x, y, z);
  }
}