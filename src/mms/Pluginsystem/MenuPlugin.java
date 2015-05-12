package mms.pluginsystem;

import javafx.scene.control.Menu;
import mms.pluginsystem.impl.PluginHost;

public abstract class MenuPlugin extends Plugin {

	public MenuPlugin(PluginHost h) {
		super(h);
	}
	
	public abstract void addMenu(Menu m);
	
	public abstract void addFirstMenu(Menu m);
}
