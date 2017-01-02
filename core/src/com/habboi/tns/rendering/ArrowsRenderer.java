package com.habboi.tns.rendering;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.habboi.tns.level.Arrow;
import com.habboi.tns.level.Arrows;

public class ArrowsRenderer implements LevelObjectRenderer<Arrows> {

  private static ArrowsRenderer instance;

  public static ArrowsRenderer getInstance() {
    if (instance == null) {
      instance = new ArrowsRenderer();
    }
    return instance;
  }

  private ArrowsRenderer() {
  }

  @Override
  public void render(Arrows arrows, ModelBatch batch, Environment environment) {
    for (Arrow arrow : arrows.getArrows()) {
      batch.render(arrow.instance, environment);
    }
  }
}
