package com.habboi.tns.utils;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

/**
 * Handles imaginary font file names with the desired size as it's extension.
 */
public class FontFileHandleResolver extends InternalFileHandleResolver {

  @Override
  public FileHandle resolve(String filename) {
    filename = filename.substring(0, filename.lastIndexOf("."));
    return super.resolve(filename);
  }
}
