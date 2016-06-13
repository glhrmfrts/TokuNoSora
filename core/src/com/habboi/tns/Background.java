package com.habboi.tns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import java.util.ArrayList;

/**
 * Created by w7 on 10/06/2016.
 */
public class Background {
  static Model lineModel;
  ArrayList<ModelInstance> instances;

  private static Model createLineModel() {
    if (lineModel != null) return lineModel;

    ModelBuilder mb = new ModelBuilder();
    mb.begin();

    MeshPartBuilder partBuilder = mb.part("line", GL20.GL_LINE_STRIP,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            new Material());

    partBuilder.setColor(Color.RED);
    partBuilder.line(-50, -5, 0, 50, -5, 0);

    return lineModel = mb.end();
  }

  public Background() {

  }
}
