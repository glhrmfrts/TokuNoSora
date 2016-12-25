package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.worlds.World;

import java.util.ArrayList;

public class Arrows implements GenericObject {
    static final float PADDING = 0.30f;
    static final float SPEED = 4;

    float alphaSize;
    ArrayList<Arrow> arrows = new ArrayList<>();
    Color color;
    int depth;
    float halfAlphaSize;
    float halfSize;
    float height;
    Vector3 pos = new Vector3();
    Vector3 rotation = new Vector3();
    float size;

    public Arrows(Vector3 pos, Vector3 rotation, float height, int depth, int colorIndex, World world) {
        this.color = world.colors.get(colorIndex);
        this.height = height;
        this.depth = depth;
        this.alphaSize = depth - 2 + (PADDING * (depth - 2));
        this.halfAlphaSize = alphaSize / 2;
        this.size = depth + (PADDING * depth);
        this.halfSize = size / 2;
        this.pos.set(pos.x, pos.y, -pos.z);
        this.rotation.set(rotation);

        for (int i = 0; i < depth; i++) {
            Arrow arrow = new Arrow(
                                    new ModelInstance(Models.getFloorArrowModel()),
                                    this.color,
                                    new Vector3(pos.x * TileShape.TILE_WIDTH, pos.y * TileShape.TILE_HEIGHT + 1f, -(pos.z + i) * TileShape.TILE_DEPTH - (PADDING * i))
                                    );

            arrow.instance.transform.setToScaling(1, height, 1);
            arrow.instance.transform.rotate(Vector3.X, rotation.x);
            arrow.instance.transform.rotate(Vector3.Y, rotation.y);
            arrow.instance.transform.rotate(Vector3.Z, rotation.z);
            arrows.add(arrow);
        }
    }

    @Override
    public void update(float dt) {
        for (Arrow arrow : arrows) {
            if (Math.abs(rotation.z) == 90) {
                float center = pos.z - halfSize;

                if (arrow.pos.z < center - halfAlphaSize) {
                    float d = Math.abs(arrow.pos.z - (center - halfAlphaSize));
                    arrow.color.a = 1 - d / (halfSize - halfAlphaSize);
                } else if (arrow.pos.z > center + halfAlphaSize) {
                    float d = Math.abs(arrow.pos.z - (center + halfAlphaSize));
                    arrow.color.a = 1 - d / (halfSize - halfAlphaSize);
                } else {
                    arrow.color.a = 1;
                }

                arrow.pos.z -= SPEED * dt;
                if (arrow.pos.z < center - halfSize) {
                    arrow.pos.z = center + halfSize;
                }
            }
        }
    }

    @Override
    public void render(GameRenderer renderer, int pass) {
        if (pass == GameRenderer.RenderPassEffect) {
            for (Arrow arrow : arrows) {
                arrow.instance.transform.setTranslation(arrow.pos);
                renderer.render(arrow.instance, arrow.color);
            }
        }
    }
}
