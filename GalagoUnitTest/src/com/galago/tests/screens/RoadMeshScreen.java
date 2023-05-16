/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.input.Input;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.spatial.Road;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
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
    private Mesh road;
    private Node controlPointsNode;
    private Spatial floor;
    private TouchPickListener touchPickListener;
    private TouchButton clearRoadButton;
    private TouchButton generateRoadButton;
    private Spatial last;
    private boolean painting = false;
    
    private DirectionalLight sunlight;

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

        touchPickListener = new TouchPickListener("my", camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    @Override
    protected void load() {
        
        sunlight = new DirectionalLight(new Vector3f(0.6f, -0.7f, -0.5f), ColorRGBA.White);
        rootNode.addLight(sunlight);
        
        controlPointsNode = new Node("control points");
        rootNode.attachChild(controlPointsNode);

        floor = SpatialUtils.addBox(rootNode, 50, 0.1f, 50);
        floor.setName(FLOOR);
        SpatialUtils.addColor(floor, ColorRGBA.Brown, true);

        camera.setLocation(new Vector3f(0, 80, -10));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

    }

    private void generateRoad() {
        System.out.println("Generate road");

        if (roadGeometry != null && roadGeometry.getParent() != null) {
            roadGeometry.removeFromParent();

        }

        if (controlPointsNode.getQuantity() >= 2) {

            Spline spline = new Spline();

            //Get all the points
            ArrayList<Vector3f> controlPoints = new ArrayList<Vector3f>();
            for (int i = 0; i < controlPointsNode.getChildren().size(); i++) {
                Spatial child = controlPointsNode.getChild(i);
                controlPoints.add(child.getWorldTranslation());
                spline.addControlPoint(child.getWorldTranslation());
            }

            Vector3f[] vertices = new Vector3f[controlPoints.size()];
            controlPoints.toArray(vertices);

            //Generate the road from the points
            road = new Road(2f, controlPointsNode.getChildren());
//            road = new Curve(spline, 10);

//            
//            System.out.println("VERTICES: " +vertices);
//            
//            road = RoadGenerator.generateRoad(vertices.length, vertices);
//            road = new Disk(30, 4);
            roadGeometry = new Geometry("road", road);
            Material roadMaterial = SpatialUtils.addTexture(roadGeometry, "Textures/road.jpg", false);
//            roadMaterial.getAdditionalRenderState().setWireframe(true);
            roadMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            roadGeometry.setMaterial(roadMaterial);
            roadGeometry.move(0, 0.2f, 0);
            rootNode.attachChild(roadGeometry);

        }

    }

    private void clearRoad() {
        if (roadGeometry != null && roadGeometry.getParent() != null) {
            roadGeometry.removeFromParent();
            controlPointsNode.detachAllChildren();
            last = null;
        }
    }

    @Override
    protected void show() {
        touchPickListener.registerWithInput(inputManager);
    }

    @Override
    protected void exit() {
        rootNode.removeLight(sunlight);
        touchPickListener.unregisterInput();
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive()) {

            if (pickEvent.isKeyDown() && pickEvent.isLeftButton() && pickEvent.getContactObject() != null
                    && pickEvent.getCursorPosition().y > 80f) {
                if (pickEvent.getContactObject().getName().equals(FLOOR)) {
                    clearRoad();
                    painting = true;
                    Spatial point = SpatialUtils.addBox(controlPointsNode, 1, 0.01f, 0.01f);
                    SpatialUtils.addColor(point, ColorRGBA.Red, true);
                    point.setLocalTranslation(pickEvent.getContactPoint().clone().setY(0.3f));
                    point.setLocalScale(3);
                    last = point;

                }
            } else if (!pickEvent.isKeyDown() && pickEvent.isLeftButton()) {
                painting = false;
                
            }
                    

        }
    }

    public void drag(PickEvent pickEvent, float tpf) {

        if (painting && pickEvent.getContactObject() != null) {
            if (pickEvent.getContactObject().getName().equals(FLOOR)) {

                if (last != null && pickEvent.getContactPoint().distance(last.getWorldTranslation()) > 6) {
                    last.lookAt(pickEvent.getContactPoint().clone(), Vector3f.UNIT_Y);
                    Spatial point = last.clone(false);
                    point.setLocalTranslation(pickEvent.getContactPoint().clone().setY(0.3f));
                    controlPointsNode.attachChild(point);
                    last = point;

                }

            }
        }

    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            if (last != null) {
                if (Input.get("left_arrow") > 0) {
                    System.out.println("Left");
                    last.rotate(0, tpf * 0.5f, 0);

                }
                if (Input.get("right_arrow") > 0) {
                    System.out.println("Left");
                    last.rotate(0, -tpf * 0.5f, 0);

                }

            }

        }
    }

}
