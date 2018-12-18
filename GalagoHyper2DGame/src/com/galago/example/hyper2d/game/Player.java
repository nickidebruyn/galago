/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper2d.game;

import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.PyramidCollisionShape;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.math.Vector3f;

/**
 *
 * @author nicki
 */
public class Player extends SimplePhysics2DPlayer implements PhysicsTickListener {

    private Sprite model;
    private RigidBodyControl rbc;
    private float moveSpeed = 0.1f;
    private PlayerShootControl playerShootControl;

    public Player(SimplePhysics2DGame physicsGame) {
        super(physicsGame);
    }

    @Override
    protected void init() {
        lives = 10;

        model = SpriteUtils.addSprite(playerNode, 1, 1);
        model.setImage("Textures/player.png");
        model.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);

        rbc = new RigidBodyControl(new PyramidCollisionShape(1, 1), 1);
        playerNode.addControl(rbc);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);

        playerShootControl = new PlayerShootControl(this);
        playerNode.addControl(playerShootControl);
    }

    @Override
    public Vector3f getPosition() {
        return rbc.getPhysicLocation().clone();
    }

    @Override
    public void doDie() {
        //TODO
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);
    }

    @Override
    public void start() {
        rbc.setGravityScale(0);
        rbc.setFriction(0.0f);
        rbc.setRestitution(0.0f);

    }

    @Override
    protected float getSize() {
        return 1f;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
//        Debug.log("Update: " + tpf);
        rbc.setPhysicRotation(0);
        rbc.setAngularVelocity(0);

    }

    public void movePlayerToTarget(Vector3f targetPosition) {
        rbc.setPhysicLocation(rbc.getPhysicLocation().interpolateLocal(targetPosition, moveSpeed));
        
    }

    public void shoot(boolean shoot) {
        playerShootControl.setShoot(shoot);
        
    }
}
