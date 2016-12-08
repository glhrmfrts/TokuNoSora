package com.habboi.tns.worlds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.habboi.tns.level.Models;
import com.habboi.tns.rendering.GameRenderer;

import java.util.ArrayList;

/**
 * Created by w7 on 03/07/2016.
 */
public abstract class World implements Disposable {
    public float gravityFactor;
    public float oxygenFactor;
    public String name;
    public String music;
    public ArrayList<Color> colors = new ArrayList<>();

    ArrayList<Model> tileModels = new ArrayList<>();
    ArrayList<Model> tunnelModels = new ArrayList<>();

    public World(float gravityFactor, float oxygenFactor, String name, String music) {
        this.gravityFactor = gravityFactor;
        this.oxygenFactor = oxygenFactor;
        this.name = name;
        this.music = music;
    }

    public void addColor(int hex) {
        colors.add(new Color(hex<<8 | 0xff));
    }

    public void addTileModel(int[][] model) {
        tileModels.add(Models.createTileModel(colors, model));
    }

    public Model getTileModel(int i) {
        return tileModels.get(i);
    }

    public void addTunnelModel(int[] model) {
        tunnelModels.add(Models.createTunnelModel(colors, model));
    }

    public Model getTunnelModel(int i) {
        return tunnelModels.get(i);
    }

    public abstract void reset();
    public abstract void update(Vector3 shipPos, float vel, float dt);
    public abstract void render(GameRenderer renderer);
    public abstract void setCenterX(float centerX);
}
