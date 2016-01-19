/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.control.effects.WaveControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;

/**
 *
 * @author nidebruyn
 */
public class WaterWaveScreen extends AbstractScreen {

    private Spatial waterNode;
    private FlyByCamera flyByCamera;

    @Override
    protected void init() {
        TouchButton button = new TouchButton(hudPanel, "button_wave", "Back");
        button.centerBottom(0, 0);
        button.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                showPreviousScreen();
            }
        });
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);

        rootNode.attachChild(createLine(Vector3f.ZERO, new Vector3f(0, 5, 0), ColorRGBA.Red, 5f));

        waterNode = createWater();
        waterNode.scale(5);
        rootNode.attachChild(waterNode);

        rootNode.attachChild(createCube(new Vector3f(0, 0, 0)));

        flyByCamera = new FlyByCamera(camera);
        flyByCamera.registerWithInput(inputManager);
        flyByCamera.setMoveSpeed(10);

    }

    private Spatial createWater() {
        Spatial spatial = baseApplication.getAssetManager().loadModel("Models/water/waterflat.j3o");
        spatial.setQueueBucket(RenderQueue.Bucket.Transparent);
        WaveControl waveControl = new WaveControl("Textures/water.png", 3, 2, 0.04f);
        spatial.addControl(waveControl);
        waveControl.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);

        waveControl.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        
        return spatial;
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

    private Spatial createCube(Vector3f position) {
        /* A colored lit cube. Needs light source! */
        Box boxMesh = new Box(1f, 1f, 1f);
        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
        Material mat = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        boxGeo.setMaterial(mat);
        boxGeo.setLocalTranslation(position);
        
        return boxGeo;
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
