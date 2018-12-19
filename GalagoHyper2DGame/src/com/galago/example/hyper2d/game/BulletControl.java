/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper2d.game;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class BulletControl extends AbstractControl {

    private Game game;
    private RigidBodyControl rbc;
    private Sprite sprite;
    private boolean destroy = false;
    private ColorRGBA targetColor;

    public BulletControl(Game game) {
        this.game = game;

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            if (rbc == null) {
                rbc = spatial.getControl(RigidBodyControl.class);
                sprite = (Sprite) spatial;

            } else if (destroy) {
                destroyBullet();
                
            } else {
                if (rbc.getPhysicLocation().y >= Game.OUTOFSCREENHEIGHT) {
                    doDamage(ColorRGBA.Black);
                }
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    protected void destroyBullet() {
        game.getBaseApplication().getEffectManager().prepareColor(new ColorRGBA(targetColor.r, targetColor.g, targetColor.b, 1f), 
                new ColorRGBA(targetColor.r, targetColor.g, targetColor.b, 0.1f));
        game.getBaseApplication().getEffectManager().doEffect("obstacle-hit", rbc.getPhysicLocation().clone());
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rbc);
        spatial.removeFromParent();

    }

    public void doDamage(ColorRGBA targetColor) {
        destroy = true;
        this.targetColor = targetColor;

    }

    public boolean isAlive() {
        return !destroy;
    }
}
