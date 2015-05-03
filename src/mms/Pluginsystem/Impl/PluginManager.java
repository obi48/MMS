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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import mms.Pluginsystem.Impl.PluginManager;
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
        Media m = new Media("http://tegos.kz/new/mp3_full/Eminem_feat_Rihanna_-_The_Monster.mp3");
//        Media m = new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv");

        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty height = mediaView.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        MediaPlayer mp = new MediaPlayer(m);
        mediaView.setMediaPlayer(mp);

        mp.play();
        
        start();
    }

    public void start() {
        File[] files = new File("Plugins").listFiles();
        for (File f : files) {
            loadPlugin(f);
        }
        loadedPlugins.stream().forEach((pi) -> {
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

            JarEntry entry;
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                entry = entries.nextElement();

                if (entry.getName().endsWith(PluginInterface.class.getName())) {
                    //Load class
                    Class cl = new URLClassLoader(new URL[]{file.toURI().toURL()}).loadClass(entry.getName());

                    //Get implemented Interfaces
                    Class[] interfaces = cl.getInterfaces();

                    //Check implemented interfaces (should implement our PluginInterface)
                    boolean isPlugin = false;
                    for (int y = 0; y < interfaces.length && !isPlugin; y++) {
                        if (interfaces[y].getName().equals("mms.Pluginsystem.PluginInterface")) { //TODO replace string with refactorable class.getName...
                            isPlugin = true;
                        }
                    }

                    if (isPlugin) {
                        loadedPlugins.add((PluginInterface) cl.newInstance());
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
