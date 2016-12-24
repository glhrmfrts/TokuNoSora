package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.shapes.TileShape;
import com.habboi.tns.worlds.World;

import java.util.ArrayList;

public class Arrows implements GenericObject {

    Color color;
    ArrayList<Arrow> arrows = new ArrayList<>();
    float height;

    public Arrows(Vector3 pos, Vector3 rotation, float height, int depth, int colorIndex, World world) {
        this.color = world.colors.get(colorIndex);
        this.height = height;
        final float padding = 0.25f;

        for (int i = 0; i < depth; i++) {
            Arrow arrow = new Arrow(
                                    new ModelInstance(Models.getFloorArrowModel()),
                                    this.color,
                                    new Vector3(pos.x * TileShape.TILE_WIDTH, pos.y * TileShape.TILE_HEIGHT + 1f, -(pos.z + i) * TileShape.TILE_DEPTH - (padding + i))
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
    }

    @Override
    public void render(GameRenderer renderer) {
        for (Arrow arrow : arrows) {
            arrow.instance.transform.setTranslation(arrow.pos);
            renderer.renderGlow(arrow.instance, arrow.color);
        }
    }
}
