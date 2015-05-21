/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.View.DefaultMenuPlugin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * FXML Controller class
 *
 * @author Michael Obermüller
 */
public class AboutViewController implements Initializable {
    @FXML
    private ImageView logoView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logoView.setImage(new Image(getClass().getClassLoader().getResource("mms/View/DefaultMenuPlugin/logo.png").openStream()));
        } catch (IOException ex) {
            Logger.getLogger(AboutViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }    
    
}
