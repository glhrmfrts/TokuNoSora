package com.habboi.tns.states;

import com.habboi.tns.Game;

/**
 * Created by w7 on 07/06/2016.
 */
public abstract class GameState {
  Game game;

  public GameState(Game g) {
    game = g;
  }

  public Game getGame() {
    return game;
  }

  public abstract void create();
  public abstract void update(float dt);
  public abstract void render();
  public abstract void dispose();
}
