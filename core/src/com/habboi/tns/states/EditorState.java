package com.habboi.tns.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.rendering.GameRenderer;
import com.habboi.tns.states.GameState;
import com.habboi.tns.worlds.World;


public class EditorState extends GameState {
    static final float VEL = 10;
    static final float CAM_DIST = 20;
    static final float CAM_Y = 20;
    static final float CAM_LOOKAT_Y = 6;

    Vector3 bgPos = new Vector3();
    Level level;
    OrthographicCamera orthoCam;
    World world;
    PerspectiveCamera worldCam;

    public EditorState(Game g, World world) {
        super(g);
        this.world = world;
    }

    @Override
    public void create() {
        orthoCam = new OrthographicCamera();
        orthoCam.setToOrtho(false, game.getWidth(), game.getHeight());

        level = new Level();
        level.setWorld(world);

        worldCam = game.getRenderer().getWorldCam();
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            game.popState();
            return true;
        }

        return false;
    }

    @Override
    public void update(float dt) {
        worldCam.position.x = 0;
        worldCam.position.y = CAM_Y;
        worldCam.position.z -= VEL * dt;
        worldCam.lookAt(0, CAM_LOOKAT_Y, worldCam.position.z - CAM_DIST);

        bgPos.set(worldCam.position);
        bgPos.y -= CAM_LOOKAT_Y;

        world.setCenterX(0);
        world.update(bgPos, VEL, dt);
    }

    @Override
    public void render() {
        worldCam.update();

        GameRenderer gr = game.getRenderer();
        gr.begin();
        gr.renderLevel(level);
        gr.end();
    }

    @Override
    public void dispose() {
    }
}
