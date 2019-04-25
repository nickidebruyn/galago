/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.spatial;

import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.spatial.Polygon;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class Gizmo extends Node implements PickListener {
    
    private float radius = 1f;
    
    private Camera camera;
    private TouchPickListener touchPickListener;
    
    private Spatial ringXAxis;
    private Spatial ringYAxis;
    private Spatial ringZAxis;
    
    private Spatial moveXAxisPos;
    private Spatial moveXAxisNeg;
    
    private Spatial moveYAxisPos;
    private Spatial moveYAxisNeg;
    
    private Spatial moveZAxisPos;
    private Spatial moveZAxisNeg;
    
    private Spatial selectedGizmo;
    private Vector3f downClickPos;
    private Vector3f mainGizmoPos;

    public Gizmo(String name, Camera camera, InputManager inputManager) {
        super(name);
        this.camera = camera;
        init();
                
        touchPickListener = new TouchPickListener(name, camera, this);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);
        
    }

    protected void init() {
        
        //Load rings.
        ringXAxis = loadRing("xAxis", ColorRGBA.Blue);
        SpatialUtils.rotateTo(ringXAxis, 0, 0, 90);
        
        ringYAxis = loadRing("yAxis", ColorRGBA.Green);
        SpatialUtils.rotateTo(ringYAxis, -90, 0, 0);
        
        ringZAxis = loadRing("zAxis", ColorRGBA.Red);
        SpatialUtils.rotateTo(ringZAxis, 0, 90, 0);
        
        //Load the movement arrows
        float arrowDis = radius + (radius*0.5f*0.5f) + 0.1f;
        
        moveZAxisPos = loadArrow("zArrowPos", ColorRGBA.Blue);
        SpatialUtils.move(moveZAxisPos, 0, 0 , arrowDis);
        
        moveZAxisNeg = loadArrow("zArrowNeg", ColorRGBA.Blue);
        SpatialUtils.move(moveZAxisNeg, 0, 0 , -arrowDis);
        SpatialUtils.rotateTo(moveZAxisNeg, 0, 180, 0);
        
        moveXAxisPos = loadArrow("xArrowPos", ColorRGBA.Red);
        SpatialUtils.rotateTo(moveXAxisPos, 0, 90, 0);
        SpatialUtils.move(moveXAxisPos, arrowDis, 0, 0);
        
        moveXAxisNeg = loadArrow("xArrowNeg", ColorRGBA.Red);
        SpatialUtils.move(moveXAxisNeg, -arrowDis, 0, 0);
        SpatialUtils.rotateTo(moveXAxisNeg, 0, -90, 0);
        
        
        moveYAxisPos = loadArrow("yArrowPos", ColorRGBA.Green);
        SpatialUtils.rotateTo(moveYAxisPos, -90, 0, 0);
        SpatialUtils.move(moveYAxisPos, 0, arrowDis, 0);
        
        moveYAxisNeg = loadArrow("yArrowNeg", ColorRGBA.Green);
        SpatialUtils.move(moveYAxisNeg, 0, -arrowDis, 0);
        SpatialUtils.rotateTo(moveYAxisNeg, 90, 0, 0);
        
    }
    
    private Spatial loadRing(String gName, ColorRGBA colorRGBA) {
        Polygon pYXis = new Polygon(30, radius, 0.05f);
        Geometry geom = new Geometry(name + gName, pYXis);
        Material m = SpatialUtils.addColor(geom, colorRGBA, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);        
        attachChild(geom);
        SpatialUtils.updateSpatialTransparency(geom, true, 0.3f);
        
        return geom;
        
    }
    
    private Spatial loadArrow(String gName, ColorRGBA colorRGBA) {        
        Spatial geom = SpatialUtils.addCone(this, 20, radius*0.2f, radius*0.5f);
        geom.setName(name + gName);
        Material m = SpatialUtils.addColor(geom, colorRGBA, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);        
        SpatialUtils.updateSpatialTransparency(geom, true, 0.3f);
                
        return geom;
        
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {
        
        if (pickEvent.isKeyDown() && pickEvent.isRightButton() && pickEvent.getContactObject() != null) {
            Debug.log("You clicked on " + pickEvent.getContactObject().getName());
            selectedGizmo = pickEvent.getContactObject();
            SpatialUtils.updateSpatialTransparency(selectedGizmo, true, 1.0f);
            downClickPos = selectedGizmo.getWorldTranslation().clone();
            mainGizmoPos = this.getLocalTranslation().clone();
            
        } else {
            SpatialUtils.updateSpatialTransparency(selectedGizmo, true, 0.8f);
            selectedGizmo = null;
            
        }
        
    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {
        
                
        if (selectedGizmo != null && pickEvent.getContactPoint() != null && pickEvent.isKeyDown() && pickEvent.isRightButton()) {
            
            float dis = pickEvent.getContactPoint().distance(downClickPos);
            Debug.log("Distance = " + dis);
            
            if (selectedGizmo.equals(moveXAxisPos)) {                
                this.setLocalTranslation(mainGizmoPos.x + dis, mainGizmoPos.y, mainGizmoPos.z);
                
            } else if (selectedGizmo.equals(moveXAxisNeg)) {                
                this.setLocalTranslation(mainGizmoPos.x - dis, mainGizmoPos.y, mainGizmoPos.z);
                
            }
            
            if (selectedGizmo.equals(moveYAxisPos)) {
                this.setLocalTranslation(mainGizmoPos.x, mainGizmoPos.y + dis, mainGizmoPos.z);
                
            } else if (selectedGizmo.equals(moveYAxisNeg)) {                
                this.setLocalTranslation(mainGizmoPos.x, mainGizmoPos.y - dis, mainGizmoPos.z);
                
            }
            
            if (selectedGizmo.equals(moveZAxisPos)) {
                this.setLocalTranslation(mainGizmoPos.x, mainGizmoPos.y, mainGizmoPos.z + dis);
                
            } else if (selectedGizmo.equals(moveZAxisNeg)) {                
                this.setLocalTranslation(mainGizmoPos.x, mainGizmoPos.y, mainGizmoPos.z-dis);
                
            }
            
        }
        
    }
    
}
