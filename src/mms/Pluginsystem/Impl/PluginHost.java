package mms.pluginsystem.impl;

import javafx.scene.layout.AnchorPane;
import mms.PlayerApp;
import mms.pluginsystem.ControlPlugin;
import mms.pluginsystem.MenuPlugin;

public class PluginHost {
	private final PlayerApp app;

	public PluginHost(PlayerApp app) {
		this.app = app;
	}
	
	public MenuPlugin getMenu() {
		return app.getPluginLoader().getMenuBar();
	}
	
	public ControlPlugin getControl() {
		return app.getPluginLoader().getControl();
	}
	
	public AnchorPane getRootPane() {
		return app.getUIRoot();
	}
}
