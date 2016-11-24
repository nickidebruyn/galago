/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.app;

import com.jme3.bullet.BulletAppState;

/**
 * If you wish to make a full 3D game then this call must be extended.
 * You also need to tell the class if you intend to use physics.
 * The physics engine used will be jBullet.
 * 
 * @author nidebruyn
 */
public abstract class Base3DApplication extends BaseApplication {
    
    protected BulletAppState bulletAppState;

    public Base3DApplication(String title, float width, float height, String gameSaveFileName, String gameFont, String splashImage, boolean resizable) {
        super(title, width, height, gameSaveFileName, gameFont, splashImage, resizable);
    }
    
    public Base3DApplication(String title, float width, float height, String gameSaveFileName, String gameFont, String splashImage, boolean resizable, float widthSample, float heightSample) {
        super(title, width, height, gameSaveFileName, gameFont, splashImage, resizable, widthSample, heightSample);
    }
    
    @Override
    protected void initPhysics() {        
        //Don't load if it already exist
        if (bulletAppState != null) {
            return;
        }
        /**
         * Set up Physics
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
//        bulletAppState.getPhysicsSpace().setAccuracy(1f/80f);
//        bulletAppState.getPhysicsSpace().setMaxSubSteps(2);
    }
    
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    @Override
    public void showDebuging() {
        if (bulletAppState != null) {
            bulletAppState.getPhysicsSpace() .enableDebug(assetManager);
        }
    }

}
