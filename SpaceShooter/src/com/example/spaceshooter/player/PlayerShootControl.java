/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.player;

import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.util.Debug;
import com.example.spaceshooter.MainApplication;
import com.example.spaceshooter.bullet.BulletCollisionControl;
import com.example.spaceshooter.bullet.BulletControl;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class PlayerShootControl extends AbstractControl implements AnalogListener, JoystickListener {

    private MainApplication mainApplication;
    private float fireDelay = 0.25f;
    private float cooldownTimer = 0f;

    public PlayerShootControl(MainApplication mainApplication1) {
        this.mainApplication = mainApplication1;
    }

    @Override
    protected void controlUpdate(float tpf) {
        cooldownTimer -= tpf;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void onAnalog(String name, float value, float tpf) {

        if (name.equals("shoot") && cooldownTimer <= 0) {
            cooldownTimer = fireDelay;
            fireBullet(spatial.getControl(RigidBodyControl.class).getPhysicLocation().clone().add(0, 2, 0));
        }

    }

    /**
     * Fire a bullet from a position
     *
     * @param position
     */
    private void fireBullet(Vector3f position) {
//        Debug.log("Shoot bullet from: " + position);

        if (spatial.getParent() != null) {
            Sprite sprite = new Sprite("bullet", 0.2f, 0.5f);
            sprite.setMaterial(mainApplication.getModelManager().getMaterial("Materials/bullets.j3m"));
            sprite.setLocalTranslation(position);
            spatial.getParent().attachChild(sprite);

            RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(0.2f, 0.5f), 0);
            rbc.setSensor(true);
            rbc.setGravityScale(0);
            rbc.setPhysicLocation(position);
            sprite.addControl(rbc);
            mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);

            sprite.addControl(new BulletControl(mainApplication, 20f));
            sprite.addControl(new BulletCollisionControl(mainApplication));

            mainApplication.getSoundManager().playSound("player-laser");
        }

    }

    public void stick(JoystickEvent joystickEvent, float fps) {
        
        if (joystickEvent.isButton3()) {
            onAnalog("shoot", 1, fps);
        }
        
    }
}
