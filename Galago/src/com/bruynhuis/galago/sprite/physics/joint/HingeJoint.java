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

}
