/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.sprytile;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class SprytilePlayerShootControl extends AbstractControl {

    private SprytilePlayer player;
    private float fireDelay = 0.1f;
    private float cooldownTimer = 0f;
    private boolean shoot;
    private float bulletSpeed = 20f;    
    private float bulletSize = 0.5f;
    private Vector3f spawnPoint;
    private Vector3f direction;

    public SprytilePlayerShootControl(SprytilePlayer player) {
        this.player = player;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setSpawnPoint(Vector3f spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (player.getGame().isStarted() && !player.getGame().isPaused() && !player.getGame().isGameOver()) {
            
            if (shoot && cooldownTimer <= 0) {
                cooldownTimer = fireDelay;
                fireBullet();
                
            } else {
                cooldownTimer -= tpf;
                
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
    private void fireBullet() {
        float size = 0.5f;//FastMath.nextRandomInt(2, 10)*0.1f;
        Sprite sprite = new Sprite("bullet-player", size, size);
        sprite.setImage("Textures/ball.png");
        sprite.setLocalTranslation(spawnPoint);
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.6f);
        sprite.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.PremultAlpha); 
        sprite.getMaterial().setColor("Color", ColorUtils.rgb(250, 100, 100));


        RigidBodyControl rbc = new RigidBodyControl(new CircleCollisionShape(sprite.getWidth()/2f), 1);
        rbc.setFriction(0.1f);
        rbc.setRestitution(0.2f);
        rbc.setGravityScale(0.6f);
        rbc.setPhysicLocation(spawnPoint);
        sprite.addControl(rbc);
        player.getGame().addBullet(rbc);
        
        sprite.addControl(new SprytileBulletControl((SprytileGame)player.getGame()));
        
        rbc.setLinearVelocity(bulletSpeed*direction.x, bulletSpeed*direction.y);
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

}
