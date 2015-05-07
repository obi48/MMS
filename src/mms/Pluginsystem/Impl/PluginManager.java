/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import mms.Pluginsystem.PluginInterface;
import mms.Pluginsystem.PluginManagerInterface;

/**
 * FXML Controller class
 *
 * @author Michael Obermüller
 */
public class PluginManager implements Initializable, PluginManagerInterface {

    @FXML
    private StackPane stackPane;
    @FXML
    private MediaView mediaView;
    @FXML
    private MenuBar menuBar;

    private final List<PluginInterface> loadedPlugins = new ArrayList<>();

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

        final PluginManager manager = this;

        //Will be called on program exit
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                manager.stop();
            }
        });
    }

    public void start() {
        File[] files = new File("Plugins").listFiles();
        for (File f : files) {
            loadPlugin(f);
        }
        loadedPlugins.stream().forEach((pi) -> {
            pi.setPluginManager(this);
            pi.start();
        });
    }

    public void stop() {
        loadedPlugins.stream().forEach((pi) -> {
            pi.stop();
        });
    }

    public void loadPlugin(File file) {
        try {
            //Create the JAR-object
            JarFile jar = new JarFile(file);

            String entryName;
            Enumeration<JarEntry> entries = jar.entries();
            jar.close();

            while (entries.hasMoreElements()) {
                entryName = entries.nextElement().getName();

                if (entryName.endsWith(".class")) {
                    //Load class
                    URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});

                    //Delete .class and replace / with .
                    String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');

                    //Load class
                    Class cl = loader.loadClass(className);
                    loader.close();

                    //Check implemented interfaces (should implement our PluginInterface)
                    if (PluginInterface.class.isAssignableFrom(cl)) {
                        loadedPlugins.add((PluginInterface) cl.newInstance());
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //******************************* API *************************************/
    @Override
    public void addToGUI(Pane pane) {
        stackPane.getChildren().add(pane);
    }

    @Override
    public MenuBar getMenuBar() {
        return menuBar;
    }

    @Override
    public void setPlayer(MediaPlayer player) {
        mediaView.setMediaPlayer(player);
    }
    //***********************************************************************/

}
