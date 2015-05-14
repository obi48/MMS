/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;

/**
 * This is the API for the plugin - devs, here are all methods listed which are
 * reachable for them.
 *
 * @author Michael Obermüller
 */
public abstract class PluginHost {

    protected final List<Plugin> loadedPlugins = new ArrayList<>();

    public abstract void addToUIStack(Pane pane);
    public abstract void setPlayer(MediaPlayer player);

    public final void registerPluginListener(Plugin listener, int observableID) throws PluginNotFoundException {
        Stream<Plugin> plugins = loadedPlugins.stream().filter(p -> p.getID() == observableID);

        if (plugins.count() == 0) {
            throw new PluginNotFoundException("Plugin with ID \"" + observableID + "\" is not loaded!");
        }
        
        plugins.findFirst().get().addListener(listener);
    }
    
    public final boolean deregisterPluginListener(Plugin listener, int observableID) {
        Stream<Plugin> plugins = loadedPlugins.stream().filter(p -> p.getID() == observableID);
        return plugins.count() != 1 ? false : plugins.findFirst().get().removeListener(listener);
    }

    public final class PluginNotFoundException extends Exception {

        public PluginNotFoundException(String message) {
            super(message);
        }
    }
}
