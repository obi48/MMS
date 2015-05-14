/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms.Pluginsystem.Impl;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
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
        System.out.println("MenuPlugin started");

        FadeTransition fade = new FadeTransition(Duration.seconds(1), menu);
        fade.setFromValue(0);
        fade.setToValue(0);
        fade.playFromStart();
        
        menu.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            fade.setDelay(Duration.seconds(0));
            fade.setFromValue(1);
            fade.setToValue(1);
            fade.playFromStart();
        });
        
        menu.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            fade.setDelay(Duration.seconds(3));
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.playFromStart();
        });
        return true;
    }

    @Override
    public boolean stop() {
        System.out.println("MenuPlugin stopped");
        return true;
    }

    @Override
    public String getDeveloper() {
        return "MMS_Team";
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
