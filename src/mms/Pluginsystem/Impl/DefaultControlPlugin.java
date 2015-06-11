/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    private EventHandler<MouseEvent> event;
    private boolean repeat = false, mute = false, hideEffects = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private FadeTransition fade;

    public DefaultControlPlugin(PluginHost pluginHost, AnchorPane pane, MediaView mediaView) {
        super(pluginHost, pane, mediaView);
    }

    @Override
    public void onMediaPlayerChanged(MediaPlayer player) {  
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

        player.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
            controller.updateValues(player, duration);
        });

        player.setOnPlaying(() -> {
            controller.getPlayButton().setText("Pause");
            setControlHideEffects(true);
        });

        player.setOnPaused(() -> {
            controller.getPlayButton().setText("Play");
            setControlHideEffects(false);
        });
        
        player.setOnStopped(() -> {
            setControlHideEffects(false);
        });

        player.setOnReady(() -> {
            duration = player.getMedia().getDuration();
            controller.updateValues(player, duration);

            ObservableMap<String, Object> metaData = player.getMedia().getMetadata();

            //Metadata marquee animation
            String title = (String) metaData.get("title");
            String artist = (String) metaData.get("artist");

            if (!duration.isIndefinite()) {
                //check if we can build a marqueeAnimation (we cannot build if string is empty (no metadata)
                if (!controller.marqueeAnimation((artist == null ? "" : artist + " - ") + (title == null ? "" : title))) {
                    try {
                        String srcPath = new URI(player.getMedia().getSource()).getPath();
                        controller.marqueeAnimation(srcPath.substring(srcPath.lastIndexOf("/") + 1));
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(DefaultControlPlugin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                controller.getCycleButton().setVisible(true);
            } else {
                controller.marqueeAnimation("Stream");
                controller.getCycleButton().setVisible(false);
            }

            player.setMute(mute);
        });

        player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        player.setOnEndOfMedia(() -> {
            if (!repeat) {
                controller.getPlayButton().setText("Play");
                atEndOfMedia = true;
                player.seek(Duration.ZERO);
                player.stop();
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
        
        player.setOnError(() -> {
            Logger.getGlobal().severe("MediaPlayer error!");
        });
    }

    @Override
    public boolean start() {
        Logger.getGlobal().info("ControlPlugin started");

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
            fireEvent("CycleChanged", repeat);
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

        fade = new FadeTransition(Duration.seconds(1), controller.getFadePane());

        event = MouseEvent -> {
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

        if (player != null && player.getStatus() == Status.PLAYING) {
            pluginHost.addUIEventFilter(MouseEvent.MOUSE_MOVED, event);
        }

        controller.getFadePane().addEventHandler(MouseEvent.MOUSE_ENTERED, MouseEvent -> {
            setControlHideEffects(false);
        });

        controller.getFadePane().addEventHandler(MouseEvent.MOUSE_EXITED, MouseEvent -> {
            if (player != null && player.getStatus() == Status.PLAYING) {
                setControlHideEffects(true);
            }
        });        
        return true;
    }

    private void setControlHideEffects(boolean b) {
        if (!hideEffects && b) {
            pluginHost.addUIEventFilter(MouseEvent.MOUSE_MOVED, event);
            event.handle(null);
            hideEffects = true;
        } else if (hideEffects && !b) {
            pluginHost.removeUIEventFilter(MouseEvent.MOUSE_MOVED, event);
            fade.setOnFinished(null);
            fade.setDelay(Duration.seconds(0));
            fade.setFromValue(1);
            fade.setToValue(1);
            fade.playFromStart();
            hideEffects = false;
        }
    }

    @Override
    public boolean stop() {
        Logger.getGlobal().info("ControlPlugin stopped");
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
