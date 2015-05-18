package mms.View;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mms.Pluginsystem.Plugin;
import mms.Pluginsystem.Impl.PluginLoader;

public class PluginSettingsController {
	@FXML
	TableView<Plugin> menuTable;

	private Stage dialogStage;

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setMenuPlugins(List<Class<Plugin>> list) {
		ObservableList<Plugin> menuPluginList = FXCollections.observableArrayList(
			list.stream()
			.map(cl -> {
				try {
					Constructor<Plugin> ctor = cl.getConstructor(PluginLoader.class);
					return ctor.newInstance(this);
				} catch(Exception ex) {
					Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
				}
				return null;
			})
			.collect(Collectors.toList())
		);
		menuTable.setItems(menuPluginList);
	}
}
