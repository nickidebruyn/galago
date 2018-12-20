/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.terrain;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import com.galago.example.platformer2d.game.Game;

/**
 *
 * @author nidebruyn
 */
public class VerticalMoveControl extends AbstractControl {

    protected RigidBodyControl rigidBodyControl;
    protected float startY;
    protected float distance = 4f;
    protected float speed = 2f;
    protected Game game;
    protected boolean startMovingUp = true;

    public VerticalMoveControl(Game game) {
        this.game = game;
    }
    
    public VerticalMoveControl(Game game, float distance, boolean startMovingUp) {
        this.game = game;
        this.distance = distance;
        this.startMovingUp = startMovingUp;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            //First we get the rigid body
            if (rigidBodyControl == null) {
                rigidBodyControl = spatial.getControl(RigidBodyControl.class);

                if (rigidBodyControl != null) {
                    startY = rigidBodyControl.getPhysicLocation().y;                    
                }

            } else {
                
                if (startMovingUp) {
                    if ((speed > 0 && rigidBodyControl.getPhysicLocation().y >= (startY + distance)) ||
                        (speed < 0 && rigidBodyControl.getPhysicLocation().y <= startY)) {
                        speed *= -1f;
                    }
                    
                } else {
                    if ((speed < 0 && rigidBodyControl.getPhysicLocation().y <= (startY - distance)) ||
                        (speed > 0 && rigidBodyControl.getPhysicLocation().y >= startY)) {
                        speed *= -1f;
                    }
                    
                }

                if (game.getPlayer().getPosition().y > (rigidBodyControl.getPhysicLocation().y+0.6f)) {
                    rigidBodyControl.setActive(true);
                } else {
                    rigidBodyControl.setActive(false);
                }

                rigidBodyControl.setPhysicLocation(rigidBodyControl.getPhysicLocation().add(0, tpf * speed, 0));
                
            }

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        return new VerticalMoveControl(game);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
