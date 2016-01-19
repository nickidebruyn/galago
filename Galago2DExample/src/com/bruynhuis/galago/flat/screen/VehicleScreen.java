/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.vehicle.Vehicle;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.math.Vector3f;

/**
 *
 * @author nidebruyn
 */
public class VehicleScreen extends AbstractSpriteScreen {

    private Vehicle vehicle;
    private ControlButton forwardButton;
    private ControlButton reverseButton;
    private TouchButton resetPosition;
    private boolean forward = false;
    private boolean reverse = false;

    @Override
    protected void init() {
        super.init();
        
        resetPosition = new TouchButton(hudPanel, "reset", "Reset Car");
        resetPosition.rightTop(600, 5);
        resetPosition.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {                    
                    vehicle.clearForces();
                    vehicle.setPhysicsLocation(Vector3f.ZERO);
                }
            }
        });

        forwardButton = new ControlButton(hudPanel, "forward", window.getWidth() * 0.3f, 70f);
        forwardButton.rightBottom(0, 0);
        forwardButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    forward = true;
                    reverse = false;
                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    forward = false;
                    reverse = false;
                }
            }
        });

        reverseButton = new ControlButton(hudPanel, "reverse", window.getWidth() * 0.3f, 70f);
        reverseButton.leftBottom(0, 0);
        reverseButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && mainApplication.getDyn4jAppState().isEnabled()) {
                    forward = false;
                    reverse = true;
                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && mainApplication.getDyn4jAppState().isEnabled()) {
                    forward = false;
                    reverse = false;
                }
            }
        });


    }

    @Override
    protected void load() {
        super.load();
        vehicle = addCar(new Vector3f(0f, 0f, 0f));
    }

    protected Vehicle addCar(Vector3f position) {
        log("Adding a car");
        float scale = 0.045f;
        float width = 80f;
        float height = 24f;
        float wheelRadius = 9f;
        float wheelXOffset = 21f;
        float wheelYOffset = 16f;

        Sprite carbody = new Sprite("CARBODY", width * scale, height * scale);
        carbody.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/carbody.j3m"));
        RigidBodyControl carbodyControl = new RigidBodyControl(new BoxCollisionShape(width * scale, height * scale), 1000f);
        carbody.addControl(carbodyControl);
        ((Base2DApplication) baseApplication).getDyn4jAppState().getPhysicsSpace().add(carbodyControl);
        carbodyControl.setPhysicLocation(position);

        final Sprite carfrontwheel = new Sprite("CARWHEELFRONT", wheelRadius * scale * 2f, wheelRadius * scale * 2f);
        carfrontwheel.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/carwheel.j3m"));
        final RigidBodyControl carfrontwheelControl = new RigidBodyControl(new CircleCollisionShape(wheelRadius * scale), 50f);
        carfrontwheel.addControl(carfrontwheelControl);
        ((Base2DApplication) baseApplication).getDyn4jAppState().getPhysicsSpace().add(carfrontwheelControl);
        carfrontwheelControl.setFriction(0.7f);
        carfrontwheelControl.setPhysicLocation(new Vector3f(position.x + wheelXOffset * scale, position.y - wheelYOffset * scale, position.z));
        rootNode.attachChild(carfrontwheel);

        Sprite carrearwheel = new Sprite("CARWHEELREAR", wheelRadius * scale * 2f, wheelRadius * scale * 2f);
        carrearwheel.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/carwheel.j3m"));
        RigidBodyControl carrearwheelControl = new RigidBodyControl(new CircleCollisionShape(wheelRadius * scale), 50f);
        carrearwheel.addControl(carrearwheelControl);
        ((Base2DApplication) baseApplication).getDyn4jAppState().getPhysicsSpace().add(carrearwheelControl);
        carrearwheelControl.setPhysicLocation(new Vector3f(position.x - wheelXOffset * scale, position.y - wheelYOffset * scale, position.z));
        carrearwheelControl.setFriction(0.7f);
        rootNode.attachChild(carrearwheel);

        rootNode.attachChild(carbody);

        Vehicle car = new Vehicle(carbodyControl, carfrontwheelControl, carrearwheelControl, true);
        car.setMaxSpeed(30f);
        car.setAcceleration(0.35f);
        car.setDecceleration(0.05f);
        mainApplication.getDyn4jAppState().getPhysicsSpace().addVehicle(car);

        return car;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (isActive()) {
            if (mainApplication.getDyn4jAppState().isEnabled() && selectedSpatial == null) {
                //moving the vehicle
                if (forward) {
                    vehicle.forward();
                } else if (reverse) {
                    vehicle.reverse();
                } else {
                    vehicle.brake();
                }
                
                camera.setLocation(new Vector3f(vehicle.getPhysicsLocation().x, 0, 0));
            } else {
                vehicle.brake();
            }
        }
    }

    @Override
    protected String getHeading() {
        return "Vehicle Test";
    }

    @Override
    protected String getInstructions() {
        return "Touch the left or right side of the screen to start driving the vehicle.";
    }
}
