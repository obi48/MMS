/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;

/**
 * This is the API for the plugin - devs, here are all methods listed which are
 * reachable for them.
 * 
 * @author Michael Obermüller
 */
public interface PluginManagerInterface {
    public void addToGUI(Pane pane);
    public MenuBar getMenuBar();
    public void setPlayer(MediaPlayer player);
}
