/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.vehicle;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.Vector3f;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author nidebruyn
 */
public class Vehicle {

    protected RigidBodyControl vehicleBodyControl;
    protected RigidBodyControl frontWheelControl;
    protected RigidBodyControl rearWheelControl;
    protected WheelJoint frontWheelJoint;
    protected WheelJoint rearWheelJoint;
    protected float speed = 0f;
    protected float maxSpeed = 20;
    protected float acceleration = 0.1f;
    protected float decceleration = 0.5f;
    protected boolean allWheelDrive = false;

    public Vehicle(RigidBodyControl vehicleBodyControl, RigidBodyControl frontWheelControl, RigidBodyControl rearWheelControl, boolean allWheelDrive) {
        this.vehicleBodyControl = vehicleBodyControl;
        this.frontWheelControl = frontWheelControl;
        this.rearWheelControl = rearWheelControl;
        this.allWheelDrive = allWheelDrive;

        initVehicle();
    }

    protected void initVehicle() {
        frontWheelJoint = new WheelJoint(vehicleBodyControl.getBody(), frontWheelControl.getBody(), frontWheelControl.getBody().getWorldCenter(), new Vector2(0.0, 1.0));
        frontWheelJoint.setCollisionAllowed(false);
        frontWheelJoint.setFrequency(8.0); // setup a spring
        frontWheelJoint.setDampingRatio(0.4);
        frontWheelJoint.setMotorEnabled(allWheelDrive); // give the car rear-wheel-drive        
        frontWheelJoint.setMotorSpeed(0); // set the speed to -180 degrees per second        
        frontWheelJoint.setMaximumMotorTorque(50); // don't forget to set the maximum torque

        rearWheelJoint = new WheelJoint(vehicleBodyControl.getBody(), rearWheelControl.getBody(), rearWheelControl.getBody().getWorldCenter(), new Vector2(0.0, 1.0));
        rearWheelJoint.setCollisionAllowed(false);
        rearWheelJoint.setFrequency(8.0); // setup a spring
        rearWheelJoint.setDampingRatio(0.4);
        rearWheelJoint.setMotorEnabled(true); // give the car rear-wheel-drive        
        rearWheelJoint.setMotorSpeed(0); // set the speed to -180 degrees per second        
        rearWheelJoint.setMaximumMotorTorque(50); // don't forget to set the maximum torque
    }

    protected void log(String text) {
        System.out.println(text);
    }
    
    public void setMaximumMotorTorque(float torque) {
        rearWheelJoint.setMaximumMotorTorque(torque);
        frontWheelJoint.setMaximumMotorTorque(torque);
    }
    
//    public void setSpringTension(float tension) {
//        rearWheelJoint.setFrequency(tension);
//        frontWheelJoint.setFrequency(tension);
//    }

    /**
     * Move vehicle forward
     */
    public void forward() {
        if (speed < 0) {
            speed = speed + (acceleration*2f);
        } else {
            speed = speed + acceleration;
        }
        
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }

        updateMotorSpeed();

    }

    /**
     * Move vehicle backward
     */
    public void reverse() {
        if (speed > 0) {
            speed = speed - (acceleration*2f);
        } else {
            speed = speed - acceleration;
        }
        
        if (speed < -maxSpeed) {
            speed = -maxSpeed;
        }
        
        updateMotorSpeed();
    }

    public void brake() {
        if (speed > 0) {
            reverse();
        } else if (speed < 0) {
            forward();
        } else {
            speed = 0f;
        }
        updateMotorSpeed();

    }
    
    protected void updateMotorSpeed() {
        rearWheelJoint.setMotorSpeed(speed); // set the speed to -180 degrees per second        
        frontWheelJoint.setMotorSpeed(speed); // set the speed to -180 degrees per second                
    }

    public WheelJoint getFrontWheelJoint() {
        return frontWheelJoint;
    }

    public WheelJoint getRearWheelJoint() {
        return rearWheelJoint;
    }

    public RigidBodyControl getVehicleBodyControl() {
        return vehicleBodyControl;
    }

    public RigidBodyControl getFrontWheelControl() {
        return frontWheelControl;
    }

    public RigidBodyControl getRearWheelControl() {
        return rearWheelControl;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public float getDecceleration() {
        return decceleration;
    }

    public void setDecceleration(float decceleration) {
        this.decceleration = decceleration;
    }
        
    public void setPhysicsLocation(Vector3f location) {
        vehicleBodyControl.setPhysicLocation(location);
    }
    
    public Vector3f getPhysicsLocation() {
        return vehicleBodyControl.getPhysicLocation();
    }
    
    public void clearForces() {
        vehicleBodyControl.clearForces();
        frontWheelControl.clearForces();
        rearWheelControl.clearForces();
    }
}
