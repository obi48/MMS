/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
        Logger.getGlobal().info("MenuPlugin started");

        //Add Fade effects to menu bar
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
                for (File file : db.getFiles()) {
                    pluginHost.setMedia(file.toURI());
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        //Get the file menu
        Menu fileMenu = pluginHost.getMenus().stream().filter(R -> R.getText().equals("File")).findFirst().get();

        //Init and add Open file item
        MenuItem openItem = new MenuItem("Open...");
        fileMenu.getItems().add(openItem);

        openItem.setOnAction(ActionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Add Files to selected Playlist");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            List<String> temp = new ArrayList<>();
            temp.addAll(java.util.Arrays.asList(PluginHost.getSupportedVideoFormats()));
            temp.addAll(java.util.Arrays.asList(PluginHost.getSupportedAudioFormats()));

            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Audio Files", PluginHost.getSupportedAudioFormats()),
                    new ExtensionFilter("Video Files", PluginHost.getSupportedVideoFormats()),
                    new ExtensionFilter("All supported Files", temp));

            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                pluginHost.setMedia(file.toURI());
            }
        });

        //Init and add Close-Menu item
        MenuItem closeItem = new MenuItem("Close");
        fileMenu.getItems().add(closeItem);

        closeItem.setOnAction(ActionEvent -> {
            System.exit(0);
        });

        Menu helpMenu = pluginHost.getMenus().stream().filter(R -> R.getText().equals("Help")).findFirst().get();
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().add(aboutItem);

        aboutItem.setOnAction(ActionEvent -> {
            try {
                Pane root = FXMLLoader.load(getClass().getClassLoader().getResource("mms/View/DefaultMenuPlugin/AboutView.fxml"));

                Scene scene = new Scene(root);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.setTitle("About");
                newStage.show();
            } catch (IOException ex) {
                Logger.getLogger(DefaultControlPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        return true;
    }

    @Override
    public boolean stop() {
        Logger.getGlobal().info("MenuPlugin stopped");
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
