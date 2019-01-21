/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.listener;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * The touchpicklistener can be used by a user to detect when a touch or left
 * mouse click has occured and also if the touch was a drag action.
 *
 * @author NideBruyn
 */
public class TouchPickListener implements ActionListener, AnalogListener {

    public static final String PICK_ACTION_LEFT = "touch_picker_left";
    public static final String PICK_ACTION_RIGHT = "touch_picker_right";
    public static final String DRAG_LEFT_ACTION = "drag_left_picker";
    public static final String DRAG_RIGHT_ACTION = "drag_right_picker";
    public static final String DRAG_UP_ACTION = "drag_up_picker";
    public static final String DRAG_DOWN_ACTION = "drag_down_picker";
    public static final String ZOOM_UP_ACTION = "zoom_up_picker";
    public static final String ZOOM_DOWN_ACTION = "zoom_down_picker";

    private Camera cam;
    private Node scene;
    private Vector3f contactPoint;
    private Vector3f contactNormal;
    private Geometry contactObject;
    private PickListener pickListener;
    private CollisionResults results;
    private Ray ray;
    private InputManager inputManager;
    private boolean enabled = true;
    private boolean keyDown = false;
    private PickEvent pickEvent;
    private BaseApplication baseApplication;

    public TouchPickListener(Camera cam, Node scene) {
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

    /**
     * This is a private helper method which will help determine where the mouse
     * pointer collision happened.
     *
     */
    private void checkCursorCollision() {

        results.clear();

        // 1. calc direction
        Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0f);
        Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 1f);
        direction.subtractLocal(origin).normalizeLocal();
        
//        if (scene.getQueueBucket().equals(RenderQueue.Bucket.Gui)) {
//            origin = new Vector3f(inputManager.getCursorPosition().x, inputManager.getCursorPosition().y, 10f);
//            direction = new Vector3f(0, 0, -10f);
//        }

        // 2. Aim the ray from cam loc to cam direction.        
        ray.setOrigin(origin);
        ray.setDirection(direction);

        // 3. Collect intersections between Ray and Shootables in results list.
        scene.collideWith(ray, results);

        // 5. Use the results (we mark the hit object)
        if (results.size() > 0) {
            // The closest collision point is what was truly hit:
//            if (baseApplication.getCurrentScreen().getWindow().isButtonTriggered()) {
//                contactPoint = null;
//                contactObject = null;
//                
//            } else {
                CollisionResult closest = results.getClosestCollision();
                contactPoint = closest.getContactPoint();
                contactNormal = closest.getContactNormal();
                contactObject = closest.getGeometry();
                
//            }
            

        } else {
            contactPoint = null;
            contactNormal = null;
            contactObject = null;

        }
    }

    public void onAction(String name, boolean keyPressed, float tpf) {

        if (enabled) {
            keyDown = keyPressed;
            baseApplication = SharedSystem.getInstance().getBaseApplication();

            if (name.equals(PICK_ACTION_LEFT) || name.equals(PICK_ACTION_RIGHT)) {

                checkCursorCollision();

                if (contactObject != null && pickListener != null) {

                    if (name.equals(PICK_ACTION_LEFT)) {
                        pickEvent.setLeftButton(true);
                        pickEvent.setRightButton(false);
                    } else {
                        pickEvent.setLeftButton(false);
                        pickEvent.setRightButton(true);
                    }
                    
                    pickEvent.setContactObject(contactObject);
                    pickEvent.setContactPoint(contactPoint);
                    pickEvent.setContactNormal(contactNormal);
                    pickEvent.setCursorPosition(inputManager.getCursorPosition());
                    pickEvent.setKeyDown(keyPressed);
                    pickEvent.setLeft(false);
                    pickEvent.setRight(false);
                    pickEvent.setDown(false);
                    pickEvent.setUp(false);
                    pickEvent.setZoomDown(false);
                    pickEvent.setZoomUp(false);
                    
                    pickEvent.setAnalogValue(0);
                    pickListener.picked(pickEvent, tpf);
                }

            } else {
                keyDown = false;
            }
        } else {
            keyDown = false;
        }
    }

    public void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;
        this.inputManager.addMapping(PICK_ACTION_LEFT, new MouseButtonTrigger(0));
        this.inputManager.addMapping(PICK_ACTION_RIGHT, new MouseButtonTrigger(1));
        this.inputManager.addMapping(DRAG_RIGHT_ACTION, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        this.inputManager.addMapping(DRAG_UP_ACTION, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        this.inputManager.addMapping(DRAG_LEFT_ACTION, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        this.inputManager.addMapping(DRAG_DOWN_ACTION, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        this.inputManager.addMapping(ZOOM_UP_ACTION, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        this.inputManager.addMapping(ZOOM_DOWN_ACTION, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        this.inputManager.addListener(this, PICK_ACTION_LEFT, PICK_ACTION_RIGHT, 
                DRAG_RIGHT_ACTION, DRAG_DOWN_ACTION, DRAG_LEFT_ACTION, 
                DRAG_UP_ACTION, ZOOM_UP_ACTION, ZOOM_DOWN_ACTION);
    }

    public void unregisterInput() {

        if (inputManager == null) {
            return;
        }

        inputManager.deleteMapping(PICK_ACTION_LEFT);
        inputManager.deleteMapping(PICK_ACTION_RIGHT);

        inputManager.deleteMapping(DRAG_DOWN_ACTION);
        inputManager.deleteMapping(DRAG_LEFT_ACTION);
        inputManager.deleteMapping(DRAG_RIGHT_ACTION);
        inputManager.deleteMapping(DRAG_UP_ACTION);
        inputManager.deleteMapping(ZOOM_UP_ACTION);
        inputManager.deleteMapping(ZOOM_DOWN_ACTION);

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

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (enabled && pickListener != null) {
            baseApplication = SharedSystem.getInstance().getBaseApplication();
            
            //Now we call the drag listener
            checkCursorCollision();
            
            pickEvent.setCursorPosition(inputManager.getCursorPosition());
            pickEvent.setKeyDown(keyDown);
            pickEvent.setAnalogValue(value);
            pickEvent.setLeft(false);
            pickEvent.setRight(false);
            pickEvent.setDown(false);
            pickEvent.setUp(false);
            pickEvent.setZoomDown(false);
            pickEvent.setZoomUp(false);

            if (DRAG_RIGHT_ACTION.equals(name)) {
                pickEvent.setRight(true);

            }
            if (DRAG_UP_ACTION.equals(name)) {
                pickEvent.setUp(true);

            }
            if (DRAG_LEFT_ACTION.equals(name)) {
                pickEvent.setLeft(true);

            }
            if (DRAG_DOWN_ACTION.equals(name)) {
                pickEvent.setDown(true);

            }
            if (ZOOM_UP_ACTION.equals(name)) {
                pickEvent.setZoomUp(true);

            }
            if (ZOOM_DOWN_ACTION.equals(name)) {
                pickEvent.setZoomDown(true);

            }

            if (contactObject != null) {
                pickEvent.setContactObject(contactObject);
                pickEvent.setContactPoint(contactPoint);
                pickEvent.setContactNormal(contactNormal);

            } else {
                pickEvent.setContactObject(null);
                pickEvent.setContactPoint(null);
                pickEvent.setContactNormal(null);
            }

            pickListener.drag(pickEvent, tpf);
        }

    }
}
