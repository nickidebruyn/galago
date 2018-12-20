/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.terrain;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.util.Timer;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;

/**
 *
 * @author NideBruyn
 */
public class DirtControl extends AbstractControl {

    private Game game;
    private Timer hitTimer = new Timer(100);
    private int crackIndex1 = 0;
    private int crackIndex2 = 0;
    private int hitCount = 0;
    private boolean canHit = true;

    public DirtControl(Game game, int crackIndex1, int crackIndex2) {
        this.game = game;
        this.crackIndex1 = crackIndex1;
        this.crackIndex2 = crackIndex2;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            hitTimer.update(tpf);
            if (hitTimer.finished()) {
                canHit = true;
                hitTimer.stop();
            }

            if (hitCount >= 3) {
                //Dispose the shape
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
            ((Sprite) spatial).showIndex(crackIndex1);
            hitCount++;
            hitTimer.start();
            canHit = false;            
        } else if (hitCount == 1) {
            ((Sprite) spatial).showIndex(crackIndex2);
            hitCount++;
            hitTimer.start();
            canHit = false;            
            
        } else if (canHit) {
            hitCount++;
            
        }
        
    }
}
