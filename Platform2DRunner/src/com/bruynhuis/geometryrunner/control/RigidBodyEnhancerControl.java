/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.control;

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
import com.bruynhuis.geometryrunner.game.Game;

/**
 *
 * @author nidebruyn
 */
public class RigidBodyEnhancerControl extends AbstractControl {

    protected RigidBodyControl rigidBodyControl;
    protected float distance = 2f;
    protected Game game;

    public RigidBodyEnhancerControl(Game game) {
        this.game = game;
    }

    public RigidBodyEnhancerControl(Game game, float distance) {
        this.game = game;
        this.distance = distance;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            //First we get the rigid body
            if (rigidBodyControl == null) {
                rigidBodyControl = spatial.getControl(RigidBodyControl.class);

            } else {
                //Check distance of rigidbody from the player so that we can activate or disable a body
                if (rigidBodyControl.getPhysicLocation().distance(game.getPlayer().getPosition()) <= distance) {
                    rigidBodyControl.setActive(true);
                    rigidBodyControl.setAsleep(false);
                    
                } else {
                    rigidBodyControl.setActive(false);
                    rigidBodyControl.setAsleep(true);
                }
                
            }




        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        return new RigidBodyEnhancerControl(game);

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
