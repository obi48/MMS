/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import mms.Pluginsystem.ControlPlugin;
import mms.Pluginsystem.PluginHost;
import mms.View.DefaultControlPlugin.Controller;

/**
 *
 * @author Michael Obermüller
 */
public class DefaultControlPlugin extends ControlPlugin {

    private MediaPlayer player;
    private Controller controller;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;

    public DefaultControlPlugin(PluginHost pluginHost, AnchorPane pane, MediaView mediaView) {
        super(pluginHost, pane, mediaView);
    }

    @Override
    public void onMediaPlayerChanged(MediaPlayer player) {
        System.out.println("Media changed event");
        this.player = player;
        player.play();

        controller.getPlayButton().setOnAction((ActionEvent event) -> {
            Status status = player.getStatus();

            if (status == Status.UNKNOWN || status == Status.HALTED) {
                // don't do anything in these states
                return;
            }

            if (status == Status.PAUSED
                    || status == Status.READY
                    || status == Status.STOPPED) {
                // rewind if we're sitting at the end
                if (atEndOfMedia) {
                    player.seek(player.getStartTime());
                    atEndOfMedia = false;
                }
                player.play();
            } else {
                player.pause();
            }
        });

        player.currentTimeProperty().addListener((Observable ov) -> {
            controller.updateValues(player, duration);
        });

        player.setOnPlaying(() -> {
            if (stopRequested) {
                player.pause();
                stopRequested = false;
            } else {
                controller.getPlayButton().setText("Pause");
            }
        });

        player.setOnPaused(() -> {
            System.out.println("onPaused");
            controller.getPlayButton().setText("Play");
        });

        player.setOnReady(() -> {
            duration = player.getMedia().getDuration();
            controller.updateValues(player, duration);
        });

        player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        player.setOnEndOfMedia(() -> {
            if (!repeat) {
                controller.getPlayButton().setText("Play");
                stopRequested = true;
                atEndOfMedia = true;
            }
        });

        controller.getTimeSlider().valueProperty().addListener((Observable ov) -> {
            if (controller.getTimeSlider().isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                player.seek(duration.multiply(controller.getTimeSlider().getValue() / 100.0));
            }
        });

        controller.getVolumeSlider().valueProperty().addListener((Observable ov) -> {
            if (controller.getVolumeSlider().isValueChanging()) {
                player.setVolume(controller.getVolumeSlider().getValue() / 100.0);
            }
        });
    }

    @Override
    public boolean start() {
        System.out.println("ControlPlugin started");

        FXMLLoader fxmlLoader = new FXMLLoader();

        try {
            Pane root = fxmlLoader.load(getClass().getClassLoader().getResource("mms/View/DefaultControlPlugin/ControlView.fxml").openStream());
            pluginHost.addToUIStack(root);
            controller = fxmlLoader.getController();
        } catch (IOException ex) {
            Logger.getLogger(DefaultControlPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }

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
