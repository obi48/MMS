/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mms.Pluginsystem.Impl.PluginController;

/**
 *
 * @author Michael Obermüller
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        //Load GUI
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mms/View/GUI.fxml"));
        Pane root = (Pane) loader.load();
        
        //Get controller
        PluginController controller = loader.getController();
        controller.setPrimaryStage(stage);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("AOPP Studios Media Player");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("assets/logo.png").openStream()));
        stage.show();
    }
}
