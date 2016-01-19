/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.control.physics;

import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author NideBruyn
 */
public class RigidBodyLifeControl extends AbstractControl {

    private float life = 10f;
    private float lifeCounter = 0;
    private BulletAppState bulletAppState;
    
    public RigidBodyLifeControl(BulletAppState bulletAppState, float life) {
        this.bulletAppState = bulletAppState;
        this.life = life;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //Let the ball idle for a while
        if (lifeCounter < life) {            
            lifeCounter = lifeCounter + (10*tpf);
            return;
        } else {
//            System.out.println("Removing...." + spatial.getName());
            bulletAppState.getPhysicsSpace().remove(spatial);
            spatial.removeFromParent();
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
