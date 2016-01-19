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
public class RopeJoint extends PhysicsJoint {

    public RopeJoint(RigidBodyControl rigidBodyControl1, RigidBodyControl rigidBodyControl2) {
        super(rigidBodyControl1, rigidBodyControl2);

    }
    
    @Override
    protected Joint createJoint() {
        org.dyn4j.dynamics.joint.RopeJoint ropeJoint = new org.dyn4j.dynamics.joint.RopeJoint(rigidBodyControl1.getBody(), rigidBodyControl2.getBody(), rigidBodyControl1.getBody().getTransform().getTranslation(), rigidBodyControl2.getBody().getTransform().getTranslation());
        return ropeJoint;
    }

}
