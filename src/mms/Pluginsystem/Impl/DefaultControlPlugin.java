/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.SepiaTone;
import javafx.scene.input.MouseEvent;
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
    private boolean repeat = false, mute = false;
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

        player.currentTimeProperty().addListener(Observable -> {
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

            ObservableMap<String, Object> metaData = player.getMedia().getMetadata();

            //Metadata marquee animation
            String title = (String) metaData.get("title");
            String artist = (String) metaData.get("artist");
            controller.marqueeAnimation((artist == null ? "" : artist + " - ") + (title == null ? "" : title));
            
            player.setMute(mute);
        });

        player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        player.setOnEndOfMedia(() -> {
            if (!repeat) {
                controller.getPlayButton().setText("Play");
                stopRequested = true;
                atEndOfMedia = true;
            }
        });

        controller.getTimeSlider().valueProperty().addListener((Observable, oldValue, newValue) -> {
            if (controller.getTimeSlider().isValueChanging() || Math.abs(oldValue.doubleValue() - newValue.doubleValue()) > 1) {
                // multiply duration by percentage calculated by slider position
                player.seek(duration.multiply(controller.getTimeSlider().getValue() / 100.0));
            }
        });

        controller.getVolumeSlider().valueProperty().addListener(Observable -> {
            player.setVolume(controller.getVolumeSlider().getValue() / 100.0);
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

        controller.getCycleButton().setOnAction(ActionEvent -> {
            if (controller.getCycleButton().isSelected()) {
                controller.getCycleButton().setEffect(new SepiaTone(1.0));
                repeat = true;
            } else {
                controller.getCycleButton().setEffect(null);
                repeat = false;
            }
            if (player != null) {
                player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
            }
        });

        controller.getMuteButton().setOnAction(ActionEvent -> {
            if (controller.getMuteButton().isSelected()) {
                controller.getMuteButton().setEffect(new SepiaTone(1.0));
                mute = true;
            } else {
                controller.getMuteButton().setEffect(null);
                mute = false;
            }
            if (player != null) {
                player.setMute(mute);
            }
        });

        FadeTransition fade = new FadeTransition(Duration.seconds(1), controller.getFadePane());
        fade.setFromValue(0);
        fade.setToValue(0);
        fade.playFromStart();

        EventHandler<MouseEvent> event = MouseEvent -> {
            fade.setDelay(Duration.seconds(0));
            fade.setFromValue(1);
            fade.setToValue(1);
            fade.playFromStart();
            fade.setOnFinished(ActionEvent -> {
                fade.setOnFinished(null);
                fade.setDelay(Duration.seconds(4));
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.playFromStart();
            });
        };

        pluginHost.addUIEventFilter(MouseEvent.MOUSE_MOVED, event);

        controller.getFadePane().addEventHandler(MouseEvent.MOUSE_ENTERED, MouseEvent -> {
            pluginHost.removeUIEventFilter(MouseEvent.MOUSE_MOVED, event);
            fade.setOnFinished(null);
            fade.setDelay(Duration.seconds(0));
            fade.setFromValue(1);
            fade.setToValue(1);
            fade.playFromStart();
        });

        controller.getFadePane().addEventHandler(MouseEvent.MOUSE_EXITED, MouseEvent -> {
            pluginHost.addUIEventFilter(MouseEvent.MOUSE_MOVED, event);
            event.handle(null);
        });

        return true;
    }

    @Override
    public boolean stop() {
        System.out.println("ControlPlugin stopped");
        return true;
    }

    @Override
    public String getDeveloper() {
        return "AOPP Studios";
    }

    @Override
    public String getName() {
        return DefaultControlPlugin.class.getSimpleName();
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "This plugin implements default mediaplayer-controls";
    }
}
