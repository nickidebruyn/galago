package com.bruynhuis.galago.util;

import com.bruynhuis.galago.app.BaseApplication;

/**
 * The shared system is only for use when you do not have access to the baseApplication.
 * This is a singleton class. Care should be taken when this class is called.
 *
 * @author nidebruyn
 */
public class SharedSystem {
    
    private static SharedSystem instance = null;
    
    private BaseApplication baseApplication;
    
    protected SharedSystem() {
        
    }
    
    /**
     * 
     * @return SharedSystem
     */
    public static SharedSystem getInstance() {
        if (instance == null) {
            instance = new SharedSystem();
        }
        return instance;
    }

    /**
     * Get the BaseApplication at runtime.
     * @return 
     */
    public BaseApplication getBaseApplication() {
        return baseApplication;
    }

    public void setBaseApplication(BaseApplication baseApplication) {
        this.baseApplication = baseApplication;
    }

}
