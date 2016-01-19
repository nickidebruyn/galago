/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.resource;

import java.util.HashMap;
import java.util.Map;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.screen.AbstractScreen;

/**
 * This class will manage the screens and cache them.
 * @author NideBruyn
 */
public class ScreenManager {

    private BaseApplication application;
    private Map<String, AbstractScreen> screens = new HashMap<String, AbstractScreen>();

    public ScreenManager(BaseApplication simpleApplication) {
        this.application = simpleApplication;
    }

    public void destroy() {
        screens.clear();
    }

    /**
     * Must be called to cash screens that wants to be loaded in the system.
     *
     * @param screen
     */
    public void loadScreen(String screenName, AbstractScreen abstractScreenState) {
        application.getStateManager().attach(abstractScreenState);
        screens.put(screenName, abstractScreenState);

    }

    /**
     * Called when a screen needs to be retrieved. This will return the screen
     *
     * @param screenName
     * @return
     */
    public AbstractScreen getScreen(String screenName) {
        return screens.get(screenName);
    }

    public Map<String, AbstractScreen> getScreens() {
        return screens;
    }
    
    
}
