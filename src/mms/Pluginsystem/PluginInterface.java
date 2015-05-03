/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem;

/**
 *
 * @author Michael Obermüller
 */
public interface PluginInterface {
    public boolean start();
    public boolean stop();
    public void setPluginManager(PluginManagerInterface manager);
}
