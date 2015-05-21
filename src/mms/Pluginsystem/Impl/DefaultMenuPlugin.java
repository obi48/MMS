/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.File;
import javafx.animation.FadeTransition;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import mms.Pluginsystem.MenuPlugin;
import mms.Pluginsystem.PluginHost;

/**
 *
 * @author Michael Obermüller
 */
public class DefaultMenuPlugin extends MenuPlugin {

    public DefaultMenuPlugin(PluginHost pluginHost, MenuBar menu) {
        super(pluginHost, menu);
    }

    @Override
    public boolean start() {
        System.out.println("MenuPlugin started");

        FadeTransition fade = new FadeTransition(Duration.seconds(1), menu);
        fade.setFromValue(0);
        fade.setToValue(0);
        fade.playFromStart();

        menu.addEventHandler(MouseEvent.MOUSE_ENTERED, MouseEvent -> {
            fade.setDelay(Duration.seconds(0));
            fade.setFromValue(1);
            fade.setToValue(1);
            fade.playFromStart();
        });

        menu.addEventHandler(MouseEvent.MOUSE_EXITED, MouseEvent -> {
            fade.setDelay(Duration.seconds(3));
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.playFromStart();
        });

        //Drag and drop files support
        pluginHost.addUIEventFilter(DragEvent.DRAG_OVER, (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.LINK);
            } else {
                event.consume();
            }
        });
        pluginHost.addUIEventFilter(DragEvent.DRAG_DROPPED, (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath = null;
                for (File file : db.getFiles()) {
                    filePath = file.toURI().toString();
                    pluginHost.setPlayer(new MediaPlayer(new Media(filePath)));
                    System.out.println(filePath);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        
        

        return true;
    }

    @Override
    public boolean stop() {
        System.out.println("MenuPlugin stopped");
        return true;
    }

    @Override
    public String getDeveloper() {
        return "AOPP Studios";
    }

    @Override
    public String getName() {
        return DefaultMenuPlugin.class.getSimpleName();
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Implements a default menuBar for a mediaPlayer";
    }
}
