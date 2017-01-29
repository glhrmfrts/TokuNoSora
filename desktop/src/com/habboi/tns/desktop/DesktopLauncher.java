package com.habboi.tns.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.habboi.tns.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 768;
		config.resizable = false;
		config.fullscreen = (1 == 0);

		new LwjglApplication(new Game(), config);
	}
}
