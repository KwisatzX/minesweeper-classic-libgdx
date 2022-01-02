package io.github.kwisatzx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.kwisatzx.Options;
import io.github.kwisatzx.Renderer;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Minesweeper";
		config.width = Options.WIDTH;
		config.height = Options.HEIGHT;
		new LwjglApplication(new Renderer(), config);
	}
}
