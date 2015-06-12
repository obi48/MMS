/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.io.File;
import java.io.IOException;
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
                    
                    pluginHost.setMedia(file.toURI());
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        Menu fileMenu = pluginHost.getMenus().stream().filter(R -> R.getText().equals("File")).findFirst().get();

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

//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Open Media File");
//            fileChooser.getExtensionFilters().addAll(
//                    new ExtensionFilter("Audio Files", "*.mp3", "*.aif", "*.aiff", "*.wav"),
//                    new ExtensionFilter("Video Files", "*.fxm", "*.flv", "*.mp4", "*.m4v"),
//                    new ExtensionFilter("All supported Files", "*.mp3", "*.aif", "*.aiff", "*.wav", "*.fxm", "*.flv", "*.mp4", "*.m4v"));
//            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(menu.getContextMenu());
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
