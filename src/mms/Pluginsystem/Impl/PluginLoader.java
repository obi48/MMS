package mms.pluginsystem.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import mms.PlayerApp;
import mms.pluginsystem.ControlPlugin;
import mms.pluginsystem.MenuPlugin;
import mms.pluginsystem.Plugin;

public class PluginLoader {
	
	private final PlayerApp mainApp;
	private final List<Class<Plugin>> availablePlugins = new ArrayList<>();
	private List<Class<Plugin>> presetPlugins;
	private final List<Plugin> loadedPlugins = new ArrayList<>();
	private MenuPlugin menuPlugin;
	private ControlPlugin controlPlugin;

	public PluginLoader(PlayerApp app) {
		this.mainApp = app;
		initialize();
	}
	
	private void initialize() {
		// List available plugins
		File[] files = new File("plugins").listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".jar");
		    }
		});
		for (File f : files) {
			listPlugins(f);
		}
		
		loadPlugins();
		presetPlugins = loadedPlugins.stream().map(p -> (Class<Plugin>)p.getClass()).collect(Collectors.toList());
	}

	/**
	 * Loads the available plugins from a jar file.
	 * @param file
	 */
	private void listPlugins(File file) {
		try {
			//Create the JAR-object
			JarFile jar = new JarFile(file);

			String entryName;
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				entryName = entries.nextElement().getName();

				if (entryName.endsWith(".class")) {
					// Load class
					URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});

					// Delete .class and replace / with .
					String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');

					// Load class
					Class<?> clazz = loader.loadClass(className);

					// Check implemented interfaces (should implement our Plugin interface)
					if (Plugin.class.isAssignableFrom(clazz)) {
						availablePlugins.add((Class<Plugin>) clazz);
					}
				}
			}
			
			jar.close();
		} catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * Loads the plugins which are specified in the configuration file.
	 */
	private void loadPlugins() {	
		// Load preferences to get activated plugins
		List<PluginInfo> pluginsToLoad = new ArrayList<>();
		try {
			File loadconfig = new File("plugins/loadconfig.xml");
			if (loadconfig.exists()) {
				JAXBContext context = JAXBContext.newInstance(PluginInfoList.class);
				Unmarshaller um = context.createUnmarshaller();

				// Reading XML from the file and unmarshalling
				PluginInfoList wrapper = (PluginInfoList) um.unmarshal(loadconfig);
				pluginsToLoad.addAll(wrapper.getPluginInfos());
			}
		} catch (UnmarshalException ex) {
			pluginsToLoad.clear();
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Plugin Manager");
			alert.setContentText("Load configuration is invalid.\n"+
									"No plugins will be loaded.");
			alert.showAndWait();
		} catch (Exception ex) {
			Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		// Load plugins based on config file
		boolean error = false;
		for (PluginInfo info : pluginsToLoad) {
			Stream<Class<Plugin>> stream = availablePlugins.stream().filter(
					clazz -> clazz.getCanonicalName().equals(info.className));
			// Check whether plugin name is unique
			if (stream.count() == 1) {
				try {
					Constructor<Plugin> ctor = stream.findFirst().get().getConstructor(PluginLoader.class);
					Plugin p = ctor.newInstance(this);
					
					// Check whether plugin ID matches
					if (p.getID() == info.id) {
						if (p instanceof MenuPlugin) {
							// There must be at most one menu plugin
							if (menuPlugin == null) {
								loadedPlugins.add(p);
								menuPlugin = (MenuPlugin) p;
							} else {
								error = true;
							}
						} else if (p instanceof ControlPlugin) {
							// There must be at most one control plugin
							if (controlPlugin == null) {
								loadedPlugins.add(p);
								controlPlugin = (ControlPlugin) p;
							} else {
								error = true;
							}
						} else {
							loadedPlugins.add(p);
						}
					} else {
						error = true;
					}
				} catch (Exception ex) {
					Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else {
				error = true;
			}
		}
		if (menuPlugin == null) {
			// Use default plugin
			menuPlugin = new DefaultMenuPlugin(mainApp.getPluginHost());
			loadedPlugins.add(menuPlugin);
		}
		if (controlPlugin == null) {
			// Use default plugin
			controlPlugin = new DefaultControlPlugin(mainApp.getPluginHost());
			loadedPlugins.add(controlPlugin);
		}
		if (error) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Plugin Manager");
			alert.setContentText("An Error occurred while loading some plugins.\n"+
									"The affected plugins will be ignored.");
			alert.showAndWait();
		}
		
		availablePlugins.stream().forEach(c -> System.out.println(c.getCanonicalName()));
		loadedPlugins.stream().forEach(p -> System.out.println(p.getName() + "," + p.getID()));
	}
	
	public void startPlugins() {
		loadedPlugins.stream().forEach(p -> p.start());
	}
	
	public void stopPlugins() {
		loadedPlugins.stream().forEach(p -> p.stop());
	}
	
	public void writeConfig() {
		try {
			File config = new File("plugins/loadconfig.xml");

			JAXBContext context = JAXBContext.newInstance(PluginInfoList.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping the plugins
			List<PluginInfo> infos = new ArrayList<>();
			infos = presetPlugins.stream()
					.filter(cl -> !(DefaultMenuPlugin.class.isAssignableFrom(cl) || DefaultControlPlugin.class.isAssignableFrom(cl)))
					.map(cl -> {
						try {
							Constructor<Plugin> ctor = cl.getConstructor(PluginLoader.class);
							Plugin p = ctor.newInstance(this);
							return new PluginInfo(p.getClass().getCanonicalName(), p.getID());
						} catch(Exception ex) {
							Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
						}
						return null;
					})
					.collect(Collectors.toList());
			if (infos.isEmpty()) {
				if (config.exists()) {
					config.delete();
				}
				return;
			}
			PluginInfoList list = new PluginInfoList();
			list.setPluginInfos(infos);

			// Marshalling and saving XML to the file.
			m.marshal(list, config);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Plugin Manager");
			alert.setContentText("Could not save plugin configuration to file.");

			alert.showAndWait();
		}

	}
	
	public void presetPlugins(List<Class<Plugin>> list) {
		presetPlugins = list;
	}
	
	public MenuPlugin getMenuBar() {
		return menuPlugin;
	}
	
	public ControlPlugin getControl() {
		return controlPlugin;
	}
	
	public List<Class<Plugin>> getAvailableMenus() {
		List<Class<Plugin>> list = availablePlugins.stream()
			.filter(clazz -> MenuPlugin.class.isAssignableFrom(clazz))
			.collect(Collectors.toList());
		return list;
	}
	
	public List<Class<Plugin>> getAvailableControls() {
		List<Class<Plugin>> list = availablePlugins.stream()
			.filter(clazz -> ControlPlugin.class.isAssignableFrom(clazz))
			.collect(Collectors.toList());
		return list;
	}
	
	public List<Class<Plugin>> getAvailableOverlays() {
		List<Class<Plugin>> list = availablePlugins.stream()
			.filter(clazz -> !(MenuPlugin.class.isAssignableFrom(clazz) || ControlPlugin.class.isAssignableFrom(clazz)))
			.collect(Collectors.toList());
		return list;
	}
}
