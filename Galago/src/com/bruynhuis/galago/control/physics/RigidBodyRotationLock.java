/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author NideBruyn
 */
public class RigidBodyRotationLock implements PhysicsTickListener {
    
    private RigidBodyControl rigidBodyControl;
    private Vector3f axisLock;

    public RigidBodyRotationLock(RigidBodyControl rigidBodyControl, Vector3f axisLock) {
        this.rigidBodyControl = rigidBodyControl;
        this.axisLock = axisLock;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        rigidBodyControl.setAngularVelocity(rigidBodyControl.getAngularVelocity().multLocal(axisLock.x, axisLock.y, axisLock.z));
//        rigidBodyControl.setPhysicsRotation(rigidBodyControl.getPhysicsRotation().fromAngles(axisLock.x, axisLock.y, axisLock.z));
        if (axisLock.x == 0 && axisLock.y == 0 && axisLock.z == 0) {
            rigidBodyControl.setPhysicsRotation(Quaternion.IDENTITY);
        }
        
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        
        
        
    }
    
}
