/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.controls;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;

/**
 *
 * @author NideBruyn
 */
public class BladeControl extends AbstractControl {

    private RigidBodyControl rigidBodyControl;
    private Game game;
    private Sprite track;
    private Vector3f startPos;
    private float dir = 1f;

    public BladeControl(Game game) {
        this.game = game;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted()) {
            if (rigidBodyControl == null) {
                rigidBodyControl = spatial.getControl(RigidBodyControl.class);                
                startPos = rigidBodyControl.getPhysicLocation().clone();
            }
            
            if (rigidBodyControl != null) {
                rigidBodyControl.move(0, tpf*dir);
                
                if (rigidBodyControl.getPhysicLocation().y >= startPos.y + 2 || 
                        rigidBodyControl.getPhysicLocation().y < startPos.y) {
                    dir *= -1f;
                }
            }
        }

        if (track == null) {
            track = new Sprite("track", 0.1f, 2f);
            track.setImage("Textures/obstacle/blade-track.png");
            track.setLocalTranslation(spatial.getLocalTranslation().add(0, 1f, -0.1f));
            spatial.getParent().attachChild(track);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
