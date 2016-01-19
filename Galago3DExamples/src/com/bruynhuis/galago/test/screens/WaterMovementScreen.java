/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.control.effects.FlowControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.Debug;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author nidebruyn
 */
public class WaterMovementScreen extends AbstractScreen {

    private Spatial waterNode;
    private FlyByCamera flyByCamera;

    @Override
    protected void init() {
        TouchButton button = new TouchButton(hudPanel, "button", "Some button");
        button.centerBottom(0, 0);
        button.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Debug.log("Bla bla clicked");
            }
        });
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.White);

        rootNode.attachChild(createLine(Vector3f.ZERO, new Vector3f(0, 5, 0), ColorRGBA.Red, 5f));

        waterNode = createWater(10, 10);
        waterNode.scale(5);
        rootNode.attachChild(waterNode);

        flyByCamera = new FlyByCamera(camera);
        flyByCamera.registerWithInput(inputManager);
        flyByCamera.setMoveSpeed(10);

    }

    private Spatial createWater(float width, float height) {
        Node node = new Node("waternode");

        Quad quad = new Quad(width, height);
        Geometry geometry = new Geometry("waterplane", quad);
        geometry.rotate(-FastMath.DEG_TO_RAD*90, 0, 0);
        quad.scaleTextureCoordinates(new Vector2f(width, height));
        node.attachChild(geometry);
        geometry.center();
        FlowControl flowControl = new FlowControl("Textures/water.jpg", 0.1f, 0.1f);
        node.addControl(flowControl);

        return node;
    }
    
    private Spatial createLine(Vector3f start, Vector3f end, ColorRGBA colorRGBA, float thickness) {
        Line line = new Line(start, end);
        line.setLineWidth(thickness);
        Geometry geometry = new Geometry("Line", line);
        Material material = baseApplication.getAssetManager().loadMaterial("Common/Materials/WhiteColor.j3m");
        material.setColor("Color", colorRGBA);
        geometry.setMaterial(material);
        return geometry;
    }

    @Override
    protected void show() {
        camera.setLocation(new Vector3f(0, 5, 20));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);


    }

    @Override
    protected void exit() {
        rootNode.detachAllChildren();
        flyByCamera.unregisterInput();
    }

    @Override
    protected void pause() {
    }
}
