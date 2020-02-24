/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.tests.screens;

import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.tests.spatial.markerpatch.SplatMarker;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;

/**
 *
 * @author NideBruyn
 */
public class SplatMarkerScreen extends AbstractScreen implements PickListener {
	
    public static final String NAME = "SplatMarkerScreen";
    private Label title;
    
    private Node ground;
    protected Grid grid;
    protected Geometry gridGeom;
    protected Spatial block;
    protected TouchPickListener touchPickListener;
    protected DirectionalLight light;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Screen Title");
        title.centerTop(0, 0);
        
        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    @Override
    protected void load() {        
        
        
//        SpatialUtils.addSkySphere(sceneNode, 2, camera);
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.DarkGray);

        //Load the ground grid
        ground = new Node("ground");
        rootNode.attachChild(ground);

        grid = new Grid(50, 50, 1);
        gridGeom = new Geometry("ground-geom", grid);
        ground.attachChild(gridGeom);
        SpatialUtils.addColor(gridGeom, ColorRGBA.LightGray, true);
        gridGeom.center();
        
        
//        block = SpatialUtils.addBox(rootNode, 1.5f, 1.5f, 1.5f);
//        SpatialUtils.addTexture(block, "Textures/crate.jpg", false);
//        SpatialUtils.move(block, 0, 1.5f, 0);


        block = assetManager.loadModel("Models/rock.j3o");
        rootNode.attachChild(block);
//        SpatialUtils.translate(block, 0,0, 0);
        block.addControl(new RotationControl(10));
        
        
        light = new DirectionalLight(new Vector3f(1, -1, -1), ColorRGBA.White);
        rootNode.addLight(light);
        
        camera.setLocation(new Vector3f(-10, 10, 10));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        
    }

    @Override
    protected void show() {
        touchPickListener.registerWithInput(inputManager);
    }

    @Override
    protected void exit() {
        rootNode.removeLight(light);
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {
        
        if (!pickEvent.isKeyDown() && pickEvent.isLeftButton()) {
            Geometry pickedGeom = pickEvent.getContactObject();
            
            if (pickedGeom != null && pickEvent.getContactPoint() != null) {
                addSplatMarker(pickedGeom, pickEvent.getContactPoint());
                
            }
            
        }
        
    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {
        
    }
    
    protected void addSplatMarker(Geometry collided, Vector3f contactPoint) {
        Spatial splat = SpatialUtils.addSphere(rootNode, 10, 10, 0.1f);
        SpatialUtils.addColor(splat, ColorRGBA.Orange, true);
        SpatialUtils.translate(splat, contactPoint.x, contactPoint.y, contactPoint.z);
        
        
        Geometry splatMarker = SplatMarker.createTargetMarker(collided.getMesh(), contactPoint, assetManager, "Textures/skull.png");
        log("Splat = " + splatMarker);
//        SpatialUtils.translate(splatMarker, contactPoint.x, contactPoint.y, contactPoint.z);
        collided.getParent().attachChild(splatMarker);
//        splatMarker.getMaterial().getAdditionalRenderState().setWireframe(true);
//        splatMarker.setQueueBucket(RenderQueue.Bucket.Translucent);
        
    }
}
