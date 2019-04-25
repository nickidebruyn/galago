/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class EggScreen extends AbstractEditorScreen {

    public static final String NAME = "egg";

    private Spatial egg;

    @Override
    protected void load() {
        setPreviousScreen(MenuScreen.NAME);
        super.load(); //To change body of generated methods, choose Tools | Templates.
        
        
        egg = baseApplication.getAssetManager().loadModel("Models/egg/egg.j3o");
        egg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        sceneNode.attachChild(egg);
        
        SpatialUtils.addMass(egg, 100);

    }

}
