/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 *
 * @author Michael Obermüller
 */
public abstract class ControlPlugin extends Plugin{
    protected final AnchorPane anchorPane;
    protected final MediaView mediaView;
    
    public ControlPlugin(PluginHost pluginHost, AnchorPane pane, MediaView mediaView){
        super(pluginHost);
        this.anchorPane = pane;
        this.mediaView = mediaView;
    }
    
    public abstract void onMediaPlayerChanged(MediaPlayer player);
}
