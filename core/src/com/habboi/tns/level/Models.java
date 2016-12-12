package com.habboi.tns.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Manage all models used in the game.
 */
public final class Models {
    private static int SUN_SEGMENTS = 48;
    private static int TUNNEL_SEGMENTS = 12;
    private static float TUNNEL_THICKNESS = 0.1f;

    private static Model floorArrowModel;
    private static Model sunModel;
    private static Model shipModel;
    private static Model planeModel;
    private static Model shipOutlineModel;
    private static Model tileBoostModel;
    private static Model tileOutlineModel;
    private static Model tunnelOutlineModel;

    private static ModelBuilder mb;
    private static ArrayList<Color> specialColors;

    static {
        mb = new ModelBuilder();

        specialColors = new ArrayList<>();
        specialColors.add(Color.GREEN);
        specialColors.add(Color.BLUE);
        specialColors.add(Color.RED);
        specialColors.add(Color.BLACK);
    }

    public static Model getFloorArrowModel() {
        if (floorArrowModel != null) return floorArrowModel;

        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;

        mb.begin();

        float width = 0.15f;
        float ll = -1;
        float lr = -1 + width;
        float rl = 1 - width;
        float rr = 1;

        Material material = new Material(new BlendingAttribute(0.75f));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));
        material.set(ColorAttribute.createDiffuse(Color.WHITE));

        partBuilder = mb.part("main", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);

        v1 = new VertexInfo().setPos(rl, 0, 0);
        v2 = new VertexInfo().setPos(rr, 0, 0);
        v3 = new VertexInfo().setPos(0, 0, -1);
        v4 = new VertexInfo().setPos(0, 0, -1 + width);
        v1.setNor(0, 0, -1);
        v2.setNor(0, 0, -1);
        v3.setNor(0, 0, -1);
        v4.setNor(0, 0, -1);
        partBuilder.rect(v1, v2, v3, v4);

        v1 = new VertexInfo().setPos(ll, 0, 0);
        v2 = new VertexInfo().setPos(lr, 0, 0);
        v3 = new VertexInfo().setPos(0, 0, -1);
        v4 = new VertexInfo().setPos(0, 0, -1 + width);
        v1.setNor(0, 0, -1);
        v2.setNor(0, 0, -1);
        v3.setNor(0, 0, -1);
        v4.setNor(0, 0, -1);
        partBuilder.rect(v1, v2, v3, v4);

        return floorArrowModel = mb.end();
    }

    public static Model getSunModel() {
        if (sunModel != null) return sunModel;

        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3;

        mb.begin();

        final int segments = SUN_SEGMENTS;
        final float deltaTheta = 2*(float)Math.PI / segments;
        final Material material = new Material(IntAttribute.createCullFace(GL20.GL_NONE));
        //material.set(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.75f));

        float theta = 0;
        float x = 1;
        float y = 0;

        for (int i = 0; i < segments; i++) {
            theta += deltaTheta;
            float nx = (float)Math.cos(theta);
            float ny = (float)Math.sin(theta);

            partBuilder = mb.part("segment" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                  material);

            v1 = new VertexInfo().setPos(0, 0, 0);
            v2 = new VertexInfo().setPos(x, y, 0);
            v3 = new VertexInfo().setPos(nx, ny, 0);

            partBuilder.setColor(Color.RED);
            partBuilder.triangle(v1, v2, v3);

            x = nx;
            y = ny;
        }

        return sunModel = mb.end();
    }

    public static Model getShipModel() {
        if (shipModel != null) return shipModel;

        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;
        Vector3 normal;

        final float front = 0.125f;
        final Material material = new Material(new BlendingAttribute(0.75f));
        material.set(ColorAttribute.createDiffuse(Color.WHITE));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        mb.begin();

        // create bottom part
        partBuilder = mb.part("bottom", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        partBuilder.setColor(Color.WHITE);
        v1 = new VertexInfo().setPos(0.125f, -0.5f, -0.5f);
        v2 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f);
        v3 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f);
        v4 = new VertexInfo().setPos(-0.125f, -0.5f, -0.5f);

        normal = calculateNormal(v1.position, v2.position, v3.position);
        v1.setNor(normal);
        v2.setNor(normal);
        v3.setNor(normal);
        v4.setNor(normal);

        partBuilder.rect(v1, v2, v3, v4);

        // create top part
        partBuilder = mb.part("top", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        partBuilder.setColor(Color.WHITE);
        v1 = new VertexInfo().setPos(-0.125f, front, -0.5f);
        v2 = new VertexInfo().setPos(-0.25f, 0.5f, 0.5f);
        v3 = new VertexInfo().setPos(0.25f, 0.5f, 0.5f);
        v4 = new VertexInfo().setPos(0.125f, front, -0.5f);

        normal = calculateNormal(v1.position, v2.position, v3.position);
        v1.setNor(normal);
        v2.setNor(normal);
        v3.setNor(normal);
        v4.setNor(normal);

        partBuilder.rect(v1, v2, v3, v4);

        // create left part
        partBuilder = mb.part("left", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        partBuilder.setColor(Color.WHITE);
        v1 = new VertexInfo().setPos(-0.125f, -0.5f, -0.5f).setNor(-1, 0, 0);
        v2 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f).setNor(-1, 0, 0);
        v3 = new VertexInfo().setPos(-0.25f, 0.5f, 0.5f).setNor(-1, 0, 0);
        v4 = new VertexInfo().setPos(-0.125f, front, -0.5f).setNor(-1, 0, 0);
        partBuilder.rect(v1, v2, v3, v4);

        // create right part
        partBuilder = mb.part("right", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        partBuilder.setColor(Color.WHITE);
        v1 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f).setNor(1, 0, 0);
        v2 = new VertexInfo().setPos(0.125f, -0.5f, -0.5f).setNor(1, 0, 0);
        v3 = new VertexInfo().setPos(0.125f, front, -0.5f).setNor(1, 0, 0);
        v4 = new VertexInfo().setPos(0.25f, 0.5f, 0.5f).setNor(1, 0, 0);
        partBuilder.rect(v1, v2, v3, v4);

        // create front part
        partBuilder = mb.part("front", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        partBuilder.setColor(Color.WHITE);
        v1 = new VertexInfo().setPos(0.125f, -0.5f, -0.5f).setNor(0, 0, -1);
        v2 = new VertexInfo().setPos(-0.125f, -0.5f, -0.5f).setNor(0, 0, -1);
        v3 = new VertexInfo().setPos(-0.125f, front, -0.5f).setNor(0, 0, -1);
        v4 = new VertexInfo().setPos(0.125f, front, -0.5f).setNor(0, 0, -1);
        partBuilder.rect(v1, v2, v3, v4);

        // create back part (visible to player)
        partBuilder = mb.part("back", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        partBuilder.setColor(Color.WHITE);
        v1 = new VertexInfo().setPos(-0.5f, -0.5f, 0.5f).setNor(0, 0, 1);
        v2 = new VertexInfo().setPos(0.5f, -0.5f, 0.5f).setNor(0, 0, 1);
        v3 = new VertexInfo().setPos(0.25f, 0.5f, 0.5f).setNor(0, 0, 1);
        v4 = new VertexInfo().setPos(-0.25f, 0.5f, 0.5f).setNor(0, 0, 1);
        partBuilder.rect(v1, v2, v3, v4);

        return shipModel = mb.end();
    }

    public static Model getShipOutlineModel() {
        if (shipOutlineModel != null) return shipOutlineModel;

        MeshPartBuilder partBuilder;
        final float front = 0.125f;
        final Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        mb.begin();

        // create bottom part
        partBuilder = mb.part("bottom", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0.125f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f);
        partBuilder.line(0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f);
        partBuilder.line(-0.5f, -0.5f, 0.5f, -0.125f, -0.5f, -0.5f);
        partBuilder.line(-0.125f, -0.5f, -0.5f, 0.125f, -0.5f, -0.5f);

        // create top part
        partBuilder = mb.part("top", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(-0.125f, front, -0.5f, -0.25f, 0.5f, 0.5f);
        partBuilder.line(-0.25f, 0.5f, 0.5f, 0.25f, 0.5f, 0.5f);
        partBuilder.line(0.25f, 0.5f, 0.5f, 0.125f, front, -0.5f);
        partBuilder.line(0.125f, front, -0.5f, -0.125f, front, -0.5f);

        // create left part
        partBuilder = mb.part("left", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(-0.125f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f);
        partBuilder.line(-0.5f, -0.5f, 0.5f, -0.25f, 0.5f, 0.5f);
        partBuilder.line(-0.25f, 0.5f, 0.5f, -0.125f, front, -0.5f);
        partBuilder.line(-0.125f, front, -0.5f, -0.125f, -0.5f, -0.5f);

        // create right part
        partBuilder = mb.part("right", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0.5f, -0.5f, 0.5f, 0.125f, -0.5f, -0.5f);
        partBuilder.line(0.125f, -0.5f, -0.5f, 0.125f, front, -0.5f);
        partBuilder.line(0.125f, front, -0.5f, 0.25f, 0.5f, 0.5f);
        partBuilder.line(0.25f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f);

        // create front part
        partBuilder = mb.part("front", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0.125f, -0.5f, -0.5f, -0.125f, -0.5f, -0.5f);
        partBuilder.line(-0.125f, -0.5f, -0.5f, -0.125f, front, -0.5f);
        partBuilder.line(-0.125f, front, -0.5f, 0.125f, front, -0.5f);
        partBuilder.line(0.125f, front, -0.5f, 0.125f, -0.5f, -0.5f);

        // create back part (visible to player)
        partBuilder = mb.part("back", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f);
        partBuilder.line(0.5f, -0.5f, 0.5f, 0.25f, 0.5f, 0.5f);
        partBuilder.line(0.25f, 0.5f, 0.5f, -0.25f, 0.5f, 0.5f);
        partBuilder.line(-0.25f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f);

        return shipOutlineModel = mb.end();
    }

    public static Model getTileBoostModel() {
        if (tileBoostModel != null) return tileBoostModel;

        tileBoostModel = createTileModel(specialColors, solidTileIndices(0));
        return tileBoostModel;
    }

    public static Model getTileOutlineModel() {
        if (tileOutlineModel != null) return tileOutlineModel;

        MeshPartBuilder partBuilder;
        mb.begin();

        final Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        // create bottom part
        partBuilder = mb.part("bottom", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0, 0, -1, 0, 0, 0);
        partBuilder.line(0, 0, 0, 1, 0, 0);
        partBuilder.line(1, 0, 0, 1, 0, -1);
        partBuilder.line(1, 0, -1, 0, 0, -1);

        // create top part
        partBuilder = mb.part("top", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0, 1, -1, 0, 1, 0);
        partBuilder.line(0, 1, 0, 1, 1, 0);
        partBuilder.line(1, 1, 0, 1, 1, -1);
        partBuilder.line(1, 1, -1, 0, 1, -1);

        // create left part
        partBuilder = mb.part("left", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0, 0, -1, 0, 0, 0);
        partBuilder.line(0, 0, 0, 0, 1, 0);
        partBuilder.line(0, 1, 0, 0, 1, -1);
        partBuilder.line(0, 1, -1, 0, 0, -1);

        // create right part
        partBuilder = mb.part("right", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(1, 0, -1, 1, 0, 0);
        partBuilder.line(1, 0, 0, 1, 1, 0);
        partBuilder.line(1, 1, 0, 1, 1, -1);
        partBuilder.line(1, 1, -1, 1, 0, -1);

        // create front part
        partBuilder = mb.part("front", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(1, 0, -1, 0, 0, -1);
        partBuilder.line(0, 0, -1, 0, 1, -1);
        partBuilder.line(0, 1, -1, 1, 1, -1);
        partBuilder.line(1, 1, -1, 1, 0, -1);

        // create back part (visible to player)
        partBuilder = mb.part("back", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(1, 0, 0, 0, 0, 0);
        partBuilder.line(0, 0, 0, 0, 1, 0);
        partBuilder.line(0, 1, 0, 1, 1, 0);
        partBuilder.line(1, 1, 0, 1, 0, 0);

        return tileOutlineModel = mb.end();
    }

    public static Model getTunnelOutlineModel() {
        if (tunnelOutlineModel != null) return tunnelOutlineModel;

        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;
        mb.begin();

        final Material material = new Material();
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        final int segments = TUNNEL_SEGMENTS;
        final float deltaTheta = (float)Math.PI / segments;
        final float backZ = 1;
        final float frontZ = -1;

        float theta = 0;
        float x = 1;
        float y = -0.5f + (float)Math.sin(deltaTheta*2);
        for (int i = 0; i < segments; i++) {
            theta += deltaTheta;
            float nx = (float)Math.cos(theta);
            float ny = (float)Math.sin(theta);
            float ix = x * (1 - TUNNEL_THICKNESS);
            float iy = y * (1 - TUNNEL_THICKNESS);
            float inx = nx * (1 - TUNNEL_THICKNESS);
            float iny = ny * (1 - TUNNEL_THICKNESS);

            if (i == 0 || i == segments - 1) {
                // create outside part of this segment
                partBuilder = mb.part("outside" + i, GL20.GL_LINES,
                                      VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                      material);
                partBuilder.setColor(Color.WHITE);

                v1 = new VertexInfo().setPos(x, y, backZ);
                v2 = new VertexInfo().setPos(x, y, frontZ);
                v3 = new VertexInfo().setPos(nx, ny, frontZ);
                v4 = new VertexInfo().setPos(nx, ny, backZ);
                adjustTunnelVertexScale(v1, v2, v3, v4);

                if (i == 0) {
                    partBuilder.line(v1, v2);
                } else {
                    partBuilder.line(v3, v4);
                }

                // create inside part of this segment
                partBuilder = mb.part("inside" + i, GL20.GL_LINES,
                                      VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                      material);
                partBuilder.setColor(Color.WHITE);

                v1 = new VertexInfo().setPos(ix, iy, backZ);
                v2 = new VertexInfo().setPos(ix, iy, frontZ);
                v3 = new VertexInfo().setPos(inx, iny, frontZ);
                v4 = new VertexInfo().setPos(inx, iny, backZ);
                adjustTunnelVertexScale(v1, v2, v3, v4);

                if (i == 0) {
                    partBuilder.line(v1, v2);
                } else {
                    partBuilder.line(v3, v4);
                }
            }

            // create back part of this segment (visible to player)
            partBuilder = mb.part("back" + i, GL20.GL_LINES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                  material);
            partBuilder.setColor(Color.WHITE);

            v1 = new VertexInfo().setPos(ix, iy, backZ);
            v2 = new VertexInfo().setPos(inx, iny, backZ);
            v3 = new VertexInfo().setPos(nx, ny, backZ);
            v4 = new VertexInfo().setPos(x, y, backZ);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            partBuilder.line(v1, v2);
            partBuilder.line(v3, v4);

            if (i == 0) {
                partBuilder.line(v4, v1);
            }
            if (i == segments - 1) {
                partBuilder.line(v2, v3);
            }

            // create front part of this segment
            partBuilder = mb.part("front" + i, GL20.GL_LINES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                  material);
            partBuilder.setColor(Color.WHITE);

            v1 = new VertexInfo().setPos(ix, iy, frontZ);
            v2 = new VertexInfo().setPos(inx, iny, frontZ);
            v3 = new VertexInfo().setPos(nx, ny, frontZ);
            v4 = new VertexInfo().setPos(x, y, frontZ);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            partBuilder.line(v1, v2);
            partBuilder.line(v3, v4);

            if (i == 0) {
                partBuilder.line(v4, v1);
            }
            if (i == segments - 1) {
                partBuilder.line(v2, v3);
            }

            x = nx;
            y = ny;
        }

        return tunnelOutlineModel = mb.end();
    }

    public static Model createTileModel(ArrayList<Color> colors, int[][] colorsIndices) {
        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;

        final Material material = new Material(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.75f));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        int[] bottomColors = colorsIndices[0];
        int[] topColors = colorsIndices[1];
        int[] leftColors = colorsIndices[2];
        int[] rightColors = colorsIndices[3];
        int[] frontColors = colorsIndices[4];
        int[] backColors = colorsIndices[5];

        mb.begin();

        // create bottom part
        partBuilder = mb.part("bottom", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(1, 0, -1).setNor(0, -1, 0).setCol(colors.get(bottomColors[0]));
        v2 = new VertexInfo().setPos(1, 0, 0).setNor(0, -1, 0).setCol(colors.get(bottomColors[1]));
        v3 = new VertexInfo().setPos(0, 0, 0).setNor(0, -1, 0).setCol(colors.get(bottomColors[2]));
        v4 = new VertexInfo().setPos(0, 0, -1).setNor(0, -1, 0).setCol(colors.get(bottomColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        // create top part
        partBuilder = mb.part("top", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(0, 1, -1).setNor(0, 1, 0).setCol(colors.get(topColors[0]));
        v2 = new VertexInfo().setPos(0, 1, 0).setNor(0, 1, 0).setCol(colors.get(topColors[1]));
        v3 = new VertexInfo().setPos(1, 1, 0).setNor(0, 1, 0).setCol(colors.get(topColors[2]));
        v4 = new VertexInfo().setPos(1, 1, -1).setNor(0, 1, 0).setCol(colors.get(topColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        // create left part
        partBuilder = mb.part("left", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(0, 0, -1).setNor(-1, 0, 0).setCol(colors.get(leftColors[0]));
        v2 = new VertexInfo().setPos(0, 0, 0).setNor(-1, 0, 0).setCol(colors.get(leftColors[1]));
        v3 = new VertexInfo().setPos(0, 1, 0).setNor(-1, 0, 0).setCol(colors.get(leftColors[2]));
        v4 = new VertexInfo().setPos(0, 1, -1).setNor(-1, 0, 0).setCol(colors.get(leftColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        // create right part
        partBuilder = mb.part("right", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(1, 0, 0).setNor(1, 0, 0).setCol(colors.get(rightColors[0]));
        v2 = new VertexInfo().setPos(1, 0, -1).setNor(1, 0, 0).setCol(colors.get(rightColors[1]));
        v3 = new VertexInfo().setPos(1, 1, -1).setNor(1, 0, 0).setCol(colors.get(rightColors[2]));
        v4 = new VertexInfo().setPos(1, 1, 0).setNor(1, 0, 0).setCol(colors.get(rightColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        // create front part
        partBuilder = mb.part("front", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(1, 0, -1).setNor(0, 0, -1).setCol(colors.get(frontColors[0]));
        v2 = new VertexInfo().setPos(0, 0, -1).setNor(0, 0, -1).setCol(colors.get(frontColors[1]));
        v3 = new VertexInfo().setPos(0, 1, -1).setNor(0, 0, -1).setCol(colors.get(frontColors[2]));
        v4 = new VertexInfo().setPos(1, 1, -1).setNor(0, 0, -1).setCol(colors.get(frontColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        // create back part (visible to player)
        partBuilder = mb.part("back", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(0, 0, 0).setNor(0, 0, 1).setCol(colors.get(backColors[0]));
        v2 = new VertexInfo().setPos(1, 0, 0).setNor(0, 0, 1).setCol(colors.get(backColors[1]));
        v3 = new VertexInfo().setPos(1, 1, 0).setNor(0, 0, 1).setCol(colors.get(backColors[2]));
        v4 = new VertexInfo().setPos(0, 1, 0).setNor(0, 0, 1).setCol(colors.get(backColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        return mb.end();
    }

    public static Model createTunnelModel(ArrayList<Color> colors, int[] colorsIndices) {
        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;
        mb.begin();

        Color backColor = colors.get(colorsIndices[0]);
        Color frontColor = colors.get(colorsIndices[1]);

        final Material material = new Material();
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        final int segments = TUNNEL_SEGMENTS;
        final float deltaTheta = (float)Math.PI / segments;
        final float backZ = 1;
        final float frontZ = -1;

        float theta = 0;
        float x = 1;
        float y = -0.5f + (float)Math.sin(deltaTheta*2);
        for (int i = 0; i < segments; i++) {
            theta += deltaTheta;
            float nx = (float)Math.cos(theta);
            float ny = (float)Math.sin(theta);
            float ix = x * (1 - TUNNEL_THICKNESS);
            float iy = y * (1 - TUNNEL_THICKNESS);
            float inx = nx * (1 - TUNNEL_THICKNESS);
            float iny = ny * (1 - TUNNEL_THICKNESS);

            // create outside part of this segment
            partBuilder = mb.part("outside" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  material);

            v1 = new VertexInfo().setPos(x, y, backZ).setCol(backColor);
            v2 = new VertexInfo().setPos(x, y, frontZ).setCol(frontColor);
            v3 = new VertexInfo().setPos(nx, ny, frontZ).setCol(frontColor);
            v4 = new VertexInfo().setPos(nx, ny, backZ).setCol(backColor);
            v1.setNor(v1.position.x, v1.position.y, 0);
            v2.setNor(v2.position.x, v2.position.y, 0);
            v3.setNor(v3.position.x, v3.position.y, 0);
            v4.setNor(v4.position.x, v4.position.y, 0);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            partBuilder.rect(v1, v2, v3, v4);

            // create back part of this segment (visible to player)
            partBuilder = mb.part("back" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  material);

            v1 = new VertexInfo().setPos(ix, iy, backZ).setCol(backColor);
            v2 = new VertexInfo().setPos(inx, iny, backZ).setCol(backColor);
            v3 = new VertexInfo().setPos(nx, ny, backZ).setCol(backColor);
            v4 = new VertexInfo().setPos(x, y, backZ).setCol(backColor);
            v1.setNor(0, 0, 1);
            v2.setNor(0, 0, 1);
            v3.setNor(0, 0, 1);
            v4.setNor(0, 0, 1);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            partBuilder.rect(v1, v2, v3, v4);

            // create inside part of this segment
            partBuilder = mb.part("inside" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  material);

            v1 = new VertexInfo().setPos(ix, iy, backZ).setCol(backColor);
            v2 = new VertexInfo().setPos(ix, iy, frontZ).setCol(frontColor);
            v3 = new VertexInfo().setPos(inx, iny, frontZ).setCol(frontColor);
            v4 = new VertexInfo().setPos(inx, iny, backZ).setCol(backColor);
            v1.setNor(-v1.position.x, -v1.position.y, 0);
            v2.setNor(-v2.position.x, -v2.position.y, 0);
            v3.setNor(-v3.position.x, -v3.position.y, 0);
            v4.setNor(-v4.position.x, -v4.position.y, 0);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            partBuilder.rect(v1, v2, v3, v4);

            // create front part of this segment
            partBuilder = mb.part("front" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  material);

            v1 = new VertexInfo().setPos(ix, iy, frontZ).setCol(frontColor);
            v2 = new VertexInfo().setPos(inx, iny, frontZ).setCol(frontColor);
            v3 = new VertexInfo().setPos(nx, ny, frontZ).setCol(frontColor);
            v4 = new VertexInfo().setPos(x, y, frontZ).setCol(frontColor);
            v1.setNor(0, 0, -1);
            v2.setNor(0, 0, -1);
            v3.setNor(0, 0, -1);
            v4.setNor(0, 0, -1);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            partBuilder.rect(v1, v2, v3, v4);

            x = nx;
            y = ny;
        }

        return mb.end();
    }

    public static Model createTileWithTunnelsModel(ArrayList<Color> colors, int[][] colorsIndices, Vector3 size, int[] tunnels) {
        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;

        final Material material = new Material(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.75f));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        int[] bottomColors = colorsIndices[0];
        int[] topColors = colorsIndices[1];
        int[] leftColors = colorsIndices[2];
        int[] rightColors = colorsIndices[3];
        int[] frontColors = colorsIndices[4];
        int[] backColors = colorsIndices[5];

        mb.begin();

        int width = (int)size.x;
        for (int i = 0; i < width; i++) {
            boolean isTunnel = false;
            for (int j = 0; j < tunnels.length; j++) {
                if (i == tunnels[j]) {
                    isTunnel = true;
                    break;
                }
            }

            float l = (float)i;
            float r = l + 1;
            if (isTunnel) {
                createTileWithTunnelsTunnel(colors, bottomColors, backColors, frontColors, material, size, l);
            } else {
                partBuilder = mb.part("bottom" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  material);

                v1 = new VertexInfo().setPos(r, 0, -size.z).setNor(0, -1, 0).setCol(colors.get(bottomColors[0]));
                v2 = new VertexInfo().setPos(r, 0, 0).setNor(0, -1, 0).setCol(colors.get(bottomColors[1]));
                v3 = new VertexInfo().setPos(l, 0, 0).setNor(0, -1, 0).setCol(colors.get(bottomColors[2]));
                v4 = new VertexInfo().setPos(l, 0, -size.z).setNor(0, -1, 0).setCol(colors.get(bottomColors[3]));
                partBuilder.rect(v1, v2, v3, v4);

                // create front part
                partBuilder = mb.part("front" + i, GL20.GL_TRIANGLES,
                                      VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                      material);

                v1 = new VertexInfo().setPos(r, 0, -size.z).setNor(0, 0, -1).setCol(colors.get(frontColors[0]));
                v2 = new VertexInfo().setPos(l, 0, -size.z).setNor(0, 0, -1).setCol(colors.get(frontColors[1]));
                v3 = new VertexInfo().setPos(l, size.y, -size.z).setNor(0, 0, -1).setCol(colors.get(frontColors[2]));
                v4 = new VertexInfo().setPos(r, size.y, -size.z).setNor(0, 0, -1).setCol(colors.get(frontColors[3]));
                partBuilder.rect(v1, v2, v3, v4);

                // create back part (visible to player)
                partBuilder = mb.part("back" + i, GL20.GL_TRIANGLES,
                                      VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                      material);

                v1 = new VertexInfo().setPos(l, 0, 0).setNor(0, 0, 1).setCol(colors.get(backColors[0]));
                v2 = new VertexInfo().setPos(r, 0, 0).setNor(0, 0, 1).setCol(colors.get(backColors[1]));
                v3 = new VertexInfo().setPos(r, size.y, 0).setNor(0, 0, 1).setCol(colors.get(backColors[2]));
                v4 = new VertexInfo().setPos(l, size.y, 0).setNor(0, 0, 1).setCol(colors.get(backColors[3]));
                partBuilder.rect(v1, v2, v3, v4);
            }
        }

        // create top part
        partBuilder = mb.part("top", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(0, size.y, -size.z).setNor(0, 1, 0).setCol(colors.get(topColors[0]));
        v2 = new VertexInfo().setPos(0, size.y, 0).setNor(0, 1, 0).setCol(colors.get(topColors[1]));
        v3 = new VertexInfo().setPos(size.x, size.y, 0).setNor(0, 1, 0).setCol(colors.get(topColors[2]));
        v4 = new VertexInfo().setPos(size.x, size.y, -size.z).setNor(0, 1, 0).setCol(colors.get(topColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        // create left part
        partBuilder = mb.part("left", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(0, 0, -size.z).setNor(-1, 0, 0).setCol(colors.get(leftColors[0]));
        v2 = new VertexInfo().setPos(0, 0, 0).setNor(-1, 0, 0).setCol(colors.get(leftColors[1]));
        v3 = new VertexInfo().setPos(0, size.y, 0).setNor(-1, 0, 0).setCol(colors.get(leftColors[2]));
        v4 = new VertexInfo().setPos(0, size.y, -size.z).setNor(-1, 0, 0).setCol(colors.get(leftColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        // create right part
        partBuilder = mb.part("right", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(size.x, 0, 0).setNor(1, 0, 0).setCol(colors.get(rightColors[0]));
        v2 = new VertexInfo().setPos(size.x, 0, -size.z).setNor(1, 0, 0).setCol(colors.get(rightColors[1]));
        v3 = new VertexInfo().setPos(size.x, size.y, -size.z).setNor(1, 0, 0).setCol(colors.get(rightColors[2]));
        v4 = new VertexInfo().setPos(size.x, size.y, 0).setNor(1, 0, 0).setCol(colors.get(rightColors[3]));
        partBuilder.rect(v1, v2, v3, v4);

        return mb.end();
    }

    private static void createTileWithTunnelsTunnel(ArrayList<Color> colors, int[] bottomColor, int[] backColor, int[] frontColor, Material material, Vector3 size, float offset) {
        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;

        Material tunnelMaterial = new Material();
        tunnelMaterial.set(IntAttribute.createCullFace(GL20.GL_NONE));

        final int segments = TUNNEL_SEGMENTS;
        final float deltaTheta = (float)Math.PI / segments;
        final float backZ = 0;
        final float frontZ = -size.z * 2;

        float l = offset;
        float theta = 0;
        float x = 1;
        float y = -0.5f + (float)Math.sin(deltaTheta*2);
        for (int i = 0; i < segments; i++) {
            theta += deltaTheta;
            float nx = (float)Math.cos(theta);
            float ny = (float)Math.sin(theta);
            float ix = x * (1 - TUNNEL_THICKNESS);
            float iy = y * (1 - TUNNEL_THICKNESS);
            float inx = nx * (1 - TUNNEL_THICKNESS);
            float iny = ny * (1 - TUNNEL_THICKNESS);

            // create inside part of this segment
            partBuilder = mb.part("inside_tunnel_" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  tunnelMaterial);

            v1 = new VertexInfo().setPos(ix, iy, backZ).setCol(colors.get(bottomColor[0]));
            v2 = new VertexInfo().setPos(ix, iy, frontZ).setCol(colors.get(bottomColor[1]));
            v3 = new VertexInfo().setPos(inx, iny, frontZ).setCol(colors.get(bottomColor[2]));
            v4 = new VertexInfo().setPos(inx, iny, backZ).setCol(colors.get(bottomColor[3]));
            v1.setNor(-v1.position.x, -v1.position.y, 0);
            v2.setNor(-v2.position.x, -v2.position.y, 0);
            v3.setNor(-v3.position.x, -v3.position.y, 0);
            v4.setNor(-v4.position.x, -v4.position.y, 0);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            adjustTunnelVertexOffset(l, v1, v2, v3, v4);
            partBuilder.rect(v1, v2, v3, v4);

            // create front part of this segment
            partBuilder = mb.part("front_tunnel_" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  material);

            v1 = new VertexInfo().setPos(ix, iy, frontZ).setCol(colors.get(frontColor[0]));
            v2 = new VertexInfo().setPos(inx, iny, frontZ).setCol(colors.get(frontColor[1]));
            v3 = new VertexInfo().setPos(inx, size.y*2, frontZ).setCol(colors.get(frontColor[2]));
            v4 = new VertexInfo().setPos(ix, size.y*2, frontZ).setCol(colors.get(frontColor[3]));
            v1.setNor(0, 0, -1);
            v2.setNor(0, 0, -1);
            v3.setNor(0, 0, -1);
            v4.setNor(0, 0, -1);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            adjustTunnelVertexOffset(l, v1, v2, v3, v4);
            partBuilder.rect(v1, v2, v3, v4);

            // create back part of this segment (visible to player)
            partBuilder = mb.part("back_tunnel_" + i, GL20.GL_TRIANGLES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                                  material);

            v1 = new VertexInfo().setPos(ix, iy, backZ).setCol(colors.get(backColor[0]));
            v2 = new VertexInfo().setPos(ix, size.y*2, backZ).setCol(colors.get(backColor[3]));
            v3 = new VertexInfo().setPos(inx, size.y*2, backZ).setCol(colors.get(backColor[2]));
            v4 = new VertexInfo().setPos(inx, iny, backZ).setCol(colors.get(backColor[1]));
            v1.setNor(0, 0, 1);
            v2.setNor(0, 0, 1);
            v3.setNor(0, 0, 1);
            v4.setNor(0, 0, 1);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            adjustTunnelVertexOffset(l, v1, v2, v3, v4);
            partBuilder.rect(v1, v2, v3, v4);

            x = nx;
            y = ny;
        }

        partBuilder = mb.part("front_tunnel_border_left", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(-1, 0, frontZ).setCol(colors.get(frontColor[0]));
        v2 = new VertexInfo().setPos(-1, size.y*2, frontZ).setCol(colors.get(frontColor[1]));
        v3 = new VertexInfo().setPos(-1 + TUNNEL_THICKNESS, size.y*2, frontZ).setCol(colors.get(frontColor[2]));
        v4 = new VertexInfo().setPos(-1 + TUNNEL_THICKNESS, 0, frontZ).setCol(colors.get(frontColor[3]));
        v1.setNor(0, 0, 1);
        v2.setNor(0, 0, 1);
        v3.setNor(0, 0, 1);
        v4.setNor(0, 0, 1);
        adjustTunnelVertexScale(v1, v2, v3, v4);
        adjustTunnelVertexOffset(l, v1, v2, v3, v4);
        partBuilder.rect(v1, v2, v3, v4);

        partBuilder = mb.part("front_tunnel_border_right", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(1 - TUNNEL_THICKNESS, 0, frontZ).setCol(colors.get(frontColor[0]));
        v2 = new VertexInfo().setPos(1 - TUNNEL_THICKNESS, size.y*2, frontZ).setCol(colors.get(frontColor[1]));
        v3 = new VertexInfo().setPos(1, size.y*2, frontZ).setCol(colors.get(frontColor[2]));
        v4 = new VertexInfo().setPos(1, 0, frontZ).setCol(colors.get(frontColor[3]));

        v1.setNor(0, 0, 1);
        v2.setNor(0, 0, 1);
        v3.setNor(0, 0, 1);
        v4.setNor(0, 0, 1);
        adjustTunnelVertexScale(v1, v2, v3, v4);
        adjustTunnelVertexOffset(l, v1, v2, v3, v4);
        partBuilder.rect(v1, v2, v3, v4);

        partBuilder = mb.part("back_tunnel_border_left", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(-1, 0, backZ).setCol(colors.get(frontColor[0]));
        v2 = new VertexInfo().setPos(-1 + TUNNEL_THICKNESS, 0, backZ).setCol(colors.get(frontColor[3]));
        v3 = new VertexInfo().setPos(-1 + TUNNEL_THICKNESS, size.y*2, backZ).setCol(colors.get(frontColor[2]));
        v4 = new VertexInfo().setPos(-1, size.y*2, backZ).setCol(colors.get(frontColor[1]));
        v1.setNor(0, 0, 1);
        v2.setNor(0, 0, 1);
        v3.setNor(0, 0, 1);
        v4.setNor(0, 0, 1);
        adjustTunnelVertexScale(v1, v2, v3, v4);
        adjustTunnelVertexOffset(l, v1, v2, v3, v4);
        partBuilder.rect(v1, v2, v3, v4);

        partBuilder = mb.part("back_tunnel_border_right", GL20.GL_TRIANGLES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked,
                              material);

        v1 = new VertexInfo().setPos(1 - TUNNEL_THICKNESS, 0, backZ).setCol(colors.get(frontColor[0]));
        v2 = new VertexInfo().setPos(1, 0, backZ).setCol(colors.get(frontColor[3]));
        v3 = new VertexInfo().setPos(1, size.y*2, backZ).setCol(colors.get(frontColor[2]));
        v4 = new VertexInfo().setPos(1 - TUNNEL_THICKNESS, size.y*2, backZ).setCol(colors.get(frontColor[1]));
        v1.setNor(0, 0, 1);
        v2.setNor(0, 0, 1);
        v3.setNor(0, 0, 1);
        v4.setNor(0, 0, 1);
        adjustTunnelVertexScale(v1, v2, v3, v4);
        adjustTunnelVertexOffset(l, v1, v2, v3, v4);
        partBuilder.rect(v1, v2, v3, v4);
    }

    public static Model createTileWithTunnelsOutlineModel(Vector3 size, int[] tunnels) {
        MeshPartBuilder partBuilder;
        mb.begin();

        final Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        int width = (int)size.x;
        for (int i = 0; i < width; i++) {
            boolean isTunnel = false;
            for (int j = 0; j < tunnels.length; j++) {
                if (i == tunnels[j]) {
                    isTunnel = true;
                    break;
                }
            }

            float l = (float)i;
            float r = l + 1;
            if (isTunnel) {
                createTileWithTunnelsTunnelOutline(material, size, l);
            } else {
                // create front part
                partBuilder = mb.part("front", GL20.GL_LINES,
                                      VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                      material);
                partBuilder.setColor(Color.WHITE);
                partBuilder.line(r, 0, -size.z, l, 0, -size.z);

                // create back part (visible to player)
                partBuilder = mb.part("back", GL20.GL_LINES,
                                      VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                      material);
                partBuilder.setColor(Color.WHITE);
                partBuilder.line(r, 0, 0, l, 0, 0);
            }
        }

        // create top part
        partBuilder = mb.part("top", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0, size.y, -size.z,      0, size.y, 0);
        partBuilder.line(0, size.y, 0,            size.x, size.y, 0);
        partBuilder.line(size.x, size.y, 0,       size.x, size.y, -size.z);
        partBuilder.line(size.x, size.y, -size.z, 0, size.y, -size.z);

        // create left part
        partBuilder = mb.part("left", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(0, 0, -size.z,      0, 0, 0);
        partBuilder.line(0, 0, 0,            0, size.y, 0);
        partBuilder.line(0, size.y, 0,       0, size.y, -size.z);
        partBuilder.line(0, size.y, -size.z, 0, 0, -size.z);

        // create right part
        partBuilder = mb.part("right", GL20.GL_LINES,
                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                              material);
        partBuilder.setColor(Color.WHITE);
        partBuilder.line(size.x, 0, -size.z, size.x, 0, 0);
        partBuilder.line(size.x, 0, 0,       size.x, size.y, 0);
        partBuilder.line(size.x, size.y, 0,  size.x, size.y, -size.z);
        partBuilder.line(size.x, size.y, -size.z, size.x, 0, -size.z);

        return mb.end();
    }

    public static void createTileWithTunnelsTunnelOutline(Material material, Vector3 size, float offset) {
        MeshPartBuilder partBuilder;
        VertexInfo v1, v2, v3, v4;

        final int segments = TUNNEL_SEGMENTS;
        final float deltaTheta = (float)Math.PI / segments;
        final float backZ = 0;
        final float frontZ = -size.z * 2;

        float l = offset;
        float theta = 0;
        float x = 1;
        float y = -0.5f + (float)Math.sin(deltaTheta*2);
        for (int i = 0; i < segments; i++) {
            theta += deltaTheta;
            float nx = (float)Math.cos(theta);
            float ny = (float)Math.sin(theta);
            float ix = x * (1 - TUNNEL_THICKNESS);
            float iy = y * (1 - TUNNEL_THICKNESS);
            float inx = nx * (1 - TUNNEL_THICKNESS);
            float iny = ny * (1 - TUNNEL_THICKNESS);

            if (i == 0 || i == segments - 1) {
                // create inside part of this segment
                partBuilder = mb.part("inside" + i, GL20.GL_LINES,
                                      VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                      material);
                partBuilder.setColor(Color.WHITE);

                v1 = new VertexInfo().setPos(ix, iy, backZ);
                v2 = new VertexInfo().setPos(ix, iy, frontZ);
                v3 = new VertexInfo().setPos(inx, iny, frontZ);
                v4 = new VertexInfo().setPos(inx, iny, backZ);
                adjustTunnelVertexScale(v1, v2, v3, v4);
                adjustTunnelVertexOffset(l, v1, v2, v3, v4);

                if (i == 0) {
                    partBuilder.line(v1, v2);
                } else {
                    partBuilder.line(v3, v4);
                }
            }

            // create back part of this segment (visible to player)
            partBuilder = mb.part("back" + i, GL20.GL_LINES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                  material);
            partBuilder.setColor(Color.WHITE);

            v1 = new VertexInfo().setPos(ix, iy, backZ);
            v2 = new VertexInfo().setPos(inx, iny, backZ);
            v3 = new VertexInfo().setPos(nx, ny, backZ);
            v4 = new VertexInfo().setPos(x, y, backZ);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            adjustTunnelVertexOffset(l, v1, v2, v3, v4);
            partBuilder.line(v1, v2);
            partBuilder.line(v3, v4);

            if (i == 0) {
                partBuilder.line(v4, v1);
            }
            if (i == segments - 1) {
                partBuilder.line(v2, v3);
            }

            // create front part of this segment
            partBuilder = mb.part("front" + i, GL20.GL_LINES,
                                  VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                  material);
            partBuilder.setColor(Color.WHITE);

            v1 = new VertexInfo().setPos(ix, iy, frontZ);
            v2 = new VertexInfo().setPos(inx, iny, frontZ);
            v3 = new VertexInfo().setPos(nx, ny, frontZ);
            v4 = new VertexInfo().setPos(x, y, frontZ);
            adjustTunnelVertexScale(v1, v2, v3, v4);
            adjustTunnelVertexOffset(l, v1, v2, v3, v4);
            partBuilder.line(v1, v2);
            partBuilder.line(v3, v4);

            if (i == 0) {
                partBuilder.line(v4, v1);
            }
            if (i == segments - 1) {
                partBuilder.line(v2, v3);
            }

            x = nx;
            y = ny;
        }
    }

    public static Model createLineModel(Color color, int[] vs) {
        mb.begin();

        final Material material = new Material(ColorAttribute.createDiffuse(color));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        MeshPartBuilder partBuilder = mb.part("line", GL20.GL_LINES,
                                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                              material);

        partBuilder.setColor(Color.WHITE);
        partBuilder.line(vs[0], vs[1], vs[2], vs[3], vs[4], vs[5]);

        return mb.end();
    }

    public static Model createLineRectModel(Color color) {
        mb.begin();
        final Material material = new Material(ColorAttribute.createDiffuse(color));
        MeshPartBuilder partBuilder = mb.part("rect", GL20.GL_LINES,
                                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                              material);

        partBuilder.setColor(Color.WHITE);
        partBuilder.line(-1, -1, 0, 1, -1, 0);
        partBuilder.line(1, -1, 0, 1, 1, 0);
        partBuilder.line(1, 1, 0, -1, 1, 0);
        partBuilder.line(-1, 1, 0, -1, -1, 0);

        return mb.end();
    }

    public static Model createPlaneModel(Color color) {
        if (planeModel != null) return planeModel;

        VertexInfo v1, v2, v3, v4;
        mb.begin();

        color = color.cpy();
        color.a = 0.75f;
        final Material material = new Material(ColorAttribute.createDiffuse(color));
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));
        material.set(new BlendingAttribute(1));

        MeshPartBuilder partBuilder = mb.part("line", GL20.GL_TRIANGLES,
                                              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
                                              material);

        v1 = new VertexInfo().setPos(-1, 0, -1);
        v2 = new VertexInfo().setPos(-1, 0, 1);
        v3 = new VertexInfo().setPos(1, 0, 1);
        v4 = new VertexInfo().setPos(1, 0, -1);

        partBuilder.setColor(Color.WHITE);
        partBuilder.rect(v1, v2, v3, v4);

        return planeModel = mb.end();
    }

    public static void dispose() {
        disposeModel(sunModel);
        disposeModel(shipModel);
        disposeModel(shipOutlineModel);
        disposeModel(tileOutlineModel);
        disposeModel(tunnelOutlineModel);
    }

    private static void disposeModel(Model model) {
        // The model might not have been created
        if (model != null) model.dispose();
    }

    private static void adjustTunnelVertexOffset(float offset, VertexInfo... vs) {
        for (VertexInfo v : vs) {
            v.position.x += offset + 0.5f;
            v.position.y += 0.25f;
        }
    }

    private static void adjustTunnelVertexScale(VertexInfo v1) {
        v1.position.x *= 0.5f;
        v1.position.y -= 0.5f;
        v1.position.y *= 0.5f;
        v1.position.z *= 0.5f;
    }

    private static void adjustTunnelVertexScale(VertexInfo... vs) {
        for (VertexInfo v : vs) {
            adjustTunnelVertexScale(v);
        }
    }

    public static Vector3 calculateNormal(Vector3 p1, Vector3 p2, Vector3 p3) {
        Vector3 v1 = new Vector3(p2).sub(p1);
        Vector3 v2 = new Vector3(p3).sub(p1);
        return v1.crs(v2).nor();
    }

    public static int[][] solidTileIndices(int c) {
        return new int[][]{
            new int[]{c, c, c, c}, new int[]{c, c, c, c}, new int[]{c, c, c, c},
            new int[]{c, c, c, c}, new int[]{c, c, c, c}, new int[]{c, c, c, c}
        };
    }
}
