package mms.Pluginsystem.Impl;

import mms.Pluginsystem.ControlPlugin;

public class DefaultControlPlugin extends ControlPlugin {

	public DefaultControlPlugin(PluginHost h) {
		super(h);
	}
	
	@Override
	public String getDeveloper() {
		return "MMS";
	}

	@Override
	public String getName() {
		return "Standard Control Elements";
	}

	@Override
	public String getDescription() {
		return "Provides standard player control elements with default look.";
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}

}
