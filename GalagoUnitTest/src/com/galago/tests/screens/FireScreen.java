/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.util.ParticleUtils;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author nidebruyn
 */
public class FireScreen extends AbstractScreen {
    
    private Spatial fireScene;
    private Spatial floor;
    private CameraNode cameraNode;
    private Node cameraJointNode;

    @Override
    protected void init() {
        
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        
        floor = SpatialUtils.addBox(rootNode, 10, 0.1f, 10);
        SpatialUtils.addColor(floor, ColorRGBA.DarkGray, true);
        
        fireScene = ParticleUtils.addFire(rootNode, new Box(1, 0.1f, 1));
        fireScene.scale(0.5f);
        
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
