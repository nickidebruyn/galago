/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.bullet;

import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.example.spaceshooter.MainApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class BulletCollisionControl extends AbstractControl implements PhysicsCollisionListener {
    
    private MainApplication mainApplication;
    private boolean initialized = false;
    private boolean gameover = false;

    public BulletCollisionControl(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        //add to physics space
        if (!initialized) {
            mainApplication.getDyn4jAppState().getPhysicsSpace().addPhysicsCollisionListener(this);
            initialized = true;
            gameover = false;
        }
        
        //Check if game is over
        if (gameover) {
            mainApplication.getDyn4jAppState().getPhysicsSpace().removePhysicsCollisionListener(this);
            mainApplication.getEffectManager().doEffect("bullet-explode", spatial.getControl(RigidBodyControl.class).getPhysicLocation().clone(), 100);
            mainApplication.getSoundManager().playSound("bullet-die");
            mainApplication.getDyn4jAppState().getPhysicsSpace().remove(spatial);
            spatial.removeFromParent();
            gameover = false;

        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB, Vector3f collisionPoint) {
        if ((spatialA.getName().startsWith("enemy") && spatialB.getName().startsWith("bullet")) ||
                (spatialA.getName().startsWith("bullet") && spatialB.getName().startsWith("enemy"))) {
            //Fire the game over event
            if (spatialA.equals(this.getSpatial()) || spatialB.equals(this.getSpatial())) {
                gameover  = true;
            }
            
        }        
        
    }
    
}
