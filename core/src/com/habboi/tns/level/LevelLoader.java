package com.habboi.tns.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Loads a level from a file (synchronously because of OpenGL =/).
 */
public class LevelLoader extends SynchronousAssetLoader<Level, LevelLoader.LevelParameter> {
    public LevelLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Level load(AssetManager assetManager, String fileName, FileHandle file, LevelParameter parameter) {
        Level level = new Level();
        LevelParser.parse(new BufferedReader(file.reader()), level);
        return level;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, LevelParameter parameter) {
        return null;
    }

    public void preloadAllLevels(AssetManager am) {
        for (String s : getLevelsNames(am)) {
          am.load(s, Level.class);
        }
    }

    public ArrayList<String> getLevelsNames(AssetManager am) {
      ArrayList<String> levelsNames = new ArrayList<>();
      FileHandle handle = Gdx.files.internal("levels");
      for (FileHandle fh : handle.list()) {
        levelsNames.add("levels/" + fh.name());
      }
      return levelsNames;
    }

    static class LevelParameter extends AssetLoaderParameters<Level> {
    }
}
