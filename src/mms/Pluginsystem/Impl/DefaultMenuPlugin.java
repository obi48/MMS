package mms.Pluginsystem.Impl;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import mms.Pluginsystem.MenuPlugin;

public class DefaultMenuPlugin extends MenuPlugin {
	private final MenuBar menuBar;

	public DefaultMenuPlugin(PluginHost h) {
		super(h);
		menuBar = new MenuBar();
	}

	@Override
	public String getDeveloper() {
		return "MMS";
	}

	@Override
	public String getName() {
		return "Standard Menu";
	}

	@Override
	public String getDescription() {
		return "Provides a standard menu bar with default look.";
	}

	@Override
	public void start() {
		AnchorPane root = host.getRootPane();
		root.getChildren().add(menuBar);
		AnchorPane.setLeftAnchor(menuBar, 0.0);
		AnchorPane.setRightAnchor(menuBar, 0.0);
		AnchorPane.setTopAnchor(menuBar, 0.0);
	}

	@Override
	public boolean stop() {
		return false;
	}

	@Override
	public void addMenu(Menu m) {
		menuBar.getMenus().add(m);
	}

	@Override
	public void addFirstMenu(Menu m) {
		menuBar.getMenus().add(0, m);
	}
}
