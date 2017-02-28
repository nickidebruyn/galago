/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author NideBruyn
 */
public class FlickerControl extends AbstractControl {

    private float life = 10f;
    private float lifeCounter = 0;
    
    public FlickerControl(float life) {
        this.life = life;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //Let the ball idle for a while
        if (lifeCounter < life) {            
            lifeCounter = lifeCounter + (10*tpf);
            return;
        } else {
            lifeCounter = 0;            
            if (!spatial.getCullHint().equals(Spatial.CullHint.Always)) {
                spatial.setCullHint(Spatial.CullHint.Always);
                
            } else if (spatial.getCullHint().equals(Spatial.CullHint.Always)) {
                spatial.setCullHint(Spatial.CullHint.Never);
                
            }

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
