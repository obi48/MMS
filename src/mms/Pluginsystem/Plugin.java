/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author Michael Obermüller
 */
public abstract class Plugin {

    protected final PluginHost pluginHost;
    private final Set<Plugin> listener = new ConcurrentSkipListSet<>();

    public Plugin(PluginHost pluginHost){
        this.pluginHost = pluginHost;
    }
    
    /**
     * This method is called if all plugins are successfully loaded and the 
     * mediaplayer starts
     * 
     * @return informs Pluginhost if plugin has successfully started
     */
    public abstract boolean start();
    
    /**
     * Informs you if mediaplayer stops running (program exit caused by user or 
     * error...)
     * 
     * @return informs Pluginhost if plugin has successfully stopped
     */
    public abstract boolean stop();
    
    /**
     * The name of Plugin developer
     * @return 
     */
    public abstract String getDeveloper();
    
    /**
     * The name of Plugin
     * @return 
     */
    public abstract String getName();
    
    /**
     * The version of plugin
     * @return 
     */
    public abstract String getVersion();
    
    /**
     * The description of plugin
     * @return 
     */
    public abstract String getDescription();

    /**
     * Unique identifier of plugin (internally used)
     * @return 
     */
    final int getID() {
        return (getDeveloper() + "," + getName() + "," + getVersion()).hashCode();
    }

    /**
     * Adds a Plugin as a listener of a plugin (internally used)
     * @param p listener
     */
    final void addListener(Plugin p) {
        listener.add(p);
    }

    /**
     * Removes a Plugin as a listener of a plugin (internally used)
     * @param p listener
     * @return true if successful
     */
    final boolean removeListener(Plugin p) {
        return listener.remove(p);
    }

    /**
     * Informs all plugins wich are listeners of this plugin
     * 
     * @param eventID ID of message (other plugins should know that)
     * @param args arbitrary objects (other plugins should know that)
     */
    public final void fireEvent(String eventID, Object... args) {
        listener.stream().forEach(p -> p.onEventReceived(eventID, args));
    }

    /**
     * Override this method if this Plugin listens to some other plugin(s)
     * 
     * @param eventID ID of message
     * @param args arbitrary objects
     */
    public void onEventReceived(String eventID, Object... args) {}
}
