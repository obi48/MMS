package mms;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mms.Pluginsystem.Impl.PluginHost;
import mms.Pluginsystem.Impl.PluginLoader;
import mms.View.PluginSettingsController;


/**
 * 
 * @author Thomas Paireder
 *
 */
public class PlayerApp extends Application {
	private PluginLoader loader;
	private PluginHost host;
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		// Create GUI
		try {
			FXMLLoader fxmlloader = new FXMLLoader();
			fxmlloader.setLocation(getClass().getResource("/mms/view/CoreGUI.fxml"));
			AnchorPane rootPane = (AnchorPane)fxmlloader.load();
			Scene scene = new Scene(rootPane, 400, 400);
			this.primaryStage = primaryStage;
			primaryStage.setScene(scene);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			// Load plugins
			host  = new PluginHost(this);
			loader = new PluginLoader(this);
			loader.startPlugins();
			
			Menu fileMenu = new Menu("File");
			loader.getMenuBar().addFirstMenu(fileMenu);
			
			Menu pluginsMenu = new Menu("Plugins");
			loader.getMenuBar().addMenu(pluginsMenu);
			MenuItem configItem = new MenuItem("Configure...");
			pluginsMenu.getItems().add(configItem);
			configItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					showPluginSettings();
				}
			});
			
			// Show window
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showPluginSettings() {
		try {
			// Load the fxml file and create a new stage for the popup
			FXMLLoader fxmlloader = new FXMLLoader(PlayerApp.class.getResource("/mms/view/PluginSettings.fxml"));
			AnchorPane page = (AnchorPane) fxmlloader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Plugin Configuration");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Sets the plugin lists
			PluginSettingsController controller = fxmlloader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMenuPlugins(loader.getAvailableMenus());
			//controller.setPerson(person);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			//return controller.isOkClicked();

		} catch (IOException ex) {
			// Exception gets thrown if the fxml file could not be loaded
			Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	@Override
	public void stop() throws Exception {
		loader.stopPlugins();
		loader.writeConfig();
		super.stop();
	}
	
	public PluginLoader getPluginLoader() {
		return loader;
	}
	
	public PluginHost getPluginHost() {
		return host;
	}
	
	public AnchorPane getUIRoot() {
		return (AnchorPane) primaryStage.getScene().getRoot();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
