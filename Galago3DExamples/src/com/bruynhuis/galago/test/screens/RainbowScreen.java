/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.test.path.RainbowPath;
import com.bruynhuis.galago.ui.Label;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Nidebruyn
 */
public class RainbowScreen extends AbstractScreen {
    
    private RainbowPath rainbowPath;
    private Spatial spatial;

    @Override
    protected void init() {
        
        Label heading = new Label(hudPanel, "Rainbow screen test", 30, 400, 50);
        heading.centerTop(0, 10);
        
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        
        spatial = baseApplication.getAssetManager().loadModel("Models/particles/rainbow.j3o");
        spatial.setLocalTranslation(-20, 0, 0);
        rootNode.attachChild(spatial);
        
        rainbowPath = new RainbowPath(rootNode, baseApplication);
        
        camera.setLocation(new Vector3f(0, 0, 35f));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    @Override
    protected void show() {
        rainbowPath.start(spatial, 5);
    }

    @Override
    protected void exit() {
        
    }

    @Override
    protected void pause() {
        
    }
    
}
