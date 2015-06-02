/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
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
        //Get root logger
        Logger logger = Logger.getLogger("");

        //Load logger configuration file
        try (FileInputStream configFile = new FileInputStream("Logger.cfg")) {
            //Loads external file
            LogManager.getLogManager().readConfiguration(configFile);
        } catch (IOException ex) {
            //If external file does not exist, load internal and create an external one
            System.out.println("WARNING: Could not open external configuration file");
            System.out.println("WARNING: Using default configuration file...");

            try (InputStream configFile = ClassLoader.getSystemClassLoader().getResource("assets/DefaultLogger.cfg").openStream()) {
                Files.copy(configFile, new File("Logger.cfg").toPath());
                LogManager.getLogManager().readConfiguration(ClassLoader.getSystemClassLoader().getResource("assets/DefaultLogger.cfg").openStream());
            } catch (IOException ex1) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        Logger.getGlobal().info("Starting application...");

        try {
            //Start program
            launch(args);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            System.exit(0);
        }
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
