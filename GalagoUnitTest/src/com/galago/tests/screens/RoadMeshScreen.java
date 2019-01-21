/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.spatial.Road;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 *
 * @author NideBruyn
 */
public class RoadMeshScreen extends AbstractScreen implements PickListener {
    
    private static final String FLOOR = "floor";

    private Geometry roadGeometry;
    private Road road;
    private Node controlPointsNode;
    private Spatial floor;
    private TouchPickListener touchPickListener;
    private TouchButton clearRoadButton;
    private TouchButton generateRoadButton;

    @Override
    protected void init() {
        
        clearRoadButton = new TouchButton(hudPanel, "clearRoadButton-button", "Clear Points");
        clearRoadButton.rightBottom(300, 10);
        clearRoadButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    clearRoad();
                }
            }
            
        });
        
        generateRoadButton = new TouchButton(hudPanel, "generate-button", "Generate Road");
        generateRoadButton.rightBottom(10, 10);
        generateRoadButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    generateRoad();
                }
            }
            
        });

        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    @Override
    protected void load() {

        controlPointsNode = new Node("control points");
        rootNode.attachChild(controlPointsNode);
        
        floor = SpatialUtils.addBox(rootNode, 50, 0.1f, 50);
        floor.setName(FLOOR);
        SpatialUtils.addColor(floor, ColorRGBA.Brown, true);

        camera.setLocation(new Vector3f(0, 30, -50));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

    }

    private void generateRoad() {
        
        if (roadGeometry != null && roadGeometry.getParent() != null) {
            roadGeometry.removeFromParent();

        }
        
        if (controlPointsNode.getQuantity() > 3) {
            
            //Get all the points
            ArrayList<Vector3f> controlPoints = new ArrayList<Vector3f>();            
            for (int i = 0; i < controlPointsNode.getQuantity(); i++) {
                Spatial child = controlPointsNode.getChild(i);
                controlPoints.add(child.getWorldTranslation());                
            }
            
            //Generate the road from the points
            road = new Road(1f, 1f, controlPoints);
            roadGeometry = new Geometry("road", road);
            Material roadMaterial = assetManager.loadMaterial("Materials/road.j3m");
            roadMaterial.getAdditionalRenderState().setWireframe(true);
            roadGeometry.setMaterial(roadMaterial);
            rootNode.attachChild(roadGeometry);

        }

    }

    private void clearRoad() {
        if (roadGeometry != null && roadGeometry.getParent() != null) {
            roadGeometry.removeFromParent();
            controlPointsNode.detachAllChildren();
        }
    }

    @Override
    protected void show() {
        touchPickListener.registerWithInput(inputManager);
    }

    @Override
    protected void exit() {
        touchPickListener.unregisterInput();
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive()) {
            
            if (pickEvent.isKeyDown() && pickEvent.isLeftButton() && pickEvent.getContactObject() != null
                    && pickEvent.getCursorPosition().y > 100f) {
                if (pickEvent.getContactObject().getName().equals(FLOOR)) {                    
                    Spatial point = SpatialUtils.addSphere(controlPointsNode, 10, 10, 1f);
                    SpatialUtils.addColor(point, ColorRGBA.Red, true);
                    point.setLocalTranslation(pickEvent.getContactPoint().clone().setY(0.3f));
                }
            }
            
        }
    }

    public void drag(PickEvent pickEvent, float tpf) {
    }

}
