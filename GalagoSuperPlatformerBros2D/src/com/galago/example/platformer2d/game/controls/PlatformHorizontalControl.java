/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.controls;

import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;

/**
 *
 * @author NideBruyn
 */
public class PlatformHorizontalControl extends AbstractControl {

    private RigidBodyControl rigidBodyControl;
    private Game game;
    private Sprite track;
    private Sprite gear;
    private Vector3f startPos;
    private float dir = 1f;
    private RotationControl rotationControl;

    public PlatformHorizontalControl(Game game) {
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
                rigidBodyControl.move(tpf * dir, 0);

                if (rigidBodyControl.getPhysicLocation().x >= startPos.x + 2
                        || rigidBodyControl.getPhysicLocation().x < startPos.x) {
                    dir *= -1f;
                    
                }
                
                if (dir > 0) {
                    rotationControl.setRotator(new Vector3f(0, 0, -200));
                } else if (dir < 0) {
                    rotationControl.setRotator(new Vector3f(0, 0, 200));
                }
            }
        }

        if (track == null) {
            track = new Sprite("track", 0.1f, 3f);
            track.setImage("Textures/obstacle/blade-track.png");
            track.setLocalTranslation(spatial.getLocalTranslation().add(1f, 0f, -0.1f));
            track.rotate(0, 0, 90 * FastMath.DEG_TO_RAD);
            spatial.getParent().attachChild(track);
            track.addControl(new SpatialRemoveListenerControl(spatial));

            gear = new Sprite("platform-gear", 0.5f, 0.5f);
            gear.setImage("Textures/other/gear.png");
            rotationControl = new RotationControl(new Vector3f(0, 0, -200));
            gear.addControl(rotationControl);
            ((Node)spatial).attachChild(gear);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
