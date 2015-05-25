/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import mms.Pluginsystem.ControlPlugin;
import mms.Pluginsystem.MenuPlugin;
import mms.Pluginsystem.Plugin;
import mms.Pluginsystem.PluginHost;

/**
 * FXML Controller class
 *
 * @author Michael Obermüller
 */
public class PluginController extends PluginHost implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private MediaView mediaView;
    @FXML
    private MenuBar menuBar;
    @FXML
    private AnchorPane anchorPane;

    private ControlPlugin controlPlugin;
    private Stage primaryStage;
    private boolean loadedControl = false, loadedMenu = false;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty height = mediaView.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        //Load plugins
        start();

        final PluginController manager = this;

        //Will be called on program exit
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                manager.stop();
            }
        });
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        //On mouse double click switch to fullscreenmode
        anchorPane.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    primaryStage.setFullScreen(!primaryStage.isFullScreen());
                }
            }
        });
    }

    public void start() {
        File[] files = new File("Plugins").listFiles();
        if (files != null) {
            for (File f : files) {
                loadPlugin(f);
            }
        }

        //Check if one MenuPlugin is loaded, if not load default one
        if (!loadedMenu) {
            loadedPlugins.add(new DefaultMenuPlugin(this, menuBar));
        }

        //Check if one ControlPlugin is loaded, if not load default one
        if (!loadedControl) {
            loadedPlugins.add(controlPlugin = new DefaultControlPlugin(this, anchorPane, mediaView));
        }

        //Sort plugins (Controlplugin is first then menuPlugin then others)
        loadedPlugins.sort((Plugin o1, Plugin o2) -> {
            if (o1 instanceof ControlPlugin || o1 instanceof MenuPlugin && o2 instanceof Plugin) {
                return -1;
            } else {
                return 0; //TODO define loadorder for all other plugins!
            }
        });

        loadedPlugins.stream().forEach(pi -> pi.start());
    }

    public void stop() {
        //Stop reversed
        loadedPlugins.stream().collect(Collectors.toCollection(LinkedList::new)).descendingIterator().forEachRemaining(pi -> pi.stop());
    }

    public void loadPlugin(File file) {

        try (JarFile jar = new JarFile(file)) {
            String entryName;
            Enumeration<JarEntry> entries = jar.entries();

            //Adds jar to SystemClassPath
            addSoftwareLibrary(file);

            while (entries.hasMoreElements()) {
                entryName = entries.nextElement().getName();

                if (entryName.endsWith(".class")) {
                    Class cl;

                    try (URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()})) {
                        //Delete .class and replace / with .
                        String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                        //Load class
                        cl = loader.loadClass(className);
                    }

                    //Check implemented interfaces (should implement our PluginInterface)
                    if (ControlPlugin.class.isAssignableFrom(cl)) {
                        if (!loadedControl) {
                            controlPlugin = (ControlPlugin) cl.getDeclaredConstructor(PluginHost.class, AnchorPane.class, MediaView.class).newInstance(this, anchorPane, mediaView);
                            loadedPlugins.add(controlPlugin);
                            loadedControl = true;
                        } else {
                            //one ControlPlugin is already loaded! --> only one allowed
                            throw new IllegalStateException("Only one ControlPlugin is allowed! (loaded twice)");
                        }
                    } else if (MenuPlugin.class.isAssignableFrom(cl)) {
                        if (!loadedMenu) {
                            MenuPlugin plugin = (MenuPlugin) cl.getDeclaredConstructor(PluginHost.class, MenuBar.class).newInstance(this, menuBar);
                            loadedPlugins.add(plugin);
                            loadedMenu = true;
                        } else {
                            //one MenuPlugin is already loaded! --> only one allowed
                            throw new IllegalStateException("Only one MenuPlugin is allowed! (loaded twice)");
                        }
                    } else if (Plugin.class.isAssignableFrom(cl)) {
                        loadedPlugins.add((Plugin) cl.getDeclaredConstructor(PluginHost.class).newInstance(this));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PluginHost.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addSoftwareLibrary(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
    }

    //******************************* API *************************************/
    @Override
    public void addToUIStack(Pane pane) {
        stackPane.getChildren().add(pane);
    }

    @Override
    public void setPlayer(MediaPlayer player) {
        MediaPlayer p;
        if ((p = mediaView.getMediaPlayer()) != null) {
            p.stop();
            player.setVolume(mediaView.getMediaPlayer().getVolume());
        }
        mediaView.setMediaPlayer(player);

        //Controlplugin is always a mediaplayerlistener!
        controlPlugin.onMediaPlayerChanged(player);

        playerListener.stream().forEach(plugin -> {
            plugin.onMediaPlayerChanged(player);
        });
    }

    @Override
    public <T extends Event> void addUIEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        anchorPane.addEventHandler(eventType, eventHandler);
    }

    @Override
    public <T extends Event> void addUIEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
        anchorPane.addEventFilter(eventType, eventFilter);
    }

    @Override
    public <T extends Event> void removeUIEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        anchorPane.removeEventHandler(eventType, eventHandler);
    }

    @Override
    public <T extends Event> void removeUIEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
        anchorPane.removeEventFilter(eventType, eventFilter);
    }

    @Override
    public ObservableList<Menu> getMenus() {
        return menuBar.getMenus();
    }
    //***********************************************************************/
}
