package com.habboi.tns.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Model;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;
import com.habboi.tns.level.LevelLoader;
import com.habboi.tns.utils.FontManager;
import com.habboi.tns.worlds.Universe;
import com.habboi.tns.worlds.World;

public class LoadingAllState extends LoadingState {

  public LoadingAllState(Game g) {
    super(g);
  }

  @Override
  public void loadItems() {
    Universe.get().createWorlds();
    AssetManager am = game.getAssetManager();
    FontManager fm = FontManager.get();
    am.load("audio/bounce.wav", Sound.class);
    am.load("audio/explosion.wav", Sound.class);
    am.load("audio/select.wav", Sound.class);
    am.load("audio/intro.ogg", Music.class);
    am.load("audio/menu.ogg", Music.class);
    am.load("audio/world1.ogg", Music.class);
    am.load("models/ship.obj", Model.class);

    fm.loadFont(Game.MAIN_FONT, Game.MAIN_FONT_SIZE);
    fm.loadFont(Game.MAIN_FONT, Game.HUGE_FONT_SIZE);
    fm.loadFont(Game.MAIN_FONT, Game.BIG_FONT_SIZE);
    fm.loadFont(Game.MAIN_FONT, Game.MEDIUM_FONT_SIZE);
    fm.loadFont(Game.MAIN_FONT, Game.SMALL_FONT_SIZE);
    ((LevelLoader) am.getLoader(Level.class)).preloadAllLevels(am);
    for (World world : Universe.get().worlds) {
      //am.load("audio/" + world.music, Music.class);
    }
  }

  @Override
  public void complete() {
    game.setState(new IntroState(game));
  }
}
