package com.habboi.tns.ui;

public interface Menu {

    void resume();
    boolean onChange(int delta);
    boolean keyDown(int keycode);
    void render();
    void remove();
}
