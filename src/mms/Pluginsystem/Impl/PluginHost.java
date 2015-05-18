package mms.Pluginsystem.Impl;

import javafx.scene.layout.AnchorPane;
import mms.PlayerApp;
import mms.Pluginsystem.ControlPlugin;
import mms.Pluginsystem.MenuPlugin;

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
