/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.test.terraingame.TerrainGame;
import com.bruynhuis.galago.test.terraingame.TerrainGamePlayer;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;

/**
 *
 * @author nidebruyn
 */
public class ShadedTerrainScreen extends AbstractScreen implements PickListener {

    private TerrainGame game;
    private TerrainGamePlayer player;
    private TouchPickListener touchPickListener;
    private Node cameraJointNode;
    private CameraNode cameraNode;
    private float cameraHeight = 24f;
    private float cameraHeightZFactor = 1f;
    private float dragSpeed = 30f;
    private float angleY = FastMath.DEG_TO_RAD * 25f;

    @Override
    protected void init() {
    }

    @Override
    protected void load() {

        game = new TerrainGame(baseApplication, rootNode, false);
        game.load();

        player = new TerrainGamePlayer(game);
        player.load();

        //load camera
        loadCameraSettings();

        //Init the picker listener
        touchPickListener = new TouchPickListener(baseApplication.getCamera(), rootNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);
    }

    @Override
    protected void show() {
        game.start(player);
    }

    @Override
    protected void exit() {
        touchPickListener.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
    }

    protected void loadCameraSettings() {
        Vector3f centerPoint = new Vector3f(0, 0, 0);

        cameraJointNode = new Node("camerajoint");
        cameraJointNode.setLocalTranslation(centerPoint);
        cameraJointNode.rotate(0, angleY, 0);
        rootNode.attachChild(cameraJointNode);

        cameraNode = new CameraNode("camnode", camera);
        cameraNode.setLocalTranslation(0, cameraHeight, cameraHeight * cameraHeightZFactor);
        cameraNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cameraJointNode.attachChild(cameraNode);
    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (pickEvent.isKeyDown() && pickEvent.getContactObject() != null) {
            log("Picked: " + pickEvent.getContactObject().getName());
        }
    }

    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive() && pickEvent.isKeyDown()) {

            if (pickEvent.isRight()) {
                cameraJointNode.rotate(0, -pickEvent.getAnalogValue() * dragSpeed * 0.5f, 0);
//                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(-pickEvent.getAnalogValue() * dragSpeed));

            } else if (pickEvent.isUp()) {
                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(pickEvent.getAnalogValue() * dragSpeed));

            } else if (pickEvent.isLeft()) {
                cameraJointNode.rotate(0, pickEvent.getAnalogValue() * dragSpeed * 0.5f, 0);
//                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(pickEvent.getAnalogValue() * dragSpeed));

            } else if (pickEvent.isDown()) {
                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(-pickEvent.getAnalogValue() * dragSpeed));

            }

            cameraNode.lookAt(cameraJointNode.getWorldTranslation(), Vector3f.UNIT_Y);

        }

        if (isActive()) {
            if (pickEvent.isZoomUp()) {
//                log("zoom up = " + pickEvent.getAnalogValue());
                cameraHeight += pickEvent.getAnalogValue();

            } else if (pickEvent.isZoomDown()) {
//                log("zoom down = " + pickEvent.getAnalogValue());
                cameraHeight -= pickEvent.getAnalogValue();
            }

            cameraNode.setLocalTranslation(0, cameraHeight, cameraHeight * cameraHeightZFactor);
        }
    }
}
