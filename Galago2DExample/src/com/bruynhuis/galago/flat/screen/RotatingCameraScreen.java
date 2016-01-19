/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;

/**
 *
 * @author Nidebruyn
 */
public class RotatingCameraScreen extends AbstractScreen {
    
    private Sprite sprite;
    private CameraNode cameraNode;

    @Override
    protected void init() {

    }

    @Override
    protected void load() {
        
        sprite = new Sprite("testsprite", 5, 5);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/smileyf.j3m"));
        rootNode.attachChild(sprite);
        
        cameraNode = new CameraNode("camnode", camera);
        cameraNode.setLocalTranslation(0, 0, 10);
        cameraNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        rootNode.attachChild(cameraNode);

    }

    @Override
    protected void show() {

    }

    @Override
    protected void exit() {
        rootNode.detachAllChildren();

    }

    @Override
    protected void pause() {

    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            cameraNode.rotate(0, 0, tpf* FastMath.DEG_TO_RAD*150f);
                        
        }
    }
    
}
