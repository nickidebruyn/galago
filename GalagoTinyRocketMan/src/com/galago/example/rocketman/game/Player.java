/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.rocketman.game;

import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author nicki
 */
public class Player extends SimplePhysics2DPlayer implements PhysicsTickListener {

    private AnimatedSprite model;
    private RigidBodyControl rbc;
    private float moveSpeed = 0.1f;
    private float size = 2f;
    private ColorRGBA color;
    private boolean flyLeft = false;
    private boolean flyRight = false;
    private float flyforce = 5f;

    public Player(SimplePhysics2DGame physicsGame) {
        super(physicsGame);
    }

    @Override
    protected void init() {
        lives = 1;

        model = new AnimatedSprite("ironman", size, size, 4, 4, 0);
        model.setMaterial(game.getBaseApplication().getModelManager().getMaterial("Materials/ironman.j3m"));
        playerNode.attachChild(model);
        game.getBaseApplication().fixFlatTexture(model.getMaterial().getTextureParam("ColorMap"));
        
        model.addAnimation(new Animation("idle", 0, 3, 15));
        model.addAnimation(new Animation("fly", 4, 7, 15));

        rbc = new RigidBodyControl(new CircleCollisionShape(size * 0.4f), 1);
        playerNode.addControl(rbc);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);

        model.play("idle", true, false, true);
    }

    @Override
    public Vector3f getPosition() {
        return rbc.getPhysicLocation().clone();
    }

    @Override
    public void doDie() {
        //TODO
//        game.getBaseApplication().getEffectManager().prepareColor(new ColorRGBA(color.r, color.g, color.b, 1f), 
//                new ColorRGBA(color.r, color.g, color.b, 0.1f));
//        game.getBaseApplication().getEffectManager().doEffect("player-kill", rbc.getPhysicLocation().clone());
        playerNode.removeFromParent();
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);
    }

    @Override
    public void start() {
        rbc.setGravityScale(1f);
        rbc.setFriction(0.0f);
        rbc.setRestitution(0.0f);

    }

    @Override
    protected float getSize() {
        return 1f;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        
        if (flyRight) {
            rbc.setLinearVelocity(flyforce*1.2f, flyforce);
            
        } else if (flyLeft) {
            rbc.setLinearVelocity(-flyforce*1.2f, flyforce);
            
        }

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
//        Debug.log("Update: " + tpf);
        rbc.setPhysicRotation(0);
        rbc.setAngularVelocity(0);

    }

    public void flyRight() {
        this.flyRight = true;
        this.flyLeft = false;
        this.model.play("fly", true, false, true);
        this.model.flipHorizontal(false);
        game.getBaseApplication().getSoundManager().playMusic("fly");
        
    }
    
    public void flyLeft() {
        this.flyLeft = true;
        this.flyRight = false;
        this.model.play("fly", true, false, true);
        this.model.flipHorizontal(true);
        game.getBaseApplication().getSoundManager().playMusic("fly");
        
    }
    
    public void flyStop() {
        this.flyLeft = false;
        this.flyRight = false;
        this.model.play("idle", true, false, true);
//        this.model.flipHorizontal(true);
        game.getBaseApplication().getSoundManager().pauseMusic("fly");
        
    }
}
