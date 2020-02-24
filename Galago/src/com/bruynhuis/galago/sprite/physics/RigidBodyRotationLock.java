/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics;

/**
 *
 * @author NideBruyn
 */
public class RigidBodyRotationLock implements PhysicsTickListener {

    private RigidBodyControl rigidBodyControl;

    public RigidBodyRotationLock(RigidBodyControl rigidBodyControl) {
        this.rigidBodyControl = rigidBodyControl;

    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        rigidBodyControl.setAngularVelocity(0);
        rigidBodyControl.setPhysicRotation(0);
//        rigidBodyControl.setPhysicsRotation(rigidBodyControl.getPhysicsRotation().fromAngles(axisLock.x, axisLock.y, axisLock.z));

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }

}
