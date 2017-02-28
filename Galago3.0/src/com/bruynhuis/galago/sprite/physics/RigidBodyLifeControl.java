/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics;

import com.bruynhuis.galago.util.Timer;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author nidebruyn
 */
public class RigidBodyLifeControl extends AbstractControl {

    private float life = 10f;
    private float lifeCounter = 0;
    private Timer timer;
    private boolean started = false;
    
    /**
     * Time is in miliseconds
     * @param life 
     */
    public RigidBodyLifeControl(float life) {
        this.life = life;
        this.timer = new Timer(life);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (timer != null && spatial != null) {
            
            //Start the timer
            if (!started) {
                timer.start();
                started = true;
            }
            
            //update the timer
            timer.update(tpf);
            
            //Check if finished
            if (timer.finished()) {
                RigidBodyControl bodyControl = spatial.getControl(RigidBodyControl.class);
                if (bodyControl != null) {
                    bodyControl.getPhysicsSpace().remove(bodyControl);
                }
                
                Node parentNode = spatial.getParent();                                
                spatial.removeFromParent();
                timer.stop();

                //Refactor the batch
                if (parentNode != null && parentNode instanceof BatchNode) {
                    ((BatchNode)parentNode).batch();
                }
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
