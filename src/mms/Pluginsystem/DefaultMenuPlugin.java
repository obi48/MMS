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
public class DefaultMenuPlugin extends MenuPlugin{

    public DefaultMenuPlugin(PluginHost pluginHost, MenuBar menu) {
        super(pluginHost, menu);
    }

    @Override
    public boolean start() {
        System.out.println("MenuPlugin started");
        return true;
    }

    @Override
    public boolean stop() {
        System.out.println("MenuPlugin stopped");
        return true;
    }

    @Override
    public String getDeveloper() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
