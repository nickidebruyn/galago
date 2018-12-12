/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.control;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.util.SharedSystem;
import com.bruynhuis.galago.util.Timer;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class SpatialRigidbodyLifeControl extends AbstractControl {

    private Timer timer;

    public SpatialRigidbodyLifeControl(float lifetime) {
        timer = new Timer(lifetime);
        timer.start();
    }

    @Override
    protected void controlUpdate(float tpf) {
        timer.update(tpf);
        if (timer.finished()) {

            //Check for physics
            if (SharedSystem.getInstance().getBaseApplication() instanceof Base3DApplication) {
                Base3DApplication base3DApplication = (Base3DApplication) SharedSystem.getInstance().getBaseApplication();
                RigidBodyControl rigidBodyControl = spatial.getControl(RigidBodyControl.class);

                if (rigidBodyControl != null) {
                    base3DApplication.getBulletAppState().getPhysicsSpace().removeAll(spatial);
                }
            }

            spatial.removeFromParent();
            timer.stop();
        }
    }

    public void start() {
        timer.start();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
