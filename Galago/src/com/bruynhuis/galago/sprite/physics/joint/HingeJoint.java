/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.joint;

import com.bruynhuis.galago.sprite.physics.Converter;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.Vector3f;
import org.dyn4j.dynamics.joint.Joint;

/**
 *
 * @author nidebruyn
 */
public class HingeJoint extends PhysicsJoint {
    
    protected Vector3f anchor;

    public HingeJoint(RigidBodyControl rigidBodyControl1, RigidBodyControl rigidBodyControl2, Vector3f anchor) {        
        super(rigidBodyControl1, rigidBodyControl2);
        this.anchor = anchor;

    }
    
    @Override
    protected Joint createJoint() {
        org.dyn4j.dynamics.joint.RevoluteJoint revoluteJoint = new org.dyn4j.dynamics.joint.RevoluteJoint(rigidBodyControl1.getBody(), rigidBodyControl2.getBody(), Converter.toVector2(anchor));
        return revoluteJoint;
    }

    public void setLimits(float lowerLimit, float upperLimit) {
        ((org.dyn4j.dynamics.joint.RevoluteJoint)joint).setLimits(lowerLimit, upperLimit);
        
    }
    
    public void setMaximumMotorTorque(float torque) {
        ((org.dyn4j.dynamics.joint.RevoluteJoint)joint).setMaximumMotorTorque(torque);
        
    }
    
    public void setLimitsEnabled(boolean enable) {
        ((org.dyn4j.dynamics.joint.RevoluteJoint)joint).setLimitEnabled(enable);        
    }
    
    public void setMotorEnabled(boolean enable) {
        ((org.dyn4j.dynamics.joint.RevoluteJoint)joint).setMotorEnabled(enable);
    }
    
    public void setCollisionAllowed(boolean allowed) {
        ((org.dyn4j.dynamics.joint.RevoluteJoint)joint).setCollisionAllowed(allowed);
    }
    
    public float getUpperLimit() {
        return (float)((org.dyn4j.dynamics.joint.RevoluteJoint)joint).getUpperLimit();
    }
    
    public float getLowerLimit() {
        return (float)((org.dyn4j.dynamics.joint.RevoluteJoint)joint).getLowerLimit();
    }

    public Vector3f getAnchor() {
        return anchor;
    }

    public void setAnchor(Vector3f anchor) {
        this.anchor = anchor;
    }
    
    
}
