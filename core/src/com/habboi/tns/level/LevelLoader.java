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

    private Vector3 parseVector3(String str) {
        String[] parts = str.split(",");
        float x = Float.parseFloat(parts[0].trim());
        float y = Float.parseFloat(parts[1].trim());
        float z = Float.parseFloat(parts[2].trim());

        return new Vector3(x, y, z);
    }

    private Cell.TouchEffect touchEffectFromName(String name) {
        switch (name) {
        case "boost":
            return Cell.TouchEffect.BOOST;
        default:
            return Cell.TouchEffect.NONE;
        }
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
        float centerX = 0;

        try {
            centerX = root.getFloat("center_x");
        } catch (Exception e) {}

        Vector3 shipPos = parseVector3(root.getString("ship_pos"));
        level = new Level(name, number, worldIndex, centerX, shipPos);
        JsonValue cells = root.get("cells");
        for (JsonValue cell : cells.iterator()) {
            if (cell.has("tile")) {
                JsonValue tile = cell.get("tile");
                Vector3 pos = parseVector3(tile.getString("pos"));
                Vector3 size = parseVector3(tile.getString("size"));
                int preset = tile.getInt("preset");
                Cell.TouchEffect effect = touchEffectFromName(tile.getString("effect", ""));

                level.addTile(pos, size, preset, effect);
            } else if (cell.has("finish")) {
                JsonValue finish = cell.get("finish");
                Vector3 pos = parseVector3(finish.getString("pos"));
                float radius = finish.getFloat("radius");

                level.addFinish(pos, radius);
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
            } else if (cell.has("twt")) {
                JsonValue twt = cell.get("twt");
                Vector3 pos = parseVector3(twt.getString("pos"));
                Vector3 size = parseVector3(twt.getString("size"));
                int preset = twt.getInt("preset");
                int[] tunnels = twt.get("tunnels").asIntArray();
                Cell.TouchEffect effect = touchEffectFromName(twt.getString("effect", ""));

                level.addTileWithTunnels(pos, size, preset, tunnels, effect);
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
            if (fh.extension().equals("json")) {
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
