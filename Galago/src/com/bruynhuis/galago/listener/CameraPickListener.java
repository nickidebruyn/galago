/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.listener;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author NideBruyn
 */
public class CameraPickListener implements ActionListener {

    public static final String PICK_ACTION_LEFT = "cam_picker_left";
    public static final String PICK_ACTION_RIGHT = "cam_picker_right";
    private Camera cam;
    private Node scene;
    private Vector3f contactPoint;
    private Geometry contactObject;
    private PickListener pickListener;
    private CollisionResults results;
    private Ray ray;
    private InputManager inputManager;
    private float yOffset = 0;
    private boolean enabled = true;
    private PickEvent pickEvent;

    public CameraPickListener(Camera cam, Node scene) {
        this.cam = cam;
        this.scene = scene;
        results = new CollisionResults();
        ray = new Ray(cam.getLocation(), cam.getDirection());
        pickEvent = new PickEvent();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public void onAction(String name, boolean keyPressed, float tpf) {
        results.clear();

        if (enabled) {
            if (name.equals(PICK_ACTION_LEFT) || name.equals(PICK_ACTION_RIGHT)) {

                // 2. Aim the ray from cam loc to cam direction.        
                ray.setOrigin(cam.getLocation().add(0, yOffset, 0));
                ray.setDirection(cam.getDirection());

                // 3. Collect intersections between Ray and Shootables in results list.
                scene.collideWith(ray, results);

                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    contactPoint = closest.getContactPoint();
                    contactObject = closest.getGeometry();



                    if (pickListener != null) {
                        pickEvent.setContactObject(contactObject);
                        pickEvent.setContactPoint(contactPoint);
                        pickEvent.setCursorPosition(inputManager != null ? inputManager.getCursorPosition() : null);
                        pickEvent.setKeyDown(keyPressed);
                        pickEvent.setLeft(false);
                        pickEvent.setRight(false);
                        pickEvent.setDown(false);
                        pickEvent.setUp(false);
                        pickEvent.setAnalogValue(0);
                        if (name.equals(PICK_ACTION_LEFT)) {
                            pickEvent.setLeftButton(true);
                            pickEvent.setRightButton(false);
                        } else {
                            pickEvent.setLeftButton(false);
                            pickEvent.setRightButton(true);
                        }

                        pickListener.picked(pickEvent, tpf);
                    }

                } else {
                    contactPoint = null;
                    contactObject = null;

                }
            }
        }


    }

    public void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;
        this.inputManager.addMapping(PICK_ACTION_LEFT, new MouseButtonTrigger(0));
        this.inputManager.addMapping(PICK_ACTION_RIGHT, new MouseButtonTrigger(1));
        this.inputManager.addListener(this, PICK_ACTION_LEFT, PICK_ACTION_RIGHT);
    }

    public void unregisterInput() {

        if (inputManager == null) {
            return;
        }

        inputManager.deleteMapping(PICK_ACTION_LEFT);
        inputManager.deleteMapping(PICK_ACTION_RIGHT);

        inputManager.removeListener(this);
        inputManager.setCursorVisible(true);

    }

    public PickListener getPickListener() {
        return pickListener;
    }

    public void setPickListener(PickListener pickListener) {
        this.pickListener = pickListener;
    }

    public boolean hasContact() {
        return contactObject != null;
    }

    public Geometry getContactObject() {
        return contactObject;
    }

    public Vector3f getContactPoint() {
        return contactPoint;
    }
}
