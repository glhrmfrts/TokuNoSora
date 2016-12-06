package com.habboi.tns.utils;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.audio.Music;

public class MusicWrapper {
    static boolean registered = false;

    Music music;

    public MusicWrapper() {
        this(null);
    }

    public MusicWrapper(Music music) {
        this.music = music;
        if (!registered) {
            registered = true;
            Tween.registerAccessor(MusicWrapper.class, new MusicAccessor());
        }
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music m) {
        music = m;
    }
}
