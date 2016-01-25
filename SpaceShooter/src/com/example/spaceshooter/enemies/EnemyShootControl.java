/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.enemies;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.Timer;
import com.example.spaceshooter.MainApplication;
import com.example.spaceshooter.bullet.BulletCollisionControl;
import com.example.spaceshooter.bullet.BulletControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class EnemyShootControl extends AbstractControl {

    private MainApplication mainApplication;
    private Timer shootTimer = new Timer(100);

    public EnemyShootControl(MainApplication mainApplication1, float shootTime) {
        this.mainApplication = mainApplication1;
        shootTimer.setMaxTime(shootTime);
        shootTimer.start();
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        shootTimer.update(tpf);
        
        if (shootTimer.finished()) {
            fireBullet(spatial.getControl(RigidBodyControl.class).getPhysicLocation().subtract(0, 3, 0));
            shootTimer.reset();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * Fire a bullet from a position
     *
     * @param position
     */
    private void fireBullet(Vector3f position) {
        Debug.log("Shoot bullet from: " + position);

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

            sprite.addControl(new BulletControl(mainApplication, -12f));
            sprite.addControl(new BulletCollisionControl(mainApplication));

            mainApplication.getSoundManager().playSound("player-laser");
        }

    }

}
