/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.enemies;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.example.spaceshooter.MainApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class EnemyMovementControl extends AbstractControl {

    private MainApplication mainApplication;
    private float verticalSpeed = 10f;
    private float horizontalSpeed = 0f;
    private float hSpeed = 0f;
        
    public EnemyMovementControl(MainApplication mainApplication, float verticalSpeed, float horizontalSpeed) {
        this.mainApplication = mainApplication;
        this.verticalSpeed = verticalSpeed;
        this.horizontalSpeed = horizontalSpeed;
        this.hSpeed = horizontalSpeed;
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        //Movement type strait forward
        spatial.getControl(RigidBodyControl.class).move(hSpeed * tpf * mainApplication.getLevelSpeed(), -verticalSpeed * tpf * mainApplication.getLevelSpeed());

        //Check if the ship must turn
        if (horizontalSpeed != 0f) {
            if (spatial.getControl(RigidBodyControl.class).getPhysicLocation().x <= -mainApplication.getLevelWidth()) {
                hSpeed = horizontalSpeed;
                
            } else if (spatial.getControl(RigidBodyControl.class).getPhysicLocation().x >= mainApplication.getLevelWidth()) {
                hSpeed = -horizontalSpeed;
                
            }
            
        }
        
        if (spatial.getControl(RigidBodyControl.class).getPhysicLocation().y <= -(mainApplication.getLevelHeight() + 5f)) {
            spatial.getControl(RigidBodyControl.class).setPhysicLocation(spatial.getControl(RigidBodyControl.class).getPhysicLocation().x, mainApplication.getLevelHeight() + 5f);
            spatial.getControl(RigidBodyControl.class).clearForces();
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
