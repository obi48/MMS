/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms;

import java.io.File;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Michael Obermüller
 */
public class MyMediaPlayer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws IOException {

        //Load GUI
        Pane root = (Pane) FXMLLoader.load(getClass().getClassLoader().getResource("View/GUI.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

//        String workingDir = System.getProperty("user.dir");
//        final File f = new File(workingDir, "../media/omgrobots.flv");
//
//        final Media m = new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv");
//        final MediaPlayer mp = new MediaPlayer(m);
//        final MediaView mv = new MediaView(mp);
//
//        final DoubleProperty width = mv.fitWidthProperty();
//        final DoubleProperty height = mv.fitHeightProperty();
//
//        width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
//        height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));
//
//        mv.setPreserveRatio(true);
//
//        StackPane root = new StackPane();
//        root.getChildren().add(mv);
//
//        final Scene scene = new Scene(root, 960, 540);
//        scene.setFill(Color.BLACK);
//
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("Full Screen Video Player");
//        primaryStage.setFullScreen(true);
//        primaryStage.show();
//
//        mp.play();
    }

    public void startBugged(final Stage stage) throws Exception {
        stage.setTitle("Movie Player");
        StackPane root = new StackPane();

        String trailer = "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
        Media media = new Media(trailer);
        final MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        final Timeline slideIn = new Timeline();
        final Timeline slideOut = new Timeline();
        root.setOnMouseExited((MouseEvent mouseEvent) -> {
            slideOut.play();
        });
        root.setOnMouseEntered((MouseEvent mouseEvent) -> {
            slideIn.play();
        });
        final VBox vbox = new VBox();
        final Slider slider = new Slider();
        vbox.getChildren().add(slider);

        final DoubleProperty width = view.fitWidthProperty();
        final DoubleProperty height = view.fitHeightProperty();
        width.bind(Bindings.selectDouble(view.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(view.sceneProperty(), "height"));
        view.setPreserveRatio(true);

//        vbox.getChildren().add(hbox);
        root.getChildren().add(view);
        root.getChildren().add(vbox);

        Scene scene = new Scene(root, 960, 540, Color.BLACK);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

        player.play();
        player.setOnReady(() -> {
            int w = player.getMedia().getWidth();
            int h = player.getMedia().getHeight();

            stage.setMinWidth(w);
            stage.setMinHeight(h);

            vbox.setMinSize(w, 100);
            vbox.setTranslateY(h - 100);

            slider.setMin(0.0);
            slider.setValue(0.0);
            slider.setMax(player.getTotalDuration().toSeconds());

            slideOut.getKeyFrames().addAll(
                    new KeyFrame(new Duration(0),
                            new KeyValue(vbox.translateYProperty(), h - 100),
                            new KeyValue(vbox.opacityProperty(), 0.9)
                    ),
                    new KeyFrame(new Duration(300),
                            new KeyValue(vbox.translateYProperty(), h),
                            new KeyValue(vbox.opacityProperty(), 0.0)
                    )
            );
            slideIn.getKeyFrames().addAll(
                    new KeyFrame(new Duration(0),
                            new KeyValue(vbox.translateYProperty(), h),
                            new KeyValue(vbox.opacityProperty(), 0.0)
                    ),
                    new KeyFrame(new Duration(300),
                            new KeyValue(vbox.translateYProperty(), h - 100),
                            new KeyValue(vbox.opacityProperty(), 0.9)
                    )
            );
        });
        player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observableValue, Duration duration, Duration current) -> {
            slider.setValue(current.toSeconds());
        });
        slider.setOnMouseClicked((MouseEvent mouseEvent) -> {
            player.seek(Duration.seconds(slider.getValue()));
        });
    }

}
