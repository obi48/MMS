/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Menu;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaErrorEvent;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

/**
 * This is the API for the plugin - devs, here are all methods listed which are
 * reachable for them.
 *
 * @author Michael Obermüller
 */
public abstract class PluginHost {

    protected final List<Plugin> loadedPlugins = new ArrayList<>();
    protected final Set<Plugin> playerListener = new CopyOnWriteArraySet<>();

    /**
     * Registers your plugin as a listener on arbitrary other plugins
     *
     * You have to override the following method:
     * @see Plugin#onEventReceived(java.lang.String, java.lang.Object...) 
     * 
     * @param listener instance of your plugin
     * @param observable use static Identifier class!
     * @throws mms.Pluginsystem.PluginHost.PluginNotFoundException
     */
    public final void registerPluginListener(Plugin listener, Object observable) throws PluginNotFoundException {
        Plugin plugin;

        try {
            if (observable.equals(ControlPlugin.class)) {
                plugin = loadedPlugins.stream().filter(p -> p instanceof ControlPlugin).findFirst().get();
            } else if (observable.equals(MenuPlugin.class)) {
                plugin = loadedPlugins.stream().filter(p -> p instanceof MenuPlugin).findFirst().get();
            } else { //Must be an other plugin
                if (observable instanceof String) {
                    plugin = loadedPlugins.stream().filter(p -> p.getID() == observable.hashCode()).findFirst().get();
                } else { //must be Integer
                    plugin = loadedPlugins.stream().filter(p -> p.getID() == (int) observable).findFirst().get();
                }
            }
            plugin.addListener(listener);
        } catch (Exception e) {
            throw new PluginNotFoundException("Plugin with ID = \"" + Identifier.toString(observable) + "\" is not loaded!");
        }
    }

    /**
     * Deregisters your plugin
     *
     * @param listener instance of your plugin
     * @param observable use static Identifier class!
     * @return true if successful
     */
    public final boolean deregisterPluginListener(Plugin listener, Object observable) {
        Stream<Plugin> plugins;

        if (observable.equals(ControlPlugin.class)) {
            plugins = loadedPlugins.stream().filter(p -> p instanceof ControlPlugin);
        } else if (observable.equals(MenuPlugin.class)) {
            plugins = loadedPlugins.stream().filter(p -> p instanceof MenuPlugin);
        } else { //Must be an other plugin
            if (observable instanceof String) {
                plugins = loadedPlugins.stream().filter(p -> p.getID() == observable.hashCode());
            } else { //must be Integer
                plugins = loadedPlugins.stream().filter(p -> p.getID() == (int) observable);
            }
        }

        return plugins.count() != 1 ? false : plugins.findFirst().get().removeListener(listener);
    }

    /**
     * Adds a javaFX pane to the GUIstack (last added pane is on top)
     *
     * @param pane to add
     */
    public abstract void addToUIStack(Pane pane);

    /**
     * With this method it is possible to play new media-files
     *
     * @param player with new mediafile
     * @deprecated 
     */
    public abstract void setPlayer(MediaPlayer player);
    
    /**
     * With this method it is possible to play new Media-Files, the user will be
     * informed by an AltertMessage if an error occurs. Throws the MediaException
     * again, you may catch it to do further errorhandling...
     * 
     * @param src the URI to the new media-Source
     * @throws MediaException (RuntimeException)
     */
    public abstract void setMedia(URI src) throws MediaException;

    /**
     * Registers an event handler to mainUI - node. The handler is called when
     * the node receives an Event of the specified type during the bubbling
     * phase of event delivery.
     *
     * @param <T> the specific event class of the handler
     * @param eventType the type of the events to receive by the handler
     * @param eventHandler the handler to register
     */
    public abstract <T extends Event> void addUIEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler);

    /**
     * Registers an event filter to mainUI - node. The filter is called when the
     * node receives an Event of the specified type during the capturing phase
     * of event delivery.
     *
     * @param <T> the specific event class of the filter
     * @param eventType the type of the events to receive by the filter
     * @param eventFilter the filter to register
     */
    public abstract <T extends Event> void addUIEventFilter(final EventType<T> eventType, final EventHandler<? super T> eventFilter);

    /**
     * Unregisters a previously registered event handler from mainUI - node. One
     * handler might have been registered for different event types, so the
     * caller needs to specify the particular event type from which to
     * unregister the handler.
     *
     * @param <T> the specific event class of the handler
     * @param eventType the event type from which to unregister
     * @param eventHandler the handler to unregister
     */
    public abstract <T extends Event> void removeUIEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler);

    /**
     * The menus to show within this MenuBar. If this ObservableList is modified
     * at runtime, the MenuBar will update as expected
     *
     * @return the list of menus
     */
    public abstract ObservableList<Menu> getMenus();
    
    /**
     * If you want to get informed when the mediaplayer starts to play a new 
     * media you have to register here!
     * 
     * You have to override the following "see also" method:
     * @see Plugin#onMediaPlayerChanged(javafx.scene.media.MediaPlayer) 
     * 
     * @param plugin your plugin instance
     */
    public void addMediaListener(Plugin plugin){
        playerListener.add(plugin);
    }
    
    /**
     * Deregisters your plugin from the mediaListeners
     * 
     * @param plugin your plugin instance
     * @return true if successful
     */
    public boolean removeMediaListener(Plugin plugin){
        return playerListener.remove(plugin);
    }

    /**
     * Unregisters a previously registered event filter from mainUI - node. One
     * filter might have been registered for different event types, so the
     * caller needs to specify the particular event type from which to
     * unregister the filter.
     *
     * @param <T> the specific event class of the filter
     * @param eventType the event type from which to unregister
     * @param eventFilter the filter to unregister
     */
    public abstract <T extends Event> void removeUIEventFilter(final EventType<T> eventType, final EventHandler<? super T> eventFilter);

    /**
     * If you want to listen on Errors of reading Media... (Errors from the
     * Mediaview)
     * @param handler your Errorhandler
     */
    public abstract void addMediaErrorHandler(ChangeListener<EventHandler<MediaErrorEvent>> handler);
    
    /**
     * Unregisters a previously registered EventListener
     * @param handler your Errorhandler
     */
    public abstract void removeMediaErrorHandler(ChangeListener<EventHandler<MediaErrorEvent>> handler);
    
    public final class PluginNotFoundException extends RuntimeException {

        public PluginNotFoundException(String message) {
            super(message);
        }
    }

    public static class Identifier {

        public static Class ControlPlugin() {
            return ControlPlugin.class;
        }

        public static Class MenuPlugin() {
            return MenuPlugin.class;
        }

        /**
         * You need to know the ID from another plugin as integer identifier =
         * hashCode of "DevName,PluginName,Version"
         *
         * @deprecated it is easier to use Plugin(String)
         * @param identifier as integer
         * @return
         */
        public static int Plugin(int identifier) {
            return identifier;
        }

        /**
         * You need to know the Developers name, plugin name and the version
         *
         * @param identifier example: "DevName,PluginName,Version"
         * @return
         */
        public static String Plugin(String identifier) {
            return identifier;
        }

        static String toString(Object id) {
            if (id instanceof String) {
                String[] data = ((String) id).split(",");
                return "Developer [" + data[0] + "], PluginName [" + data[1] + "], Version [" + data[2] + "]";
            } else if (id instanceof Class) {
                return ((Class) id).getSimpleName();
            } else if (id instanceof Integer) {
                return "" + (int) id;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
