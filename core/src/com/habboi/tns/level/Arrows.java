package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.Scene;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.utils.Models;
import com.habboi.tns.worlds.World;

import java.util.ArrayList;

public class Arrows extends LevelObject {
    static final float PADDING = 0.25f;
    static final float SPEED = 4;

    float alphaSize;
    ArrayList<Arrow> arrows = new ArrayList<>();
    float baseX;
    float baseY;
    float baseZ;
    Color color;
    int depth;
    float halfAlphaSize;
    float halfSize;
    float height;
    Vector3 pos = new Vector3();
    Vector3 rotation = new Vector3();
    Vector3 movement = new Vector3();
    float size;

    public Arrows(Vector3 pos, Vector3 rotation, Vector3 movement, float height, float pad, int depth, int colorIndex, World world) {
        final float padding = PADDING * pad;
        this.color = new Color(world.colors.get(colorIndex));
        this.height = height;
        this.depth = depth;
        this.alphaSize = depth - 3 + (padding * (depth - 3));
        this.halfAlphaSize = alphaSize / 2;
        this.size = depth + (padding * depth);
        this.halfSize = size / 2;
        this.pos.set(pos.x, pos.y, -pos.z);
        this.rotation.set(rotation);
        this.movement.set(movement);
        this.baseX = pos.x;
        this.baseY = pos.y;
        this.baseZ = -pos.z;
        
        if (movement.x == -1) {
            this.baseX = pos.x + depth + (padding * depth) + 0.5f;
        } else if (movement.x == 1) {
            this.baseX -= 0.5f + padding * depth;
        }

        for (int i = 0; i < depth; i++) {
            Vector3 position = new Vector3(
                                           baseX * TileShape.TILE_WIDTH,
                                           baseY * TileShape.TILE_HEIGHT + 1f,
                                           baseZ * TileShape.TILE_DEPTH
                                           );
            if (movement.x != 0) {
                position.x = (baseX + (i * movement.x) * TileShape.TILE_WIDTH + (padding * i * movement.x));
            } else if (movement.z != 0) {
                position.z = (baseZ - (i * movement.z) * TileShape.TILE_DEPTH - (padding * i * movement.z));
            }

            Arrow arrow = new Arrow(
                                    new ModelInstance(Models.getFloorArrowModel()),
                                    this.color,
                                    position
                                    );

            arrow.modelInstance.transform.setToScaling(1, height, 1);
            arrow.modelInstance.transform.rotate(Vector3.X, rotation.x);
            arrow.modelInstance.transform.rotate(Vector3.Y, rotation.y);
            arrow.modelInstance.transform.rotate(Vector3.Z, rotation.z);

            Models.setColor(arrow.modelInstance, ColorAttribute.Diffuse, color);
            arrows.add(arrow);
        }
    }

    public ArrayList<Arrow> getArrows() {
        return arrows;
    }

    @Override
    public void addToScene(Scene scene) {
      for (Arrow a : arrows) {
        scene.add(a);
      }
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(float dt) {
        for (Arrow arrow : arrows) {
            if (movement.x != 0) {
                float center = baseX + halfSize * movement.x;

                if (arrow.pos.x < center - halfAlphaSize) {
                    float d = Math.abs(arrow.pos.x - (center - halfAlphaSize));
                    arrow.color.a = 1 - Math.min(1, d / (halfSize - halfAlphaSize));
                } else if (arrow.pos.x > center + halfAlphaSize) {
                    float d = Math.abs(arrow.pos.x - (center + halfAlphaSize));
                    arrow.color.a = 1 - Math.min(1, d / (halfSize - halfAlphaSize));
                } else {
                    arrow.color.a = 1;
                }

                arrow.pos.x += SPEED * movement.x * dt;
                if (movement.x == -1 && arrow.pos.x < center - halfSize) {
                    arrow.pos.x = center + halfSize;
                }
                if (movement.x == 1 && arrow.pos.x > center + halfSize) {
                    arrow.pos.x = center - halfSize;
                }
            } else if (movement.z != 0) {
                float center = baseZ - halfSize;

                if (arrow.pos.z < center - halfAlphaSize) {
                    float d = Math.abs(arrow.pos.z - (center - halfAlphaSize));
                    arrow.color.a = 1 - Math.min(1, d / (halfSize - halfAlphaSize));
                } else if (arrow.pos.z > center + halfAlphaSize) {
                    float d = Math.abs(arrow.pos.z - (center + halfAlphaSize));
                    arrow.color.a = 1 - Math.min(1, d / (halfSize - halfAlphaSize));
                } else {
                    arrow.color.a = 1;
                }

                arrow.pos.z -= SPEED * dt;
                if (arrow.pos.z < center - halfSize) {
                    arrow.pos.z = center + halfSize;
                }
            }

            arrow.modelInstance.transform.setTranslation(arrow.pos);
            Models.setColor(arrow.modelInstance, ColorAttribute.Diffuse, arrow.color);
        }
    }
}
