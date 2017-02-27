package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.rendering.Fragment;

public class Arrow extends Fragment {
    public Color color = new Color();
    public Vector3 pos = new Vector3();

    public Arrow(ModelInstance instance, Color color, Vector3 pos) {
        super(instance);
        glow(true);

        this.color.set(color);
        this.pos.set(pos);

        light = new PointLight().set(color, pos, 2f);
    }
}
