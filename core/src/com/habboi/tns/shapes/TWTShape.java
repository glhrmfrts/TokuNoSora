package com.habboi.tns.shapes;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class TWTShape implements Shape {

    public Vector3 pos = new Vector3();
    public Vector3 size = new Vector3();
    public boolean isShipInside;

    Shape.CollisionInfo collisionInfo;
    ArrayList<Shape> shapes = new ArrayList<>();

    public TWTShape(Vector3 pos, Vector3 size, int[] tunnels) {
        this.pos.set(pos);
        this.size.set(size);

        int width = (int)size.x;
        for (int i = 0; i < width; i++) {
            boolean isTunnel = false;
            for (int j = 0; j < tunnels.length; j++) {
                if (i == tunnels[j]) {
                    isTunnel = true;
                    break;
                }
            }

            if (isTunnel) {
                Vector3 spos = new Vector3(pos.x + (float)i + TunnelShape.TUNNEL_WIDTH * 0.5f, pos.y + TileShape.TILE_HEIGHT, pos.z*TunnelShape.TUNNEL_DEPTH - size.z/2);
                shapes.add(new TunnelShape(spos, size.z));

                Vector3 ssize = new Vector3(TileShape.TILE_WIDTH, size.y - 0.5f - TileShape.TILE_HEIGHT, size.z);
                spos.y = pos.y + 0.5f + ssize.y/2;
                shapes.add(new TileShape(spos, ssize));
            } else {
                Vector3 ssize = new Vector3(TileShape.TILE_WIDTH, size.y - TileShape.TILE_HEIGHT, size.z);
                Vector3 spos = new Vector3(pos.x + (float)i + TileShape.TILE_WIDTH * 0.5f, pos.y + ssize.y/2, pos.z - ssize.z/2);

                shapes.add(new TileShape(spos, ssize));
            }
        }

        Vector3 spos = new Vector3(pos.x + size.x/2, pos.y + size.y - TileShape.TILE_HEIGHT*0.5f, pos.z - size.z/2);
        Vector3 ssize = new Vector3(size.x, TileShape.TILE_HEIGHT, size.z);
        shapes.add(new TileShape(spos, ssize));
    }

    @Override
    public boolean checkCollision(Shape abstractShape) {
        boolean inside = false;
        boolean col = false;

        for (Shape shape : shapes) {
            if (shape.checkCollision(abstractShape)) {
                collisionInfo = shape.getCollisionInfo();
                col = true;
            }

            if (shape instanceof TunnelShape && !inside) {
                inside = ((TunnelShape)shape).isInside;
            }
        }

        isShipInside = inside;
        return col;
    }

    @Override
    public Shape.CollisionInfo getCollisionInfo() {
        return collisionInfo;
    }

    @Override
    public Vector3 getPos() {
        return pos;
    }

    @Override
    public Vector3 getSize() {
        return size;
    }
}
