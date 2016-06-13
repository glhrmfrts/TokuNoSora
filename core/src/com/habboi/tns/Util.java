package com.habboi.tns;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Created by w7 on 09/06/2016.
 */
public class Util {

  public static Random random = new Random();

  public static float randomClamp(float min, float max) {
    return min + random.nextFloat() * max;
  }

  public static float getDensity() {
    if (Gdx.app.getType() == Application.ApplicationType.Android) {
      return Gdx.graphics.getDensity();
    }
    return 1;
  }

  public static BitmapFont generateFont(String file, int size) {
    FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(file));
    FreeTypeFontGenerator.FreeTypeFontParameter params =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
    params.size = (int)(size * Util.getDensity());
    BitmapFont font = gen.generateFont(params);
    gen.dispose();

    return font;
  }

  public static Color alphaFix(Color src) {
    Color cpy = src.cpy();
    cpy.r *= cpy.a;
    cpy.g *= cpy.a;
    cpy.b *= cpy.a;
    return cpy;
  }

  public static void d(Object ...args) {
    for (Object arg : args) {
      System.out.println(arg);
    }
  }

  public static Vector3 calculateNormal(Vector3 p1, Vector3 p2, Vector3 p3) {
    Vector3 v1 = new Vector3(p2).sub(p1);
    Vector3 v2 = new Vector3(p3).sub(p1);
    return v1.crs(v2).nor();
  }
}
