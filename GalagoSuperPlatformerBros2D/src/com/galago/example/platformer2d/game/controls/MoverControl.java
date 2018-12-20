/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.controls;

import com.bruynhuis.galago.util.Debug;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.game.Player;

/**
 *
 * @author NideBruyn
 */
public class MoverControl extends AbstractControl {
    
    private Game game;
    private Vector3f direction;
    private float detectDistance = 0.5f;

    public MoverControl(Game game, Vector3f direction) {
        this.game = game;
        this.direction = direction;
    }
    
    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {
            //TODO
            
        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    public void doHit() {
//        game.getBaseApplication().getSoundManager().playSound("bounce");
//        ((Player)game.getPlayer()).transportToPosition(spatial.getWorldTranslation().clone());
        ((Player)game.getPlayer()).setMoverActive(direction);
        Debug.log("Mover hit on direction: " + direction);
        
        
    }
}
