package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;


public class Arrow {
    public ModelInstance instance;
    public Color color = new Color();
    public Vector3 pos = new Vector3();

    public Arrow(ModelInstance instance, Color color, Vector3 pos) {
        this.instance = instance;
        this.color.set(color);
        this.pos.set(pos);
    }
}
