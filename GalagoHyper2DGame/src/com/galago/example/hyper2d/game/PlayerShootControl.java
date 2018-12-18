/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper2d.game;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.RigidBodyLifeControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class PlayerShootControl extends AbstractControl {

    private Player player;
    private float fireDelay = 0.25f;
    private float cooldownTimer = 0f;
    private boolean shoot;

    public PlayerShootControl(Player player) {
        this.player = player;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (player.getGame().isStarted() && !player.getGame().isPaused() && !player.getGame().isGameOver()) {
            cooldownTimer -= tpf;

            if (shoot && cooldownTimer <= 0) {
                cooldownTimer = fireDelay;
                fireBullet(player.getPosition().add(0, 1.3f, 0));
            }
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
        Sprite sprite = new Sprite("bullet-player", 0.1f, 0.4f);
        sprite.setImage("Textures/bullet.png");
        sprite.setLocalTranslation(position);

        RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 1);
        rbc.setSensor(true);
        rbc.setGravityScale(0);        
        rbc.setPhysicLocation(position);
        sprite.addControl(rbc);
        player.getGame().addBullet(rbc);
        
        sprite.addControl(new RigidBodyLifeControl(100));
        
        rbc.setLinearVelocity(0f, 10f);
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

}
