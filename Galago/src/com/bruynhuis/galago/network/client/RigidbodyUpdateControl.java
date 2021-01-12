/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.client;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class RigidbodyUpdateControl extends AbstractControl {

    private Vector3f targetPosition;
    private Quaternion targetRotation;
    private RigidBodyControl rbc;

    @Override
    protected void controlUpdate(float tpf) {

        if (rbc == null) {
            rbc = spatial.getControl(RigidBodyControl.class);

        } else {
            if (targetPosition != null && targetPosition.distance(rbc.getPhysicLocation()) < 2f) {
                rbc.setPhysicLocation(rbc.getPhysicLocation().interpolateLocal(targetPosition, 0.4f));

            } else if (targetPosition != null) {
                rbc.setPhysicLocation(targetPosition);
            }

            if (targetRotation != null) {
                spatial.setLocalRotation(targetRotation.slerp(spatial.getLocalRotation(), targetRotation, 0.4f));

            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Vector3f getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Vector3f targetPosition) {
        this.targetPosition = targetPosition;
    }

    public Quaternion getTargetRotation() {
        return targetRotation;
    }

    public void setTargetRotation(Quaternion targetRotation) {
        this.targetRotation = targetRotation;
    }

}
