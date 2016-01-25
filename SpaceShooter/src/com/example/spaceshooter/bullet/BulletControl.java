/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.bullet;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.util.Debug;
import com.example.spaceshooter.MainApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class BulletControl extends AbstractControl {

    private MainApplication mainApplication;
    private float verticalSpeed = 24f;

    public BulletControl(MainApplication mainApplication1, float verticalSpeed) {
        this.mainApplication = mainApplication1;
        this.verticalSpeed = verticalSpeed;
    }

    @Override
    protected void controlUpdate(float tpf) {

        spatial.getControl(RigidBodyControl.class).move(0, verticalSpeed * tpf);

        if (spatial.getControl(RigidBodyControl.class).getPhysicLocation().y >= mainApplication.getLevelHeight() ||
                spatial.getControl(RigidBodyControl.class).getPhysicLocation().y <= -mainApplication.getLevelHeight()) {
            destroy();
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void destroy() {
        //Dispose the bullet
        mainApplication.getDyn4jAppState().getPhysicsSpace().remove(spatial);
        spatial.removeFromParent();
//        Debug.log("Destroy bullet");
    }
}
