package com.habboi.tns.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.audio.Music;

public class MusicAccessor implements TweenAccessor<MusicWrapper> {

    public static final int TWEEN_VOLUME = 0;

    @Override
    public int getValues(MusicWrapper wrapper, int type, float[] values) {
        if (type == TWEEN_VOLUME) {
            values[0] = wrapper.getMusic().getVolume();
            return 1;
        }
        return 0;
    }

    @Override
    public void setValues(MusicWrapper wrapper, int type, float[] values) {
        if (type == TWEEN_VOLUME) {
            wrapper.getMusic().setVolume(values[0]);
        }
    }
}
