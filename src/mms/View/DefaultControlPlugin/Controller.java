/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.View.DefaultControlPlugin;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
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

    private final Timeline textMaqueeTimer = new Timeline(80);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumeSlider.setTooltip(new Tooltip("Volume"));
        timeSlider.setTooltip(new Tooltip("Search bar"));

        // Create an indefinite time line.
        textMaqueeTimer.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO),
                new KeyFrame(Duration.INDEFINITE)
        );

        // Hook up a listener to the time line which triggers all object updates.
        textMaqueeTimer.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            boolean b = true;
            FadeTransition fade = new FadeTransition(Duration.seconds(2), marqueeText);

            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                double x = marqueeText.getTranslateX();
                if (x > -marqueeText.getBoundsInLocal().getMaxX() - 15) {
                    marqueeText.setTranslateX(x - 0.5);
                    if (b && x < 0) {
                        b = false;
                        fade.setFromValue(1);
                        fade.setToValue(0);
                        fade.playFromStart();

                        fade.setOnFinished((ActionEvent event) -> {
                            fade.setOnFinished(null);
                            marqueeText.setTranslateX(marqueeNode.getBoundsInLocal().getMaxX() - marqueeText.getBoundsInLocal().getMaxX() + 20);
                            fade.setFromValue(0);
                            fade.setToValue(1);
                            fade.playFromStart();
                            b = true;
                        });
                    }
                }
            }
        });

        playTime.textProperty().addListener((ObservableValue<? extends String> ob, String o, String n) -> {
            Platform.runLater(() -> {
                Text text = new Text(playTime.getText());
                text.setFont(playTime.getFont()); // Set the same font, so the size is the same

                double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                        + playTime.getPadding().getLeft() + playTime.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing

                playTime.setMinWidth(width); // Set the width
            });
        });
    }

    public boolean marqueeAnimation(String s) {
        textMaqueeTimer.stop();
        marqueeText.setTranslateX(marqueeNode.getBoundsInLocal().getMaxX() / 2.);
        marqueeText.setText(s);
        if (s != null && !s.isEmpty()) {
            textMaqueeTimer.playFromStart();
            FadeTransition fade = new FadeTransition(Duration.seconds(3), marqueeText);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.playFromStart();
            return true;
        } else {
            return false;
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

        if (duration.greaterThan(Duration.ZERO) && !duration.isIndefinite()) {
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
