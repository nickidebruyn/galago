/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.control.camera.CameraStickControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.bounding.BoundingSphere;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;

/**
 *
 * @author NideBruyn
 */
public class LightningSky extends AbstractScreen {
    
    private Label header;

    @Override
    protected void init() {
        header = new Label(hudPanel, "Lighting sky");
        header.centerTop(0, 10);
    }

    @Override
    protected void load() {
//        addSkyDome(rootNode, camera);
        SpatialUtils.addSkySphere(rootNode, 1, camera);
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        
        Spatial b = SpatialUtils.addSphere(rootNode, 20, 30, 2);
//        SpatialUtils.addColor(b, ColorRGBA.Green, false);
        SpatialUtils.slerp(b, 0, 360, 0, 5, 0, true);
        SpatialUtils.addSunLight(rootNode, ColorRGBA.White);
        
        b.setMaterial(assetManager.loadMaterial("Materials/lava.j3m"));
                
        camera.setLocation(new Vector3f(0, 10, 20));
        camera.lookAt(b.getWorldTranslation(), Vector3f.UNIT_Y);
    }
    
    public Spatial addSkyDome(Node parent, Camera camera) {

        Dome dome = new Dome(Vector3f.ZERO, 11, 20, 100, true);
        Geometry sky = new Geometry("sky", dome);
//        sky.setQueueBucket(RenderQueue.Bucket.Sky);
        sky.setCullHint(Spatial.CullHint.Never);
        sky.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));
        sky.addControl(new CameraStickControl(camera));

        Material m = baseApplication.getAssetManager().loadMaterial("Materials/lightning.j3m");
//        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        sky.setMaterial(m);

//        rotate(sky, -90, 0, 0);

        parent.attachChild(sky);

        return sky;

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
    
}
