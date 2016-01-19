/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import static com.bruynhuis.galago.games.tilemap.TileMapGame.BLANK;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.test.blockbuilder.BlockBuilderGame;
import com.bruynhuis.galago.test.blockbuilder.BlockBuilderGamePlayer;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author nidebruyn
 */
public class BlockBuilderScreen extends AbstractScreen implements PickListener {

    private BlockBuilderGame game;
    private BlockBuilderGamePlayer player;
    private TouchPickListener touchPickListener;
    private Node cameraJointNode;
    private CameraNode cameraNode;
    private float cameraHeight = 10f;
    private float cameraHeightZFactor = 0.75f;
    private float dragSpeed = 30f;
    private float angleY = FastMath.DEG_TO_RAD * 25f;
    private Spatial marker;
    private Spatial surface;
    private float surfaceSize = 100;
    private boolean dragged = false;

    @Override
    protected void init() {
    }

    /**
     * Create the surface quad
     *
     * @return
     */
    protected void initSurface() {
        Quad quad = new Quad(surfaceSize, surfaceSize);
        Geometry geometry = new Geometry(BLANK, quad);
        geometry.setName(BLANK);
        quad.scaleTextureCoordinates(new Vector2f(surfaceSize, surfaceSize));
        Material material = baseApplication.getAssetManager().loadMaterial("Materials/tile-blank.j3m");
        geometry.setMaterial(material);
        geometry.rotate(FastMath.DEG_TO_RAD * 90, 0, 0);
        surface = geometry;
        rootNode.attachChild(surface);
        surface.move(-surfaceSize * 0.5f + 0.5f, 0f, -surfaceSize * 0.5f + 0.5f);
    }

    protected void initMarker() {
        marker = new Node("marker");
        Material mat = baseApplication.getAssetManager().loadMaterial("Common/Materials/RedColor.j3m");
        WireBox box = new WireBox(0.5f, 0.5f, 0.5f);
        box.setLineWidth(3);
        Geometry g = new Geometry(BLANK, box);
        g.setMaterial(mat);
        g.setLocalTranslation(0, 0.5f, 0);
        ((Node) marker).attachChild(g);
        rootNode.attachChild(marker);
    }

    @Override
    protected void load() {

        game = new BlockBuilderGame(baseApplication, rootNode, false);
        game.load();

        player = new BlockBuilderGamePlayer(game);
        player.load();

        //load camera
        loadCameraSettings();

        //Init surface
        initSurface();

        //Load the marker
        initMarker();

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
        marker.removeFromParent();
        surface.removeFromParent();
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
        if (pickEvent.isKeyDown()) {
            log("Key down");
        }
        if (!pickEvent.isKeyDown() && pickEvent.getContactObject() != null && !dragged) {
            log("Key up: " + pickEvent.getContactObject().getName());

            if (pickEvent.isLeftButton()) {
                game.addObject(BLANK, marker.getWorldTranslation());
            }

            if (pickEvent.isRightButton()) {
                if (pickEvent.getContactObject().getParent().getParent() != null
                        && pickEvent.getContactObject().getParent().getParent().getName().equals(BlockBuilderGame.TERRAIN)) {
                    pickEvent.getContactObject().getParent().getParent().removeFromParent();
                }

            }
            dragged = false;
        }
    }

    public void drag(PickEvent pickEvent, float tpf) {
        dragged = true;
        log("dragged");
        
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

        } else {
            if (pickEvent.getContactPoint() != null) {
                log("Object=" + pickEvent.getContactObject().getName());
                if (pickEvent.getContactObject().getName().equals(BLANK)) {
                    marker.setLocalTranslation((int) pickEvent.getContactPoint().x, 0, (int) pickEvent.getContactPoint().z);
                } else {
                    marker.setLocalTranslation((int) pickEvent.getContactPoint().x, pickEvent.getContactObject().getWorldTranslation().y + 1f, (int) pickEvent.getContactPoint().z);
                }


            }
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
