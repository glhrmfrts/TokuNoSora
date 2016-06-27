package com.habboi.tns.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

/**
 * Loads a font directly from a .ttf file.
 */
public class FontLoader extends AsynchronousAssetLoader<BitmapFont, FontLoader.FontParameter> {

  public FontLoader(FileHandleResolver resolver) {
    super(resolver);
  }

  @Override
  public void loadAsync(AssetManager manager, String fileName, FileHandle file, FontParameter parameter) {

  }

  @Override
  public BitmapFont loadSync(AssetManager manager, String fileName, FileHandle file, FontParameter parameter) {
    BitmapFont font;
    FreeTypeFontGenerator gen = new FreeTypeFontGenerator(file);
    FreeTypeFontGenerator.FreeTypeFontParameter params =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
    params.size = parameter.size;
    font = gen.generateFont(params);
    gen.dispose();
    return font;
  }

  @Override
  public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, FontParameter parameter) {
    return null;
  }

  public static class FontParameter extends AssetLoaderParameters<BitmapFont> {
    int size;

    public FontParameter(int size) {
      this.size = size;
    }
  }
}
