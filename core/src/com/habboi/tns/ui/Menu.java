package com.habboi.tns.ui;

public interface Menu {

    void resume();
    boolean onChange(int delta);
    boolean buttonDown(int buttonCode);
    void render();
    void remove();
}
