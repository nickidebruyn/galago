/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.controls;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;

/**
 *
 * @author NideBruyn
 */
public class MushroomControl extends AbstractControl {
    
    private Game game;

    public MushroomControl(Game game) {
        this.game = game;
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
        game.getBaseApplication().getSoundManager().playSound("bounce");
        Tween.to(spatial, SpatialAccessor.SCALE_XYZ, 0.1f)
                .target(1.2f, 1.1f, 1)
                .ease(Circ.INOUT)
                .setCallback(new TweenCallback() {

                    public void onEvent(int i, BaseTween<?> bt) {
                        spatial.setLocalScale(1);
                    }
                })
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
    }
}
