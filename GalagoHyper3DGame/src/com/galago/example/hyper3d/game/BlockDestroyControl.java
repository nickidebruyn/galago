/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper3d.game;

import com.bruynhuis.galago.util.Debug;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class BlockDestroyControl extends AbstractControl {
    
    private Game game;
    private Player player;

    public BlockDestroyControl(Game game) {
        this.game = game;
        
    }        

    @Override
    protected void controlUpdate(float tpf) {
        
        if (game.isStarted() && !game.isPaused()) {
            
            if (player == null) {
                player = (Player)game.getPlayer();
            }
            
            if (player.getPosition().x > spatial.getWorldTranslation().x && 
                    player.getPosition().distance(spatial.getWorldTranslation()) > game.getDistanceBetweenSections()) {
                
                doDestroy();                
            }
            
        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    public void doDestroy() {
//        Debug.log("Destroy block at " + spatial.getWorldTranslation());
        game.getBaseApplication().getBulletAppState().getPhysicsSpace().remove(spatial.getControl(RigidBodyControl.class));
        spatial.removeFromParent();        
    }
}
