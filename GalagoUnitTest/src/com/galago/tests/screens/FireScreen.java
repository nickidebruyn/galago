/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class FireScreen extends AbstractScreen {
    
    private Spatial fireScene;
    private CameraNode cameraNode;
    private Node cameraJointNode;

    @Override
    protected void init() {
        
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        
        fireScene = baseApplication.getAssetManager().loadModel("Scenes/scene1.j3o");
        rootNode.attachChild(fireScene);
        
        cameraJointNode = new Node("camjoint");
        rootNode.attachChild(cameraJointNode);
        cameraJointNode.addControl(new RotationControl(10));
        
        cameraNode = new CameraNode("camnode", camera);
        cameraNode.setLocalTranslation(0, 6, -12);
        cameraNode.rotate(FastMath.DEG_TO_RAD*20f, 0, 0);
        cameraJointNode.attachChild(cameraNode);
    }

    @Override
    protected void show() {
        
    }

    @Override
    protected void exit() {
        
    }

    @Override
    protected void pause() {
        
    }
    
}
