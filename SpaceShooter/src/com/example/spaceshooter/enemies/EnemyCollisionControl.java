/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.enemies;

import com.bruynhuis.galago.control.FlickerControl;
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
public class EnemyCollisionControl extends AbstractControl implements PhysicsCollisionListener {
    
    private MainApplication mainApplication;
    private boolean initialized = false;
    private boolean gameover = false;
    private int shield = 1;

    public EnemyCollisionControl(MainApplication mainApplication, int shield) {
        this.mainApplication = mainApplication;
        this.shield = shield;
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
            mainApplication.getEffectManager().doEffect("enemy-explode", spatial.getControl(RigidBodyControl.class).getPhysicLocation().clone(), 100);
            mainApplication.getSoundManager().playSound("enemy-die");
            mainApplication.getDyn4jAppState().getPhysicsSpace().remove(spatial);
            spatial.removeFromParent();
            gameover = false;
            mainApplication.getMessageManager().sendMessage("kill", null);

        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB, Vector3f collisionPoint) {
        if ((spatialA.getName().startsWith("enemy") && spatialB.getName().startsWith("player")) ||
                (spatialA.getName().startsWith("player") && spatialB.getName().startsWith("enemy"))) {
            //Fire the game over event
            if (spatialA.equals(this.getSpatial()) || spatialB.equals(this.getSpatial())) {
                doDamage();
            }
            
        } else 
            
        if ((spatialA.getName().startsWith("enemy") && spatialB.getName().startsWith("bullet")) ||
                (spatialA.getName().startsWith("bullet") && spatialB.getName().startsWith("enemy"))) {
            //Fire the game over event
            if (spatialA.equals(this.getSpatial()) || spatialB.equals(this.getSpatial())) {
                doDamage();
            }
            
        }        
    }
    
    /**
     * Add a hit point to the enemy
     */
    private void doDamage() {
        shield --;
        
        if (shield <= 0) {
            gameover  = true;
            
        } else if (shield == 1) {
            mainApplication.getSoundManager().playSound("shield-down");
            spatial.addControl(new FlickerControl(0.5f));
            
        }
        
        
    }
    
}
