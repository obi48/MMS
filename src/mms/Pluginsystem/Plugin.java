/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Obermüller
 */
public abstract class Plugin {

    protected final PluginHost pluginHost;
    private final List<Plugin> listener = new ArrayList<>();

    public Plugin(PluginHost pluginHost){
        this.pluginHost = pluginHost;
    }
    
    public abstract boolean start();
    public abstract boolean stop();
    public abstract String getDeveloper();
    public abstract String getName();
    public abstract String getVersion();
    public abstract String getDescription();

    final int getID() {
        return (getDeveloper() + "," + getName() + "," + getVersion()).hashCode();
    }

    final void addListener(Plugin p) {
        listener.add(p);
    }

    final boolean removeListener(Plugin p) {
        return listener.remove(p);
    }

    public final void fireEvent(String eventID, Object... args) {
        listener.stream().forEach(p -> p.onEventReceived(eventID, args));
    }

    public void onEventReceived(String eventID, Object... args) {}
}
