/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.View.DefaultControlPlugin;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Michael Obermüller
 */
public class Controller implements Initializable {

    @FXML
    private Slider volumeSlider;
    @FXML
    private ToggleButton muteButton;
    @FXML
    private Button playButton;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label playTime;
    @FXML
    private ToggleButton cycleButton;
    @FXML
    private AnchorPane fadePane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane marqueeNode;
    @FXML
    private Text marqueeText;

    private final double SPEED_FACTOR = 0.07;
    private TranslateTransition transition;
    private final Timeline textMaquee = new Timeline(80);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumeSlider.setTooltip(new Tooltip("Volume"));
        timeSlider.setTooltip(new Tooltip("Search bar"));

//        transition = new TranslateTransition(new Duration(5), marqueeText);
//        transition.setInterpolator(Interpolator.LINEAR);
//        transition.setCycleCount(1);
//
//        transition.setOnFinished((ActionEvent actionEvent) -> {
//            rerunAnimation(marqueeText.getText());
//        });
//
//        rerunAnimation("Sylver - Skin");
        // Create an indefinite time line.
        textMaquee.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO),
                new KeyFrame(Duration.INDEFINITE)
        );

        // Hook up a listener to the time line which triggers all object updates.
        textMaquee.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            double x = marqueeText.getTranslateX();
            if (x > -marqueeText.getBoundsInLocal().getMaxX() - 15) {
                marqueeText.setTranslateX(x - 0.5);
            } else {
                marqueeText.setTranslateX(marqueeNode.getBoundsInLocal().getMaxX() + 15);
            }
        });
    }

    public void marqueeAnimation(String s) {
        textMaquee.stop();
        marqueeText.setText(s);
        if (s != null && !s.isEmpty()) {
            textMaquee.playFromStart();
        }
    }

    public AnchorPane getFadePane() {
        return fadePane;
    }

    public ToggleButton getCycleButton() {
        return cycleButton;
    }

    public Slider getTimeSlider() {
        return timeSlider;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public ToggleButton getMuteButton() {
        return muteButton;
    }

    public Button getPlayButton() {
        return playButton;
    }

    public void updateValues(MediaPlayer mp, Duration duration) {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mp.getCurrentTime();
                playTime.setText(formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(currentTime.divide(duration).toMillis()
                            * 100.0);
                }
                if (!volumeSlider.isValueChanging()) {
                    volumeSlider.setValue((int) Math.round(mp.getVolume()
                            * 100));
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
}
