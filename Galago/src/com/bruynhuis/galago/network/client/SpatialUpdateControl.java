/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.client;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class SpatialUpdateControl extends AbstractControl {
    
    private Vector3f targetPosition;
    private Quaternion targetRotation;

    @Override
    protected void controlUpdate(float tpf) {
        
        if (targetPosition != null) {
            spatial.setLocalTranslation(spatial.getLocalTranslation().interpolateLocal(targetPosition, 0.4f));
            
        }
        
        if (targetRotation != null) {
            spatial.setLocalRotation(targetRotation.slerp(spatial.getLocalRotation(), targetRotation, 0.4f));

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
