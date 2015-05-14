/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

import javafx.scene.control.MenuBar;

/**
 *
 * @author Michael Obermüller
 */
public abstract class MenuPlugin extends Plugin {
    protected final MenuBar menu;
    
    public MenuPlugin(PluginHost pluginHost, MenuBar menu){
        super(pluginHost);
        this.menu = menu;
    }
}
