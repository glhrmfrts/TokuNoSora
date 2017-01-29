package com.habboi.tns.states;

import com.badlogic.gdx.assets.AssetManager;
import com.habboi.tns.Game;
import com.habboi.tns.level.Level;

public class LoadingLevelState extends LoadingState {
  String levelName;

  public LoadingLevelState(Game g, String levelName) {
    super(g);
    this.levelName = levelName;
  }

  @Override
  public void loadItems() {
    AssetManager am = game.getAssetManager();
    am.unload(levelName);
    am.load(levelName, Level.class);
  }

  @Override
  public void complete() {
    game.setState(new InGameState(game, levelName));
  }
}
