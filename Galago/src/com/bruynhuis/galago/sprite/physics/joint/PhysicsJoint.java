/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.joint;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import org.dyn4j.dynamics.joint.Joint;

/**
 *
 * @author nidebruyn
 */
public abstract class PhysicsJoint {
    
    protected RigidBodyControl rigidBodyControl1;
    protected RigidBodyControl rigidBodyControl2;
    protected Joint joint;

    public PhysicsJoint(RigidBodyControl rigidBodyControl1, RigidBodyControl rigidBodyControl2) {
        this.rigidBodyControl1 = rigidBodyControl1;
        this.rigidBodyControl2 = rigidBodyControl2;
        
    }
    
    protected abstract Joint createJoint();

    public RigidBodyControl getRigidBodyControl1() {
        return rigidBodyControl1;
    }

    public RigidBodyControl getRigidBodyControl2() {
        return rigidBodyControl2;
    }

    public Joint getJoint() {
        if (joint == null) {
            joint = createJoint();
        }
        return joint;
    }
    
}
