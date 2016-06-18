package com.habboi.tns.level;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Ship;
import com.habboi.tns.rendering.GameRenderer;

/**
 * Represents a tunnel instance on the level.
 */
public class Tunnel extends Cell {

  float depth;
  Vector3 center = new Vector3();

  public Tunnel(Vector3 pos, float depth, Model model) {
    this.depth = depth;

    center.set(Tile.TILE_WIDTH/2, 0.5f*Tile.TILE_HEIGHT, (depth*Tile.TILE_DEPTH)/2);
    float x = pos.x*Tile.TILE_WIDTH;
    float y = pos.y*Tile.TILE_HEIGHT + center.y;
    float z = -(pos.z*Tile.TILE_DEPTH + center.z);
    this.pos.set(x, y, z);

    modelInstance = new ModelInstance(model);
    modelInstance.transform.setToScaling(1, 1, depth*Tile.TILE_DEPTH);
  }

  @Override
  public void render(GameRenderer renderer) {
    modelInstance.transform.setTranslation(pos.x, pos.y + center.y*2, pos.z);
    renderer.render(modelInstance);
  }

  @Override
  public boolean checkCollision(Ship ship) {
    return false;
  }
}
