/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.galago.example.fps.screens;

import com.bruynhuis.galago.control.camera.CameraMovementControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.button.TouchStick;
import com.bruynhuis.galago.ui.listener.TouchStickAdapter;
import com.jme3.input.FlyByCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen {

    private TouchStick moveTouchStick;
    private TouchStick lookTouchStick;
    private Image crosshair;
    private Node scene;
    private CameraMovementControl cameraMovementControl;
    private FlyByCamera flyByCamera;
    private float movePercentage = 0.5f;
    private float lookPercentage = 0.025f;

    @Override
    protected void init() {

        moveTouchStick = new TouchStick(hudPanel, "movement-stick", 300, 300);
        moveTouchStick.leftBottom(0, 0);
        moveTouchStick.addTouchStickListener(new TouchStickAdapter() {
            @Override
            public void doLeft(float x, float y, float distance) {
                cameraMovementControl.setStrafeSpeed(distance * movePercentage);
                cameraMovementControl.setStrafeLeft(true);
                cameraMovementControl.setStrafeRight(false);

            }

            @Override
            public void doRight(float x, float y, float distance) {
                cameraMovementControl.setStrafeSpeed(distance * movePercentage);
                cameraMovementControl.setStrafeLeft(false);
                cameraMovementControl.setStrafeRight(true);

            }

            @Override
            public void doUp(float x, float y, float distance) {
                cameraMovementControl.setMoveSpeed(distance * movePercentage);
                cameraMovementControl.setForward(true);
                cameraMovementControl.setBackward(false);

            }

            @Override
            public void doDown(float x, float y, float distance) {
                cameraMovementControl.setMoveSpeed(distance * movePercentage);
                cameraMovementControl.setForward(false);
                cameraMovementControl.setBackward(true);

            }

            @Override
            public void doRelease(float x, float y) {
                cameraMovementControl.setMoveSpeed(0);
                cameraMovementControl.setForward(false);
                cameraMovementControl.setBackward(false);
                cameraMovementControl.setStrafeLeft(false);
                cameraMovementControl.setStrafeRight(false);

            }
        });


        lookTouchStick = new TouchStick(hudPanel, "look-stick", 300, 300);
        lookTouchStick.rightBottom(0, 0);
        lookTouchStick.addTouchStickListener(new TouchStickAdapter() {
            @Override
            public void doLeft(float x, float y, float distance) {
                cameraMovementControl.setLookSpeed(distance * lookPercentage);
                cameraMovementControl.setLeft(true);
                cameraMovementControl.setRight(false);

            }

            @Override
            public void doRight(float x, float y, float distance) {
                cameraMovementControl.setLookSpeed(distance * lookPercentage);
                cameraMovementControl.setLeft(false);
                cameraMovementControl.setRight(true);

            }

            @Override
            public void doUp(float x, float y, float distance) {
                cameraMovementControl.setLookSpeed(distance * lookPercentage);
                cameraMovementControl.setUp(true);
                cameraMovementControl.setDown(false);

            }

            @Override
            public void doDown(float x, float y, float distance) {
                cameraMovementControl.setLookSpeed(distance * lookPercentage);
                cameraMovementControl.setUp(false);
                cameraMovementControl.setDown(true);

            }

            @Override
            public void doRelease(float x, float y) {
                cameraMovementControl.setLookSpeed(0);
                cameraMovementControl.setLeft(false);
                cameraMovementControl.setRight(false);
                cameraMovementControl.setUp(false);
                cameraMovementControl.setDown(false);

            }
        });


        crosshair = new Image(hudPanel, "Interface/crosshair.png", 100, 100, true);
        crosshair.center();

        if ( !baseApplication.isMobileApp()) {
            flyByCamera = new FlyByCamera(camera);
            flyByCamera.setMoveSpeed(60);
            flyByCamera.setRotationSpeed(4);
        }


    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(new ColorRGBA(75f/255f, 119f/255f, 157f/255f, 1));

        scene = (Node) baseApplication.getAssetManager().loadModel("Scenes/scene1.j3o");
        rootNode.attachChild(scene);

        camera.setLocation(new Vector3f(0, 60, 0));

        cameraMovementControl = new CameraMovementControl(camera);
        cameraMovementControl.setStrafeSpeed(0);
        cameraMovementControl.setMoveSpeed(0);
        cameraMovementControl.setLookSpeed(0);


        if (flyByCamera != null) {
            flyByCamera.registerWithInput(inputManager);
        }
    }

    @Override
    protected void show() {
        scene.addControl(cameraMovementControl);

    }

    @Override
    protected void exit() {
        if (flyByCamera != null) {
            flyByCamera.unregisterInput();
        }
        
        scene.removeFromParent();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            camera.getLocation().setY(100);
        }
    }
}
