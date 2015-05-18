package mms.Pluginsystem;

import mms.Pluginsystem.Impl.PluginHost;

public abstract class Plugin {
	protected final PluginHost host;
	
	public Plugin(PluginHost h) {
		this.host = h;
	}
	
	public abstract String getDeveloper();
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	public final int getID() {
		return (getDeveloper() + "," + getName() + "," + getDescription()).hashCode();
	}
	
	public abstract void start();
	
	public abstract boolean stop();
}
