/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.player;

import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.example.spaceshooter.MainApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class PlayerCollisionControl extends AbstractControl implements PhysicsCollisionListener {
    
    private MainApplication mainApplication;
    private boolean initialized = false;
    private boolean gameover = false;

    public PlayerCollisionControl(MainApplication mainApplication) {
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
            mainApplication.getEffectManager().doEffect("player-explode", spatial.getControl(RigidBodyControl.class).getPhysicLocation().clone(), 100);
            mainApplication.getSoundManager().playSound("player-die");
            mainApplication.getDyn4jAppState().getPhysicsSpace().remove(spatial);
            spatial.removeFromParent();
            gameover = false;
            mainApplication.getMessageManager().sendMessage("gameover", null);
        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB) {
        if ((spatialA.getName().startsWith("enemy") && spatialB.getName().startsWith("player")) ||
                (spatialA.getName().startsWith("player") && spatialB.getName().startsWith("enemy"))) {
            //Fire the game over event
            gameover  = true;
            
        } else if ((spatialA.getName().startsWith("bullet") && spatialB.getName().startsWith("player")) ||
                (spatialA.getName().startsWith("player") && spatialB.getName().startsWith("bullet"))) {
            //Fire the game over event
            gameover  = true;
        }
        
        
    }
    
}
