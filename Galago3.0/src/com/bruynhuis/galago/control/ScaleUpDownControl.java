/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class ScaleUpDownControl extends AbstractControl {

    private float maxScale = 1f;
    private float speed = 1;
    private boolean scaleUp = true;
    
    public ScaleUpDownControl(float maxScale, float speed) {
        this.maxScale = maxScale;
        this.speed = speed;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (scaleUp) {
            if (spatial.getLocalScale().x >= maxScale) {
                scaleUp = false;
                spatial.setLocalScale(maxScale);
                
            } else {
                spatial.setLocalScale(spatial.getLocalScale().x + (tpf*speed));
            }
            
        } else {
            if (spatial.getLocalScale().x <= 1f) {
                scaleUp = true;
                spatial.setLocalScale(1f);
            } else {
                spatial.setLocalScale(spatial.getLocalScale().x - (tpf*speed));
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

}
