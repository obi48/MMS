/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import mms.Pluginsystem.ControlPlugin;
import mms.Pluginsystem.PluginHost;

/**
 *
 * @author Michael Obermüller
 */
public class DefaultControlPlugin extends ControlPlugin {

    public DefaultControlPlugin(PluginHost pluginHost, AnchorPane pane, MediaView mediaView) {
        super(pluginHost, pane, mediaView);
    }

    @Override
    public void onMediaPlayerChanged(MediaPlayer player) {
        System.out.println("Media changed event");
    }

    @Override
    public boolean start() {
        System.out.println("ControlPlugin started");
        return true;
    }

    @Override
    public boolean stop() {
        System.out.println("ControlPlugin stopped");
        return true;
    }

    @Override
    public String getDeveloper() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
