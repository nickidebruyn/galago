/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.terrain;

import com.bruynhuis.galago.util.Timer;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;

/**
 *
 * @author NideBruyn
 */
public class CrateControl extends AbstractControl {

    private Game game;
    private Timer hitTimer = new Timer(100);
    private int hitCount = 0;
    private boolean canHit = true;

    public CrateControl(Game game) {
        this.game = game;

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            hitTimer.update(tpf);
            if (hitTimer.finished()) {
                canHit = true;
                hitTimer.stop();
            }

            if (hitCount >= 1) {
                //Dispose the shape
                game.getBaseApplication().getEffectManager().doEffect("crate-break", spatial.getWorldTranslation().clone());
                game.getBaseApplication().getSoundManager().playSound("crate-break");
                game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(spatial);
                spatial.removeFromParent();
            }

        }


    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void doHit() {

        if (hitCount == 0) {
            hitCount++;
            hitTimer.start();
            canHit = false;            
            game.getBaseApplication().getSoundManager().playSound("glass");
        } else if (canHit) {
            hitCount++;
            
        }
        
    }
}
